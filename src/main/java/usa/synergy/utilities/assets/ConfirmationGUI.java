package usa.synergy.utilities.assets;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.gui.api.ClickableGuiElement;
import usa.synergy.utilities.assets.gui.api.GuiElement;
import usa.synergy.utilities.assets.gui.api.GuiSize;
import usa.synergy.utilities.assets.gui.impl.Gui;
import usa.synergy.utilities.assets.utilities.ItemBuilder;

public abstract class ConfirmationGUI extends Gui {

    public ConfirmationGUI(JavaPlugin plugin, String name, GuiSize guiSize){
        super(plugin, name, guiSize);
    }

    public ConfirmationGUI(JavaPlugin plugin, String name){
        super(plugin, name, GuiSize.THREE_ROWS);
    }

    public abstract void onAccept(Player player);
    public abstract void onDisallow(Player player);

    public ClickableGuiElement getLeftItem(){
        return new ClickableGuiElement() {
            @Override
            public ItemStack getIcon(Player player) {
                return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                    .setName("§a§lYes!")
                    .build();
            }

            @Override
            public boolean click(Player player, ClickType clickType, Gui gui) {
                player.closeInventory();
                onAccept(player);
                return true;
            }
        };
    }

    public ClickableGuiElement getRightItem(){
        return new ClickableGuiElement() {
            @Override
            public ItemStack getIcon(Player player) {
                return new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setName("§c§lNo")
                    .build();
            }

            @Override
            public boolean click(Player player, ClickType clickType, Gui gui) {
                player.closeInventory();
                onDisallow(player);
                return true;
            }
        };
    }

    @Override
    public void setup() {
        for(int i : getLeftSide()){
            addElement(i, getLeftItem());
        }
        for(int i : getRightSide()){
            addElement(i, getRightItem());
        }
        for(int i : getMiddle()){
            addElement(i, new GuiElement() {
                @Override
                public ItemStack getIcon(Player player) {
                    return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                            .setName(" ")
                            .build();
                }
            });
        }
    }

    private int[] getLeftSide(){
        if (getGuiSize() == GuiSize.ONE_ROW){
            return new int[]{0,1,2,3};
        }
        return new int[]{0,1,2,3,9,10,11,12,18,19,20,21};
    }

    private int[] getRightSide(){
        if (getGuiSize() == GuiSize.ONE_ROW){
            return new int[]{5,6,7,8};
        }
        return new int[]{5,6,7,8,14,15,16,17,23,24,25,26};
    }

    private int[] getMiddle(){
        if (getGuiSize() == GuiSize.ONE_ROW){
            return new int[]{4};
        }
        return new int[]{4,13,22};
    }
}
