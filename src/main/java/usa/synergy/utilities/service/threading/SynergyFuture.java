package usa.synergy.utilities.service.threading;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import usa.synergy.utilities.SynergyAPI;
import usa.synergy.utilities.service.threading.runnable.SynergyRunnable;

/**
 * Simplifies getting stuff that requires database calls, opens up
 * the option to get the result in the main thread (mainly for bukkit)
 *
 * Also reduces some strange java behavior where execution may
 * not be 100% clear.
 * Sync: Main bukkit (bungee doesn't really have one) thread.
 * Async: A thread from a specified Executor or the default Java CompletableFuture thread pool.
 */
@Slf4j
public class SynergyFuture<T> implements Future<T>{

  // Maximum number of milliseconds a future call is allowed to take before logging an error
  private static final long ALERT_THRESHOLD = 20;
  // Sample of future calls to log time metrics data for, out of 100
  private static final long ALERT_SAMPLE = 10;

  private final CompletableFuture<T> future;

  public static <T> SynergyFuture<T> createInstance(){
    return of(new CompletableFuture<>());
  }

  public static SynergyFuture<Void> allComplete(SynergyFuture<?>... simpleFutures){
    return allComplete(Arrays.stream(simpleFutures));
  }

  /**
   * Sometimes these work, sometimes these are just a pain, also check
   * allCompleted too see if it works better for your case
   */
  public static SynergyFuture<Void> allComplete(Collection<SynergyFuture<?>> simpleFutures){
    return allComplete(simpleFutures.stream());
  }

  public static SynergyFuture<Void> allComplete(Stream<SynergyFuture<?>> simpleFutures){
    return SynergyFuture.of(
        CompletableFuture.allOf(
            simpleFutures.map(SynergyFuture::getFuture).toArray((IntFunction<CompletableFuture<?>[]>) CompletableFuture[]::new)
        )
    );
  }

  public static <T> SynergyFuture<Void> allCompleted(Collection<SynergyFuture<T>> simpleFutures){
    return allCompleted(simpleFutures.stream());
  }

  public static <T> SynergyFuture<Void> allCompleted(Stream<SynergyFuture<T>> simpleFutures){
    return SynergyFuture.of(
        CompletableFuture.allOf(
            simpleFutures.map(SynergyFuture::getFuture).toArray((IntFunction<CompletableFuture<?>[]>) CompletableFuture[]::new)
        )
    );
  }

  /**
   * Hate java generics with a passion. Anyone got any way to improve the fact that
   * I have to have 2 methods, one with T and one with ? extends T :(
   */
  public static <T> SynergyFuture<Void> allCompletedExt(Collection<SynergyFuture<? extends T>> simpleFutures){
    return allCompletedExt(simpleFutures.stream());
  }

  public static <T> SynergyFuture<Void> allCompletedExt(Stream<SynergyFuture<? extends T>> simpleFutures){
    return SynergyFuture.of(
        CompletableFuture.allOf(
            simpleFutures.map(SynergyFuture::getFuture).toArray((IntFunction<CompletableFuture<?>[]>) CompletableFuture[]::new)
        )
    );
  }

  @SuppressWarnings("Duplicates")//Stupid intellij. I couldn't figure out how to unduplicate.
  public static <A, B> SynergyFuture<Pair<A, B>> join(
      SynergyFuture<A> simpleFutureA, SynergyFuture<B> simpleFutureB){
    CompletableFuture<Pair<A, B>> completableFuture = new CompletableFuture<>();
    simpleFutureA.getSameThread(a -> {
      if (completableFuture.isDone()) return;
      if (simpleFutureB.isDone()){
        try {
          completableFuture.complete(Pair.of(a, simpleFutureB.get()));
        } catch (ExecutionException | InterruptedException e){
          completableFuture.completeExceptionally(e);
        }
      }
    }).finishExceptionallyOnException(completableFuture);
    if (completableFuture.isDone())
      return of(completableFuture);
    simpleFutureB.getSameThread(b -> {
      if (completableFuture.isDone()) return;
      if (simpleFutureA.isDone()){
        try {
          completableFuture.complete(Pair.of(simpleFutureA.get(), b));
        } catch (ExecutionException | InterruptedException e){
          completableFuture.completeExceptionally(e);
        }
      }
    }).finishExceptionallyOnException(completableFuture);
    return of(completableFuture);
  }

  public static <T> SynergyFuture<T> of(CompletableFuture<T> future){
    return new SynergyFuture<>(future);
  }

  public static <T> SynergyFuture<T> completed(T value){
    return new SynergyFuture<>(CompletableFuture.completedFuture(value));
  }

  public static <T> SynergyFuture<T> completedExceptionally(Throwable throwable){
    SynergyFuture<T> future = createInstance();
    future.completeExceptionally(throwable);
    return future;
  }

