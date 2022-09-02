package usa.synergy.utilities.libraries.files;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.Module;
import usa.synergy.utilities.libraries.files.json.GSONController;

@Getter
public class FileProvider<A extends JavaPlugin> extends Module<A> {

  private final GSONController<A> gsonController;

  public FileProvider(A plugin) {
    super(plugin, "Files");

    this.gsonController = new GSONController<>(plugin);
  }
}
