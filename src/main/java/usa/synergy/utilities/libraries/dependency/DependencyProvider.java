package usa.synergy.utilities.libraries.dependency;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.utlities.SynergyLogger;

public class DependencyProvider<A extends JavaPlugin> extends Module<A> {

  public DependencyProvider(A plugin) {
    super(plugin, "Dependency Provider");
  }

  /**
   *
   * @see Plugin
   * @param name the plugin name to be checked by bukkit.
   * @param <P> represents the plugin main file to be returned.
   * @return the plugin which is defined by P.
   * @throws NullPointerException when the plugin doesn't exists.
   */
  @Nullable
  public <P> P load(@NotNull String name) throws NullPointerException{
    try{
      Plugin plugin = getLoader().getServer().getPluginManager().getPlugin(name);
      if (plugin == null){
        throw new NullPointerException("Plugin " + name + " is not found. Skipping...");
      }

      if (!plugin.isEnabled()){
        throw new NullPointerException("Plugin " + name + " is not enabled. We need it.");
      }

      SynergyLogger.success("Found dependency '" + name + "' and using it now...");

      return (P) plugin;
    }catch (Exception throwable){
      throwable.printStackTrace();
      return null;
    }
  }

}
