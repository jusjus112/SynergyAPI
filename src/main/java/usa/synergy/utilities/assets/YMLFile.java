package usa.synergy.utilities.assets;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.function.Consumer;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import usa.synergy.utilities.SynergyAPI;
import usa.synergy.utilities.libraries.files.SynergyFile;

@Deprecated
public class YMLFile extends SynergyFile {

  private final String dataFolder;
  private final String name;
  private final Consumer<FileConfiguration> configurationConsumer;
  @Getter
  private File file;
  private FileConfiguration data;

  public YMLFile(String dataFolder, String fileName) {
    this(dataFolder, fileName, fileConfiguration -> {
    });
  }

  public YMLFile(String dataFolder, String fileName, Consumer<FileConfiguration> reloadConsumer) {
    this(dataFolder, fileName, Maps.newHashMap(), reloadConsumer);
  }

  public YMLFile(String dataFolder, String fileName, HashMap<String, Object> defaults,
      Consumer<FileConfiguration> reloadConsumer) {
    this.dataFolder = dataFolder;
    this.name = fileName;
    this.configurationConsumer = reloadConsumer;

    file = new File(dataFolder, fileName + ".yml");
    data = YamlConfiguration.loadConfiguration(file);

    if (!defaults.isEmpty()) {
      set(defaults);
    }

    SynergyAPI.getInstance().getFileController().registerFile(this);
    this.configurationConsumer.accept(this.data);
  }

  public FileConfiguration get() {
    return data;
  }

  public void save() {
    try {
      data.save(file);
      reload();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void reload() {
    file = new File(dataFolder, name + ".yml");
    data = YamlConfiguration.loadConfiguration(file);
    this.configurationConsumer.accept(this.data);
  }

  public YMLFile setHeader(String header) {
    get().options().header(header);
    return this;
  }

  public void set(HashMap<String, Object> data) {
    if (!data.isEmpty()) {
      for (String key : data.keySet()) {
        if (key.startsWith("#")) {
          continue;
        }
        if (!get().contains(key)) {
          get().set(key, data.get(key));
        }
      }
    }
    save();
  }

  public boolean exists() {
    return file.exists();
  }

}