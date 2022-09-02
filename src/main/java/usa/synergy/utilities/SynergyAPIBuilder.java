package usa.synergy.utilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.PluginLoader;
import usa.synergy.utilities.libraries.user.api.SynergyUser;
import usa.synergy.utilities.libraries.user.impl.UserHandler;

public class SynergyAPIBuilder<A extends JavaPlugin> extends PluginLoader<A> {

  private UserHandler<A, ? extends SynergyUser<? extends Player>> userProvider;

  public SynergyAPIBuilder(A loader) {
    super(loader);
  }

  public <U extends SynergyUser<? extends Player>> SynergyAPIBuilder<A> userProvider(UserHandler<A, U> userProvider){
    this.userProvider = userProvider;
    return this;
  }

  public SynergyAPI<A> buildAPI() {
    return new SynergyAPI<>(
        getLoader(),
        userProvider == null ? new UserHandler<A, SynergyUser<Player>>(getLoader()) : userProvider
    );
  }

}
