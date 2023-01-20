package usa.synergy.utilities;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.command.api.SynergyCommand;
import usa.synergy.utilities.libraries.PluginLoader;

@Getter
public abstract class Module<A extends JavaPlugin> extends PluginLoader<A> implements Listener{

  @Deprecated
  private final String name;
  private boolean disabled;
  private final List<Listener> listeners;

  protected Module(A plugin, String name) {
    super(plugin);

    this.name = name;
    this.disabled = false;
    this.listeners = Lists.newArrayList();

    getLoader().getServer().getPluginManager().registerEvents(this, getLoader());
  }

  protected Module(A plugin) {
    this(plugin, plugin.getName());
  }

  public void registerListener(Listener... listeners) {
    Arrays.stream(listeners).forEach(listener -> {
      getLoader().getServer().getPluginManager().registerEvents(listener, getLoader());
      this.listeners.add(listener);
    });
  }

  public void registerCommand(SynergyCommand... commands) {
    for (SynergyCommand command : commands) {
      SynergyAPI.getInstance().getCommandManager().registerCommand(command);
    }
  }

  public String getShortname() {
    return getName().split(" ")[0];
  }

  public void disable() {
    this.disabled = true;
    // Unregister listeners.
    this.listeners.forEach(HandlerList::unregisterAll);
  }
}
