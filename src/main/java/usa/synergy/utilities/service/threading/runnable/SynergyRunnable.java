package usa.synergy.utilities.service.threading.runnable;

@FunctionalInterface
public interface SynergyRunnable {
  /**
   * When an object implementing interface {@code Runnable} is used
   * to create a thread, starting the thread causes the object's
   * {@code run} method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method {@code run} is that it may
   * take any action whatsoever.
   *
   * @see     java.lang.Thread#run()
   */
  public abstract void run();
}