  public static <T> SynergyFuture<T> compute(Supplier<T> supplier){
    return compute(supplier, false);
  }

  public static <T> SynergyFuture<T> compute(Supplier<T> supplier, boolean printException){
    CompletableFuture<T> future = new CompletableFuture<>();
    SynergyThreads.SERVICE.execute(() -> {
      try {
        T value = supplier.get();
        future.complete(value);
      } catch (Throwable e){
        if (printException)
          log.error("An error occurred executing simple future runnable.", e);
        future.completeExceptionally(e);
      }
    });
    return of(future);
  }

  private SynergyFuture(CompletableFuture<T> future){
    this.future = future;
  }


  public SynergySuccessFuture getAsync(Consumer<T> consumer){
    return getAsync(consumer, SynergyThreads.SERVICE);
  }

  public SynergySuccessFuture getAsync(Consumer<T> consumer, Executor executor){
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    future.whenComplete((result, throwable) -> {
      if (throwable != null){
        successFuture.completeExceptionally(throwable);
        return;
      }
      executor.execute(() -> {
        try {
          consumer.accept(result);
          successFuture.complete();
        } catch (Throwable e){
          successFuture.completeExceptionally(e);
        }
      });
    });
    return successFuture;
  }

  public SynergySuccessFuture getAsync(Consumer<T> consumer, int timeout, TimeUnit unit){
    return getAsync(consumer, timeout, unit, SynergyThreads.SERVICE);
  }

  public SynergySuccessFuture getAsync(Consumer<T> consumer, int timeout, TimeUnit unit, Executor executor){
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    executor.execute(() -> {
      T result;
      try {
        result = future.get(timeout, unit);
      } catch (Throwable e){
        successFuture.completeExceptionally(e);
        return;
      }
      try {
        consumer.accept(result);
        successFuture.complete();
      } catch (Throwable e){
        successFuture.completeExceptionally(e);
      }
    });
    return successFuture;
  }

  protected void getAsync(BiConsumer<T, Throwable> consumer){
    future.whenCompleteAsync(consumer);
  }

  protected void getAsync(BiConsumer<T, Throwable> consumer, Executor executor){
    future.whenCompleteAsync(consumer, executor);
  }

  protected void getAsync(BiConsumer<T, Throwable> consumer, long timeout, TimeUnit unit){
    SynergyAPI.runner().runTaskAsynchronously("handleGetAsync", () ->
        handleGetAsync(consumer, timeout, unit));
  }

  protected void getAsync(BiConsumer<T, Throwable> consumer, long timeout, TimeUnit unit, Executor executor){
    executor.execute(() -> handleGetAsync(consumer, timeout, unit));
  }

  protected void handleGetAsync(BiConsumer<T, Throwable> consumer, long timeout, TimeUnit unit){
    T result;
    try {
      result = future.get(timeout, unit);
    } catch (InterruptedException | ExecutionException | TimeoutException e){
      e.printStackTrace();
      consumer.accept(null, e);
      return;
    }
    try {
      consumer.accept(result, null);
    } catch (Throwable e){
      e.printStackTrace();
    }
  }

  public <U> SynergyFuture<U> thenAsync(Function<T, U> mapper){
    return SynergyFuture.of(future.thenApplyAsync(mapper));
  }

  public <U> SynergyFuture<U> thenAsync(Function<T, U> mapper, Executor executor){
    return SynergyFuture.of(future.thenApplyAsync(mapper, executor));
  }

  private static final String THEN_SAME_THREAD = "thenSameThread";

  /**
   * For lightweight process that don't need to be in the main thread.
   * This will run in the thread that it's called from if the future
   * is completed, or will run in the thread of the future after it's
   * completed.
   */
  public <U> SynergyFuture<U> thenSameThread(Function<T, U> mapper){
    Throwable caller = randomSampleThrowable(THEN_SAME_THREAD);
    return SynergyFuture.of(future.thenApply(a -> {
      long time = caller == null ? 0 : System.currentTimeMillis();
      U b = mapper.apply(a);
      if (caller != null) {
        long timeTaken = System.currentTimeMillis() - time;
        if (timeTaken >= ALERT_THRESHOLD) {
          log.warn("", new Throwable(
              "SimpleFuture#thenSameThread call took longer than " + ALERT_THRESHOLD
                  + " milliseconds. Took " + timeTaken + "ms", caller));
        }
      }
      return b;
    }));
  }

  public <U> SynergyFuture<U> thenSync(Function<T, U> mapper){
    return thenSync(mapper, 5, TimeUnit.SECONDS);
  }

