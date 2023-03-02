package usa.synergy.utilities.assets.events;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.PluginLoader;

@Getter
public abstract class SynergyListener<A extends JavaPlugin> extends PluginLoader<A> implements Listener{

  public SynergyListener(A loader) {
    super(loader);
  }
}
