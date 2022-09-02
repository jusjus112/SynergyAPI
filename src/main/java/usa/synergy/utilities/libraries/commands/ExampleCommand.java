package usa.synergy.utilities.libraries.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.libraries.commands.annotations.CommandArgument;
import usa.synergy.utilities.libraries.commands.annotations.CommandSettings;
import usa.synergy.utilities.libraries.commands.api.SynergyCommand;

@CommandSettings(
  command = "generator"
)
public class ExampleCommand extends SynergyCommand<JavaPlugin, Player> {

  public ExampleCommand(JavaPlugin loader) {
    super(loader);

  }

  @Override
  public void execute(Player sender) {

  }
}
