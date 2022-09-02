package usa.synergy.utilities.service.threading.runnable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import usa.synergy.utilities.service.threading.SynergyFuture;
import usa.synergy.utilities.service.threading.SynergyThreads;

/**
 * A simplified Caching operation that doesn't refresh.
 * It only loads the data when requested and returns it
 * in SimpleFuture form.
 */
public abstract class SimpleLoader<T>{

  public static <T> SimpleLoader<T> loaded(T value){
    return new SimpleLoader<T>(){
      @Override
      public SynergyFuture<T> get(){
        return SynergyFuture.completed(value);
      }

      @Override
      protected T load(){
        return value;
      }
    };
  }

  private final AtomicBoolean loading = new AtomicBoolean(false);
  private final AtomicReference<CompletableFuture<T>> future = new AtomicReference<>(new CompletableFuture<>());

  public SynergyFuture<T> get(){
    CompletableFuture<T> future = this.future.get();
    if (!future.isDone() || future.isCompletedExceptionally()){
      synchronized (this){
        if (!this.future.get().isDone()){
          boolean loading = this.loading.get();
          if (!loading){
            this.loading.set(true);
            startLoading();
          }
        }
      }
    }
    return SynergyFuture.of(future);
  }

  private void startLoading(){
    //If something blocks the main thread for this, and we use bukkit
    //it kills the server, as async threads are called by the main thread.
    SynergyThreads.SERVICE.execute(() -> {
      try {
        T value = load();
        future.get().complete(value);
      } catch (Throwable e){
        e.printStackTrace();
        future.getAndSet(new CompletableFuture<>()).completeExceptionally(e);
      }
      loading.set(false);
    });
  }

  protected abstract T load();
}
