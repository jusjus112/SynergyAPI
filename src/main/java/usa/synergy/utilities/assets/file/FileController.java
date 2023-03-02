package usa.synergy.utilities.assets.file;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.assets.JSONFile;
import usa.synergy.utilities.assets.PropertiesFile;
import usa.synergy.utilities.assets.YMLFile;
import usa.synergy.utilities.libraries.files.SynergyFile;
import usa.synergy.utilities.utlities.SynergyLogger;

@Getter
public class FileController extends Module {

  private final List<YMLFile> files;

  public FileController(JavaPlugin plugin) {
    super(plugin, "Files");

    this.files = Lists.newArrayList();
  }

  /**
   * Register the YMLFile to this controller.
   *
   * @param ymlFile the file that needs to be registered.
   * @return if the file has been successfully registered.
   */
  public boolean registerFile(YMLFile ymlFile) {
    return this.files.add(ymlFile);
  }

  /**
   * Reloading all the registered files in this controller.
   * Will let the logger know if there were any problems.
   *
   * @return if there were no errors while reloading.
   */
  public boolean reloadAll() {
    return reloadAll(YMLFile.class, JSONFile.class, PropertiesFile.class);
  }

  /**
   * Reloading all the registered files in this controller.
   * Will only reload the files that are of the specified type.
   * Will let the logger know if there were any problems.
   *
   * @param classes the classes that need to be reloaded.
   * @return if there were no errors while reloading.
   */
  @SafeVarargs
  public final boolean reloadAll(Class<? extends SynergyFile>... classes){
    boolean reloaded = true;

    for (YMLFile file : getFiles()) {
      for (Class<? extends SynergyFile> clazz : classes) {
        if(file.getClass().equals(clazz)){
          try{
            file.reload();
            SynergyLogger.info(file.getFile().getName() + " Reloaded");
          }catch (Exception exception){
            SynergyLogger.error("Error while reloading " + file.getFile().getName());
            SynergyLogger.error(exception.getMessage());
            return false;
          }
        }
      }
    }

    return reloaded;
  }
}
