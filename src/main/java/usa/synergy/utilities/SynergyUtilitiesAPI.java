package usa.synergy.utilities;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.command.impl.CommandManager;
import usa.synergy.utilities.assets.cooldown.CooldownManager;
import usa.synergy.utilities.assets.file.FileController;
import usa.synergy.utilities.assets.gui.impl.GuiManager;
import usa.synergy.utilities.service.threading.runnable.RunnableProvider;
import usa.synergy.utilities.assets.version.VersionManager;

@Getter
@Deprecated
public class SynergyUtilitiesAPI {

  @Getter
//  public static SynergyUtilitiesAPI api;
  private final JavaPlugin javaPlugin;
  private final GuiManager guiManager;
  private final CommandManager commandManager;
  private final VersionManager versionManager;
  private final RunnableProvider runnableManager;
  private final FileController fileController;
  private final CooldownManager cooldownManager;

  @Deprecated
  public SynergyUtilitiesAPI(JavaPlugin javaPlugin) {
//    api = this;
    this.javaPlugin = javaPlugin;

    this.guiManager = new GuiManager(javaPlugin);
    this.commandManager = new CommandManager(javaPlugin);
    this.versionManager = new VersionManager(javaPlugin);
    this.runnableManager = new RunnableProvider(javaPlugin);
    this.fileController = new FileController(javaPlugin);
    this.cooldownManager = new CooldownManager(javaPlugin);
  }

}
