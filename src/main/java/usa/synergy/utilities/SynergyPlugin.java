package usa.synergy.utilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.PluginLoader;
import usa.synergy.utilities.libraries.user.api.SynergyUser;
import usa.synergy.utilities.libraries.user.impl.UserHandler;

public abstract class SynergyPlugin<A extends JavaPlugin> extends PluginLoader<A> {

  public SynergyPlugin(A loader) {
    super(loader);
  }

  protected  <U extends SynergyUser<? extends Player>> UserHandler<A, U> constructUserProvider(){
    return (UserHandler<A, U>) new UserHandler<A, SynergyUser<Player>>(getLoader());
  }

  public abstract void init();
  public abstract void deInit();

}
