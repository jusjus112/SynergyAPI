package usa.synergy.utilities.assets.utilities;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class UtilBlock {

  public static ItemStack toItemStack(Block block){
    return block.getState().getData().toItemStack(1);
  }

}
