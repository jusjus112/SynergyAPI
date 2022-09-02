package usa.synergy.utilities.libraries;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
@Getter
public abstract class PluginLoader<A extends JavaPlugin> {

  private final A loader;

}
