package usa.synergy.utilities.libraries.files.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.libraries.files.json.adapters.serializers.*;

public class GSONController<A extends JavaPlugin> extends Module<A> {

  private final Gson gson;

  public GSONController(A plugin){
    super(plugin, "GSON");

    this.gson = new GsonBuilder()
        .registerTypeAdapter(Location.class, new BukkitLocationSerializer())
        .registerTypeAdapter(ChatColor.class, new ChatColorSerializer())
        .setPrettyPrinting()
        .create();
  }

  /**
   *
   * @param javaPlugin
   * @param configClass
   * @param <C>
   * @return
   */
  public <C extends JsonConfig> C getConfig(JavaPlugin javaPlugin, Class<? extends C> configClass) {
    return getConfig(javaPlugin, null, configClass);
  }

  /**
   *
   * @param configClass
   * @param <C>
   * @return
   */
  @SneakyThrows
  private <C extends JsonConfig> C createConfigClazz(Class<? extends C> configClass) {
    return configClass.newInstance();
  }

  /**
   *
   * @param file
   * @param configClass
   * @param <C>
   * @return
   * @throws IOException
   */
  public <C extends JsonConfig> C getConfig(File file, Class<? extends C> configClass)
      throws IOException {
    return gson.fromJson(FileUtils.readFileToString(file, StandardCharsets.UTF_8), configClass);
  }

  /**
   *
   * @param dataFolder
   * @param configName
   * @return
   */
  private File getConfigFile(File dataFolder, String configName) {
    Validate.notNull(configName);

    return new File(dataFolder, configName);
  }

  /**
   *
   * @param javaPlugin
   * @param configName
   * @param configClass
   * @param <C>
   * @return
   */
  public <C extends JsonConfig> C getConfig(JavaPlugin javaPlugin, String configName, Class<? extends C> configClass) {
    C config = createConfigClazz(configClass);
    String name = ".json";

    if (config.getConfigName() == null){
      if (configName != null){
        name = configName + name;
      }else{
        throw new NullPointerException("No value provided for config name. Both are null.");
      }
    }else{
      name = config.getConfigName() + (configName == null ? "" : "-" + configName) + name;
    }

    File file = getConfigFile(javaPlugin.getDataFolder(), name);

    try{
      return getConfig(file, configClass);
    }catch (FileNotFoundException exception){
      if (!file.exists()){
        try{
          if (file.createNewFile()){
            //TODO: File already exists.
          }
        }catch (Exception exception1){
          javaPlugin.getSLF4JLogger().error("Cannot create file: " + exception1.getMessage());
        }
      }
    }catch (Exception exception){
      exception.printStackTrace();
    }

    return config;
  }

}
