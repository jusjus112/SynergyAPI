package usa.synergy.utilities.service.threading.runnable;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

public class SynergyRunnableExecutor<A extends JavaPlugin> extends BukkitRunnable implements Cloneable {

  private final A plugin;
  private final RunnableProvider<A> runnableManager;
  @Getter
  private final String name;
  @Getter
  private final SynergyRunnable synergyRunnable;

  public SynergyRunnableExecutor(A plugin, RunnableProvider<A> runnableManager, String name,
      SynergyRunnable synergyRunnable) {
    this.plugin = plugin;
    this.runnableManager = runnableManager;
    this.name = name;
    this.synergyRunnable = synergyRunnable;
  }

  @Override
  public void run() {
    synergyRunnable.run();
  }

  @Override
  public synchronized void cancel() throws IllegalStateException {
    super.cancel();
    runnableManager.RUNNABLES.remove(name);
  }

  @Nullable
  public SynergyRunnableExecutor<A> clone() {
    try {
      return (SynergyRunnableExecutor<A>) super.clone();
    } catch (Exception ex) {
      return null;
    }
  }
}
