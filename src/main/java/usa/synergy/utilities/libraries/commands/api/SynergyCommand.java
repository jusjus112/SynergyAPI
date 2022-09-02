package usa.synergy.utilities.libraries.commands.api;

import com.google.common.collect.Sets;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.PluginLoader;

public abstract class SynergyCommand<A extends JavaPlugin, P extends CommandSender> extends PluginLoader<A> {

  protected final Set<SynergySubCommand> subCommands;

  public SynergyCommand(A loader) {
    super(loader);

    this.subCommands = Sets.newConcurrentHashSet();
  }

  public abstract void execute(P sender);

  /**
   *
   * @param synergySubClazz
   * @param <C>
   */
  public <C extends SynergySubCommand> void processSubCommand(Class<C> synergySubClazz){
    try{
      synergySubClazz.getConstructor().newInstance();

    } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
      e.printStackTrace();
    }
  }
}
