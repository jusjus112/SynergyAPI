package usa.synergy.utilities.assets.file;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.assets.YMLFile;

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
    boolean allReloaded = true;

    for (YMLFile file : getFiles()) {
      try{
        file.reload();
        getLoader().getSLF4JLogger().info(file.getFile().getName() + " Reloaded");
      }catch (Exception exception){
        getLoader().getSLF4JLogger().info("Error while reloading " + file.getFile().getName());
        exception.printStackTrace();
        return false;
      }
    }

    return allReloaded;
  }
}