  public <U> SynergyFuture<U> thenSync(Function<T, U> mapper, long timeout, TimeUnit timeoutUnit){
    return SynergyFuture.of(future.thenApplyAsync(obj -> {
      CompletableFuture<U> future = new CompletableFuture<>();
      scheduleSync(() -> {
        try {
          future.complete(mapper.apply(obj));
        } catch (Throwable e){
          future.completeExceptionally(e);
        }
      });
      try {
        return future.get(timeout, timeoutUnit);
      } catch (InterruptedException | ExecutionException | TimeoutException e){
        throw new RuntimeException(e);
      }
    }));
  }

  protected void getSameThread(BiConsumer<T, Throwable> consumer){
    future.whenComplete(consumer);
  }

  private static final String GET_SAME_THREAD = "getSameThread";

  public SynergySuccessFuture getSameThread(Consumer<T> consumer){
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    Throwable caller = randomSampleThrowable(GET_SAME_THREAD);
    future.whenComplete((result, throwable) -> {
      long start = caller == null ? 0 : System.currentTimeMillis();
      if (throwable != null){
        successFuture.completeExceptionally(throwable);
        return;
      }
      try {
        consumer.accept(result);
        successFuture.complete();
      } catch (Throwable e){
        successFuture.completeExceptionally(e);
      }
      if (caller != null) {
        long timeTaken = System.currentTimeMillis() - start;
        if (timeTaken >= ALERT_THRESHOLD) {
          log.warn("", new Throwable(
              "SimpleFuture#getSameThread call took longer than " + ALERT_THRESHOLD
                  + " milliseconds. Took " + timeTaken + "ms", caller));
        }
      }
    });
    return successFuture;
  }

  private static final String GET_SYNC = "getSync";

  public SynergySuccessFuture getSync(Consumer<T> consumer){
    Throwable caller = randomSampleThrowable(GET_SYNC);
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    future.whenComplete((result, throwable) -> {
      if (throwable != null){
        successFuture.completeExceptionally(throwable);
        return;
      }
      handleResultForSync(successFuture, result, consumer, caller);
    });
    return successFuture;
  }

  public SynergySuccessFuture getSync(Consumer<T> consumer, long timeout, TimeUnit unit){
    Throwable caller = randomSampleThrowable(GET_SYNC);
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    SynergyThreads.SERVICE.execute(() -> {
      T result;
      try {
        result = future.get(timeout, unit);
      } catch (InterruptedException | ExecutionException | TimeoutException e){
        successFuture.completeExceptionally(e);
        return;
      }
      handleResultForSync(successFuture, result, consumer, caller);
    });
    return successFuture;
  }

  private static final String HANDLE_RESULT_FOR_SYNC = "handleResultForSync";

  private void handleResultForSync(SynergySuccessFuture successFuture, T result, Consumer<T> consumer, Throwable caller){
    scheduleSync(() ->  {
      try {
        long start = caller == null ? 0 : System.currentTimeMillis();
        consumer.accept(result);
        successFuture.complete();
        if (caller != null) {
          long timeTaken = System.currentTimeMillis() - start;
          if (timeTaken >= ALERT_THRESHOLD) {
            log.warn("", new Throwable(
                "SimpleFuture#handleResultForSync call took longer than "
                    + ALERT_THRESHOLD + " milliseconds. Took " + timeTaken + "ms",
                caller));
          }
        }
      } catch (Throwable e){
        successFuture.completeExceptionally(e);
      }
    });
  }

  private void scheduleSync(SynergyRunnable runnable){
//        if (game == null){
//            //Exceptions are normally swallowed by this point. - Print directly.
//            if (GameFrameworkCoreImpl.getInstance() == null || GameFrameworkCoreImpl.getInstance().getSchedulingHandler() == null) {
//                log.error("ERROR: Scheduling handler was not initialised, cannot call getSync!", new Throwable());
//                return;
//            }
//            GameFrameworkCoreImpl.getInstance().getSchedulingHandler().runTask(null, runnable);
//        } else {
    SynergyAPI.runner().runTask("scheduleSync", runnable);
//        }
  }

  protected void getSync(BiConsumer<T, Throwable> consumer){
    future.whenComplete((t, throwable) ->
        scheduleSync(() -> consumer.accept(t, throwable)));
  }

  protected void getSync(BiConsumer<T, Throwable> consumer, long timeout, TimeUnit unit){
    SynergyThreads.SERVICE.execute(() -> {
      T result;
      try {
        result = future.get(timeout, unit);
      } catch (InterruptedException | ExecutionException | TimeoutException e){
        e.printStackTrace();
        scheduleSync(() -> consumer.accept(null, e));
        return;
      }
      scheduleSync(() -> {
        try {
          consumer.accept(result, null);
        } catch (Throwable e){
          e.printStackTrace();
        }
      });
    });
  }

  private static final String THEN_JOIN_SAME_THREAD = "thenJoinSameThread";

