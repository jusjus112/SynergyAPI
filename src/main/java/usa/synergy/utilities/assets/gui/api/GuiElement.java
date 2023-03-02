package usa.synergy.utilities.assets.gui.api;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GuiElement {

  public abstract ItemStack getIcon(Player player);

  /**
   *
   *
   * @param itemStack
   * @return
   */
  public static GuiElement fromItemStack(@NonNull ItemStack itemStack){
    return new GuiElement() {
      @Override
      public ItemStack getIcon(Player player) {
        return itemStack;
      }
    };
  }

}
