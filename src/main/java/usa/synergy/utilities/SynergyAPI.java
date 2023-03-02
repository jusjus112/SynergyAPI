package usa.synergy.utilities;

import java.util.function.BiFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.command.impl.CommandManager;
import usa.synergy.utilities.assets.cooldown.CooldownManager;
import usa.synergy.utilities.assets.file.FileController;
import usa.synergy.utilities.assets.gui.impl.GuiManager;
import usa.synergy.utilities.libraries.user.api.SynergyUser;
import usa.synergy.utilities.libraries.user.impl.UserHandler;
import usa.synergy.utilities.service.sql.DatabaseManager;
import usa.synergy.utilities.service.threading.runnable.RunnableProvider;
import usa.synergy.utilities.assets.version.VersionManager;
import usa.synergy.utilities.libraries.PluginLoader;
import usa.synergy.utilities.libraries.dependency.DependencyProvider;
import usa.synergy.utilities.libraries.files.FileProvider;
import usa.synergy.utilities.utlities.SynergyLogger;

@Getter
public class SynergyAPI<A extends JavaPlugin> extends PluginLoader<A>{

  @Deprecated
  @Getter
  private static SynergyAPI<?> instance;
  private final GuiManager guiManager;
  private final CommandManager commandManager;
  private final VersionManager versionManager;
  private static RunnableProvider<?> runnableProvider;
  private final FileController fileController;
  private final DatabaseManager databaseManager;

  // New APIS
  private final CooldownManager<A> cooldownManager;
  private final FileProvider<A> fileProvider;
  private final DependencyProvider<A> dependencyProvider;

  // Setters for APIs and modules that support custom implementations
  private final UserHandler<A, ? extends SynergyUser<? extends Player>> userProvider;

  public SynergyAPI(A javaPlugin,
      UserHandler<A, ? extends SynergyUser<? extends Player>> userProvider,
      DatabaseManager databaseManager
  ) {
    super(javaPlugin);

    // Support the old API
    instance = this;

    // Show the logo and information, because it's epic.
    SynergyLogger.info("Using SynergyAPI v" + javaPlugin.getDescription().getVersion());
//    Logo.display();
//    Logo.showInformation();

    // Initialize the runner
    runnableProvider = new RunnableProvider<>(javaPlugin);
    this.databaseManager = databaseManager;

    // Initialize the modules
    this.guiManager = new GuiManager(javaPlugin);
    this.commandManager = new CommandManager(javaPlugin);
    this.versionManager = new VersionManager(javaPlugin);
    this.fileController = new FileController(javaPlugin);
    this.cooldownManager = new CooldownManager<>(javaPlugin);
    this.fileProvider = new FileProvider<>(javaPlugin);
    this.dependencyProvider = new DependencyProvider<>(javaPlugin);
    this.userProvider = userProvider;

//    new SQLConnector<>(SQLConfig.fromProperties()).initialize(javaPlugin);
//    getSQLController().query(SelectQuery.class)
//      .columns("")
//      .results(resultSet -> {
//
//      }
//    );
  }

  public static RunnableProvider<? extends JavaPlugin> runner(){
    Validate.notNull(runnableProvider, "Creating runner while API not initialized. Are you drunk?");
    return runnableProvider;
  }

}