  public <U> SynergyFuture<U> thenJoinSameThread(Function<T, SynergyFuture<U>> mapper){
    if (isDone()){
      if (!isCancelled()){
        try {
          return mapper.apply(get());
        } catch (ExecutionException | InterruptedException e){
          return SynergyFuture.completedExceptionally(e);
        }
      }
    }
    Throwable caller = randomSampleThrowable(THEN_JOIN_SAME_THREAD);
    return SynergyFuture.of(future.thenCompose(a -> {
      long time = caller == null ? 0 : System.currentTimeMillis();
      SynergyFuture<U> b = mapper.apply(a);
      if (caller != null) {
        long timeTaken = System.currentTimeMillis() - time;
        if (timeTaken >= ALERT_THRESHOLD) {
          log.warn("", new Throwable(
              "SimpleFuture#thenJoinSameThread call took longer than " + ALERT_THRESHOLD
                  + " milliseconds. Took " + timeTaken + "ms", caller));
        }
      }
      return b.getFuture();
    }));
  }

  private static final String THEN_JOIN_SYNC = "thenJoinSync";

  public <U> SynergyFuture<U> thenJoinSync(Function<T, SynergyFuture<U>> mapper){
    Throwable caller = randomSampleThrowable(THEN_JOIN_SYNC);
    SynergyFuture<SynergyFuture<U>> futureFuture = thenSync(a -> {
      long time = caller == null ? 0 : System.currentTimeMillis();
      SynergyFuture<U> b = mapper.apply(a);
      if (caller != null) {
        long timeTaken = System.currentTimeMillis() - time;
        if (timeTaken >= ALERT_THRESHOLD) {
          log.warn("", new Throwable(
              "SimpleFuture#thenJoinSync call took longer than " + ALERT_THRESHOLD
                  + " milliseconds. Took " + timeTaken + "ms", caller));
        }
      }
      return b;
    });
    if (futureFuture.isDone()){
      try {
        return futureFuture.get();
      } catch (ExecutionException | InterruptedException e){
        return SynergyFuture.completedExceptionally(e);
      }
    } else {
      return futureFuture.thenAsync(future2 -> {
        try {
          return future2.get();
        } catch (ExecutionException | InterruptedException e){
          throw new RuntimeException(e);
        }
      });
    }
  }

  public SynergySuccessFuture thenJoinSuccessSameThread(Function<T, SynergySuccessFuture> mapper){
    if (isDone()){
      if (!isCancelled()){
        try {
          return mapper.apply(get());
        } catch (ExecutionException | InterruptedException e){
          return SynergySuccessFuture.completedUnsuccessful(e);
        }
      }
    }
    SynergySuccessFuture successFuture = SynergySuccessFuture.createInstance();
    getSameThread(result -> {
      SynergySuccessFuture newFuture = mapper.apply(result);
      newFuture.getSameThread(successThrowable -> {
        if (successThrowable != null){
          successFuture.completeExceptionally(successThrowable);
          return;
        }
        successFuture.complete();
      });
    }).finishExceptionallyOnException(successFuture);
    return successFuture;
  }

  public Optional<T> potentiallyCompletedSafe() {
    try {
      return Optional.ofNullable(future.getNow(null));
    } catch (CancellationException | CompletionException ex) {
      return Optional.empty();
    }
  }

  public Optional<T> potentiallyCompleted() {
    return Optional.ofNullable(future.getNow(null));
  }

  /**
   * Feel free to use, this class just makes things easier.
   */
  public CompletableFuture<T> getFuture(){
    return future;
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning){
    return future.cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled(){
    return future.isCancelled();
  }

  @Override
  public boolean isDone(){
    return future.isDone();
  }

  public boolean isDoneSuccessfully(){
    return future.isDone() && !future.isCancelled() && !future.isCompletedExceptionally();
  }

  public boolean isCancelledOrCompletedExceptionally(){
    return future.isCancelled() || future.isCompletedExceptionally();
  }

  public boolean isCompletedExceptionally(){
    return future.isCompletedExceptionally();
  }

  @Override
  public T get() throws ExecutionException, InterruptedException{
    return future.get();
  }

  @Override
  public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException{
    return future.get(timeout, unit);
  }

  public T getUnchecked(){
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e){
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public T getUnchecked(long timeout, TimeUnit unit){
    try {
      return future.get(timeout, unit);
    } catch (InterruptedException | ExecutionException | TimeoutException e){
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public boolean complete(T value){
    return future.complete(value);
  }

  public boolean completeExceptionally(Throwable throwable){
    return future.completeExceptionally(throwable);
  }

  public SynergySuccessFuture toSuccessFuture(){
    return getSameThread(aVoid -> {});
  }

  private Throwable randomSampleThrowable(String name) {
    if (ThreadLocalRandom.current().nextInt(100) <= ALERT_SAMPLE) return new Throwable("SimpleFuture#" + name + " call stack");
    return null;
  }
}
