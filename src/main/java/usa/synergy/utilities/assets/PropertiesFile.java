package usa.synergy.utilities.assets;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileWriter;
import lombok.Getter;
import org.bukkit.Bukkit;
import usa.synergy.utilities.libraries.files.SynergyFile;

@Deprecated
public class PropertiesFile extends SynergyFile {

  private JsonObject main = new JsonObject();
  @Getter
  private File file;

  public PropertiesFile(String fileName) {
    this(Bukkit.getPluginsFolder().getPath(), fileName);
  }

  public PropertiesFile(String folder, String fileName) {
    try {
      File f = new File(folder);
      if (!f.exists()) {
        f.mkdir();
      }

      this.file = new File(f.getAbsolutePath() + File.separator + fileName + ".properties");
      if (!exists()) {
        this.file.createNewFile();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean exists() {
    return this.file.exists();
  }

//  public PropertiesFile write(String path, JsonElement object, boolean override) {
//    if (override || !exists(path)) {
//      main.add(path, object);
//    }
//    finish();
//    return this;
//  }

  public void finish() {
    try {
      FileWriter fileWriter = new FileWriter(this.file);
      fileWriter.write(this.main.toString());
      fileWriter.flush();
      fileWriter.close();
      this.main = new JsonObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
