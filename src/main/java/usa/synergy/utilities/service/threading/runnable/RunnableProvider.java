package usa.synergy.utilities.service.threading.runnable;

import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;

/**
 *
 * @param <A>
 */
public class RunnableProvider<A extends JavaPlugin> extends Module<A> {

  protected final Map<String, SynergyRunnableExecutor<A>> RUNNABLES = Maps.newConcurrentMap();

  public RunnableProvider(A plugin) {
    super(plugin, "Runnable Manager");
  }

  public void runTask(String name, SynergyRunnable synergyRunnable) {
    createRunnable(name, synergyRunnable).runTask(getLoader());
  }

  public void runTaskAsynchronously(String name, SynergyRunnable synergyRunnable) {
    createRunnable(name, synergyRunnable).runTaskAsynchronously(getLoader());
  }

  public void runTaskLater(String name, SynergyRunnable synergyRunnable, long time) {
    createRunnable(name, synergyRunnable).runTaskLater(getLoader(), time);
  }

  public void runTaskLaterAsynchronously(String name, SynergyRunnable synergyRunnable, long time) {
    createRunnable(name, synergyRunnable).runTaskLaterAsynchronously(getLoader(), time);
  }

  public void runTaskTimer(String name, SynergyRunnable synergyRunnable, long delay, long period) {
    createRunnable(name, synergyRunnable).runTaskTimer(getLoader(), delay, period);
  }

  public void runTaskTimerAsynchronously(String name, SynergyRunnable synergyRunnable, long delay,
      long period) {
    createRunnable(name, synergyRunnable).runTaskTimerAsynchronously(getLoader(), delay, period);
  }

  private SynergyRunnableExecutor<A> createRunnable(String name, SynergyRunnable synergyRunnable) {
    SynergyRunnableExecutor<A> runnable = new SynergyRunnableExecutor<>(getLoader(),
        this, name, synergyRunnable);

    RUNNABLES.put(name, runnable);

    return runnable;
  }

  public Map<String, SynergyRunnableExecutor<A>> getActiveRunnables() {
    return RUNNABLES;
  }

}