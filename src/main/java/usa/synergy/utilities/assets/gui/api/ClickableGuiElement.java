package usa.synergy.utilities.assets.gui.api;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import usa.synergy.utilities.assets.gui.impl.Gui;

public abstract class ClickableGuiElement extends GuiElement {

  public abstract boolean click(Player player, ClickType clickType, Gui gui);

}
