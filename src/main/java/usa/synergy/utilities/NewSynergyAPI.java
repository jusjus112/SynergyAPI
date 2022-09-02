package usa.synergy.utilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.user.api.SynergyUser;
import usa.synergy.utilities.libraries.user.impl.UserHandler;

public class NewSynergyAPI<A extends JavaPlugin, P extends SynergyPlugin<A>> {

  private final A javaPlugin;
  private P plugin;

  private UserHandler<A, ? extends SynergyUser<? extends Player>> userProvider;

  public NewSynergyAPI(A javaPlugin) {
    this.javaPlugin = javaPlugin;
  }

  public P initializePlugin(P plugin){
    this.plugin = plugin;
    plugin.init();
    return plugin;
  }

  public P deInitializePlugin(P plugin){
    plugin.deInit();
    return plugin;
  }

}
