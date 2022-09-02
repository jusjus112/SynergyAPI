package usa.synergy.utilities.service.threading;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Same as a SimpleFuture, however designed for boolean, allows us to be
 * able to parse more values like exceptions, reasons for failures and stuff
 * in the future, with the plans that I have changes for database API and
 * stuffs.
 *
 * If the exception supplied is null, then the success future completed
 * successfully, if the exception is not null, the resultant exception
 * is the cause of a failure in the execution of a process.
 *
 * SimpleFuture explanation:
 * Simplifies getting stuff that requires database calls, opens up
 * the option to get the result in the main thread (mainly for bukkit)
 *
 * Also reduces some strange java behavior where execution may
 * not be 100% clear.
 * Sync: Main bukkit (bungee doesn't really have one) thread.
 * Async: A thread from a specified Executor or the default Java CompletableFuture thread pool.
 */
public class SynergySuccessFuture {

    private static final SynergySuccessFuture COMPLETED_SUCCESSFUL = new SynergySuccessFuture(CompletableFuture.completedFuture(null));

    private final SynergyFuture<Void> future;

    public static SynergySuccessFuture createInstance(){
        return new SynergySuccessFuture(new CompletableFuture<>());
    }

    public static SynergySuccessFuture completedSuccessful(){
        return COMPLETED_SUCCESSFUL;
    }

    public static SynergySuccessFuture completedUnsuccessful(Throwable throwable){
        CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        return new SynergySuccessFuture(future);
    }

    public static SynergySuccessFuture join(SynergySuccessFuture... futures){
        return join(Arrays.stream(futures));
    }

    public static SynergySuccessFuture join(Collection<SynergySuccessFuture> futures){
        return join(futures.stream());
    }

    private static SynergySuccessFuture join(Stream<SynergySuccessFuture> futures) {
        SynergyFuture<?>[] simpleFutures = futures.map(future -> future.future).toArray(
            SynergyFuture[]::new);
        SynergySuccessFuture newFuture = SynergySuccessFuture.createInstance();
        SynergyFuture.allComplete(simpleFutures).thenSameThread(finished -> newFuture.complete());
        return newFuture;
    }

    public static SynergySuccessFuture compute(Runnable runnable){
        return SynergyFuture.<Void>compute(() -> {
            runnable.run();
            return null;
        }).toSuccessFuture();
    }

    private SynergySuccessFuture(CompletableFuture<Void> future){
        this.future = SynergyFuture.of(future);
    }

    // ------------------------------------------------------------------------

    /**
     * For information on what thread stuff is run in please refer to SimpleFuture.
     *
     * get will give you a throwable. If the throwable is null, the process succeeded
     */
    public void getAsync(Consumer<Throwable> consumer){
        future.getAsync((aVoid, throwable) -> consumer.accept(throwable));
    }

    public void getAsync(Consumer<Throwable> consumer, Executor executor){
        future.getAsync((aVoid, throwable) -> consumer.accept(throwable), executor);
    }

    public void getAsync(Consumer<Throwable> consumer, long timeout, TimeUnit unit){
        future.getAsync((aVoid, throwable) -> consumer.accept(throwable), timeout, unit);
    }

    public void getAsync(Consumer<Throwable> consumer, long timeout, TimeUnit unit, Executor executor){
        future.getAsync((aVoid, throwable) -> consumer.accept(throwable), timeout, unit, executor);
    }

    public void getSync(Consumer<Throwable> consumer){
        future.getSync((aVoid, throwable) -> consumer.accept(throwable));
    }

    public void getSync(Consumer<Throwable> consumer, long timeout, TimeUnit unit){
        future.getSync((aVoid, throwable) -> consumer.accept(throwable), timeout, unit);
    }

    public void getSameThread(Consumer<Throwable> consumer){
        future.getSameThread((aVoid, throwable) -> consumer.accept(throwable));
    }

    // ------------------------------------------------------------------------

    /**
     * For infomation on what thread stuff is run in please refer to SimpleFuture.
     *
     * getIfErrored will give you a throwable. This will only run if an error has occured
     */
    public SynergySuccessFuture getIfErroredAsync(Consumer<Throwable> consumer){
        future.getAsync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        });
        return this;
    }

    public SynergySuccessFuture getIfErroredAsync(Consumer<Throwable> consumer, Executor executor){
        future.getAsync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        }, executor);
        return this;
    }

    public SynergySuccessFuture getIfErroredAsync(Consumer<Throwable> consumer, long timeout, TimeUnit unit){
        future.getAsync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        }, timeout, unit);
        return this;
    }

    public SynergySuccessFuture getIfErroredAsync(Consumer<Throwable> consumer, long timeout, TimeUnit unit, Executor executor){
        future.getAsync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        }, timeout, unit, executor);
        return this;
    }

    public SynergySuccessFuture getIfErroredSync(Consumer<Throwable> consumer){
        future.getSync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        });
        return this;
    }

    public SynergySuccessFuture getIfErroredSync(Consumer<Throwable> consumer, long timeout, TimeUnit unit){
        future.getSync((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        }, timeout, unit);
        return this;
    }

    public SynergySuccessFuture getIfErroredSameThread(Consumer<Throwable> consumer){
        future.getSameThread((aVoid, throwable) -> {
            if (throwable != null)
                consumer.accept(throwable);
        });
        return this;
    }

    // ------------------------------------------------------------------------

    /**
     * For infomation on what thread stuff is run in please refer to SimpleFuture.
     *
     * getIfSucceeded will run if your completable future succeeds
     *
     * The success future this returns is a combination of this future, and the
     * runnable supplied. if this has failed, the runnable will no run, and a copy
     * of this will be returned. If this has succeeded, and the runnable fails, a
     * success future will be returned containing the exception that caused the
     * runnable to fail.
     */
    public SynergySuccessFuture getIfSucceededAsync(Runnable runnable){
        return getIfSucceededAsync(runnable, SynergyThreads.SERVICE);
    }

    public SynergySuccessFuture getIfSucceededAsync(Runnable runnable, Executor executor){
        SynergySuccessFuture successFuture = createInstance();
        future.getAsync((aVoid, throwable) -> {
            handleSucceededRunnable(runnable, throwable, successFuture);
        }, executor);
        return successFuture;
    }

    public SynergySuccessFuture getIfSucceededAsync(Runnable runnable, long timeout, TimeUnit unit){
        return getIfSucceededAsync(runnable, timeout, unit, SynergyThreads.SERVICE);
    }

    public SynergySuccessFuture getIfSucceededAsync(Runnable runnable, long timeout, TimeUnit unit, Executor executor){
        SynergySuccessFuture successFuture = createInstance();
        future.getAsync((aVoid, throwable) -> {
            handleSucceededRunnable(runnable, throwable, successFuture);
        }, timeout, unit, executor);
        return successFuture;
    }

    public SynergySuccessFuture getIfSucceededSync(Runnable runnable){
        SynergySuccessFuture successFuture = createInstance();
        future.getSync((aVoid, throwable) -> {
            handleSucceededRunnable(runnable, throwable, successFuture);
        });
        return successFuture;
    }

    public SynergySuccessFuture getIfSucceededSync(Runnable runnable, long timeout, TimeUnit unit){
        SynergySuccessFuture successFuture = createInstance();
        future.getSync((aVoid, throwable) -> {
            handleSucceededRunnable(runnable, throwable, successFuture);
        }, timeout, unit);
        return successFuture;
    }

    public SynergySuccessFuture getIfSucceededSameThread(Runnable runnable){
        SynergySuccessFuture successFuture = createInstance();
        future.getSameThread((aVoid, throwable) -> {
            handleSucceededRunnable(runnable, throwable, successFuture);
        });
        return successFuture;
    }

    private void handleSucceededRunnable(Runnable runnable, Throwable result, SynergySuccessFuture failureCatcher){
        if (result == null)
            try {
                runnable.run();
                failureCatcher.complete();
            } catch (Throwable e){
                failureCatcher.completeExceptionally(e);
            }
        else
            failureCatcher.completeExceptionally(result);
    }

    // ------------------------------------------------------------------------

    /**
     * Runs if the process success, and can be used to generate another
     * value based of the success.
     */
    public <U> SynergyFuture<U> thenAsync(Supplier<U> supplier){
        return future.thenAsync(aVoid -> supplier.get());
    }

    public <U> SynergyFuture<U> thenAsync(Supplier<U> supplier, Executor executor){
        return future.thenAsync(aVoid -> supplier.get(), executor);
    }

    public <U> SynergyFuture<U> thenSync(Supplier<U> supplier){
        return future.thenSync(aVoid -> supplier.get());
    }

    public <U> SynergyFuture<U> thenSync(Supplier<U> supplier, long timeout, TimeUnit unit){
        return future.thenSync(aVoid -> supplier.get(), timeout, unit);
    }

    public <U> SynergyFuture<U> thenSameThread(Supplier<U> supplier){
        return future.thenSameThread(aVoid -> supplier.get());
    }

    public SynergyFuture<Void> asSimpleFuture(){
        return future;
    }

    public CompletableFuture<Void> asCompletableFuture(){
        return future.getFuture();
    }

    public boolean cancel(){
        return future.cancel(true);
    }

    public boolean isCancelled(){
        return future.isCancelled();
    }

    public boolean isDone(){
        return future.isDone();
    }

    /**
     * Blocks the thread. If it throws an exception, it didn't work.
     */
    public void attempt() throws ExecutionException, InterruptedException{
        future.get();
    }

    /**
     * Blocks the thread. If it throws an exception, it didn't work.
     */
    public void attempt(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException{
        future.get(timeout, unit);
    }

    /**
     * Blocks the thread. If it throws an exception, it didn't work.
     */
    public void attemptUnchecked(){
        future.getUnchecked();
    }

    /**
     * Blocks the thread. If it throws an exception, it didn't work.
     */
    public void attemptUnchecked(long timeout, TimeUnit unit){
        future.getUnchecked(timeout, unit);
    }

    /**
     * Blocks to get, if it throws an exception, it prints it out
     * and returns false.
     */
    public boolean getSucceeded(){
        try {
            future.get();
            return true;
        } catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean getUnchecked(long timeout, TimeUnit unit){
        try {
            future.get(timeout, unit);
            return true;
        } catch (InterruptedException | ExecutionException | TimeoutException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean complete(){
        return future.complete(null);
    }

    public boolean completeExceptionally(Throwable throwable){
        return future.completeExceptionally(throwable);
    }

    public SynergySuccessFuture concat(SynergySuccessFuture future){
        return join(this, future);
    }

    /**
     * Sends the error message to the user if an exception occurs
     * @return Returns this so you can chain more calls onto this object.
     */
//    public <U> SynergySuccessFuture errorMessage(U user, GameString<U> message, GamePlatform<U> platform){
//        getSameThread(throwable -> {
//            if (throwable != null){
//                platform.sendChatMessage(user, message.toStringResult(user, s -> null), null);
//            }
//        });
//        return this;
//    }

//    public <U> SynergySuccessFuture errorMessage(U user, String string, GamePlatform<U> platform){
//        return errorMessage(user, GameString.constant(string), platform);
//    }

    /**
     * Prints the exception to the console if an exception gets thrown
     */
    public SynergySuccessFuture printStackTrace(){
        getSameThread(throwable -> {
            if (throwable != null){
                throwable.printStackTrace();
            }
        });
        return this;
    }

    public SynergySuccessFuture finishExceptionallyOnException(SynergyFuture<?> simpleFuture){
        getSameThread(throwable -> {
            if (throwable != null){
                simpleFuture.completeExceptionally(throwable);
            }
        });
        return this;
    }

    public SynergySuccessFuture finishExceptionallyOnException(CompletableFuture<?> completableFuture){
        getSameThread(throwable -> {
            if (throwable != null){
                completableFuture.completeExceptionally(throwable);
            }
        });
        return this;
    }

    public SynergySuccessFuture finishExceptionallyOnException(SynergySuccessFuture simpleFuture){
        getSameThread(throwable -> {
            if (throwable != null){
                simpleFuture.completeExceptionally(throwable);
            }
        });
        return this;
    }

    /**
     * Joins the supplied future in the function to this future.
     * The function will be called wheather this fails or succeeds.
     */
    public SynergySuccessFuture thenJoinSameThread(Function<Throwable, SynergySuccessFuture> function){
        SynergySuccessFuture future = SynergySuccessFuture.createInstance();
        getSameThread(throwable -> {
            SynergySuccessFuture f = function.apply(throwable);
            if (f == null){
                future.completeExceptionally(new NullPointerException("Future supplied by function is null!"));
                return;
            }
            f.getSameThread(throwable1 -> {
                if (throwable1 == null)
                    future.complete();
                else
                    future.completeExceptionally(throwable1);
            });
        });
        return future;
    }

    /**
     * Joins the supplied future in the function to this future.
     * The supplier will only be called if the future succeeds.
     */
    public SynergySuccessFuture thenJoinSameThread(Supplier<SynergySuccessFuture> supplier){
        return thenJoinSameThread(throwable -> {
            if (throwable == null)
                return supplier.get();
            else
                return completedUnsuccessful(throwable);
        });
    }
}
