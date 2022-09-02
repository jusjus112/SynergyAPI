package usa.synergy.utilities.assets.events;

import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public abstract class SynergyListener implements Listener {

  private final JavaPlugin plugin;

  public SynergyListener(JavaPlugin plugin) {
    this.plugin = plugin;
  }

}
