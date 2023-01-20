package usa.synergy.utilities;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.PluginLoader;
import usa.synergy.utilities.libraries.user.api.SynergyUser;
import usa.synergy.utilities.libraries.user.impl.UserHandler;
import usa.synergy.utilities.service.SQLService;
import usa.synergy.utilities.service.sql.DatabaseManager;

public class SynergyAPIBuilder<A extends JavaPlugin> extends PluginLoader<A> {

  private UserHandler<A, ? extends SynergyUser<? extends Player>> userProvider;
  private DatabaseManager databaseManager;

  public SynergyAPIBuilder(A loader) {
    super(loader);
  }

  public <U extends SynergyUser<? extends Player>> SynergyAPIBuilder<A> userProvider(UserHandler<A, U> userProvider){
    this.userProvider = userProvider;
    return this;
  }

  @Deprecated
  public <U extends SynergyUser<? extends Player>> SynergyAPIBuilder<A> databaseProvider(SQLService sqlService){
    this.databaseManager = new DatabaseManager(sqlService);
    return this;
  }

  public SynergyAPI<A> buildAPI() {
    return new SynergyAPI<>(
        getLoader(),
        userProvider == null ? new UserHandler<A, SynergyUser<Player>>(getLoader()) : userProvider,
        databaseManager
    );
  }

}
