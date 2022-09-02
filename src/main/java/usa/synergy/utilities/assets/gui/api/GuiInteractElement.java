package usa.synergy.utilities.assets.gui.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class GuiInteractElement {

  public abstract ItemStack getIcon(Player player);

  public abstract void click(Player player, Action action);

}
