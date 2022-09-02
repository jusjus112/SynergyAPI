package usa.synergy.utilities.assets.gui.impl;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import usa.synergy.utilities.assets.Pair;
import usa.synergy.utilities.assets.gui.api.ClickableGuiElement;
import usa.synergy.utilities.assets.gui.api.GuiElement;
import usa.synergy.utilities.assets.gui.api.GuiSize;
import usa.synergy.utilities.assets.gui.api.MenuInventoryHolder;
import usa.synergy.utilities.assets.utilities.ItemBuilder;
import usa.synergy.utilities.assets.utilities.SkullItemBuilder;
import usa.synergy.utilities.assets.utilities.UtilString;

public abstract class Gui {

  @Getter
  private final JavaPlugin plugin;
  private final String name;
  private final GuiSize guiSize;
  private final Map<Integer, GuiElement> elements;
  private final Map<UUID, Inventory> currentSessions;
  @Getter
  private Gui parent = null;
  @Setter
  @Getter
  private boolean ignoringParent;

  public Gui(JavaPlugin plugin, String name, GuiSize guiSize) {
    this(plugin, name, guiSize, true);
  }

  public Gui(JavaPlugin plugin, String name, GuiSize guiSize, boolean setup) {
    this.plugin = plugin;
    this.name = name;
    this.guiSize = guiSize;
    this.elements = Maps.newHashMap();
    this.currentSessions = Maps.newHashMap();
    this.ignoringParent = false;

		if (setup) {
			setup();
		}
  }

//  public static GuiElement guiElementFromConfig(String prefix, ConfigurationSection configurationSection,
//      BiFunction<Pair<Player, Material>, String, String> lineEditor){
//    return new GuiElement() {
//      @Override
//      public ItemStack getIcon(Player player) {
//        return ItemBuilder.fromConfig(prefix, configurationSection, (material, s) ->
//            lineEditor.apply(new Pair<>(player, material), s)).build();
//      }
//    };
//  }

  public static GuiElement guiElementFromConfig(String prefix, ConfigurationSection configurationSection,
      BiFunction<Player, ItemBuilder, ItemBuilder> function){

    return new GuiElement() {
      @Override
      public ItemStack getIcon(Player player) {
        ItemBuilder itemBuilder = ItemBuilder.fromConfig(player, prefix, configurationSection);
        itemBuilder.setName(itemBuilder.getItemName()
            .replaceAll("%player%", player.getName())
        );

        if (itemBuilder instanceof SkullItemBuilder){
          // TODO: Add player variable to material.
        }

        return function.apply(player, itemBuilder).build();
      }
    };
  }

  public static Gui fromBukkitFileConfiguration(JavaPlugin plugin, FileConfiguration fileConfiguration,
      BiConsumer<Pair<String, FileConfiguration>, Player> itemClick, BiFunction<Pair<Player, String>, String, String> lineEditor) {
    String name = fileConfiguration.getString("name");
    GuiSize guiSize = GuiSize.fromNumber(fileConfiguration.getInt("size")).orElse(GuiSize.ONE_ROW);

    Validate.notNull(name, "GUI Name cannot be empty or wrong.");
    Validate.notNull(guiSize, "GUI Size cannot be 0 or null.");

    return new Gui(plugin, name, guiSize) {
      @Override
      public void setup() {
        if (fileConfiguration.contains("items")) {
          fileConfiguration.getConfigurationSection("items").getKeys(false).forEach(slot -> {
            try {
              if (Integer.parseInt(slot) >= 0) {
                String prefix = "items." + slot;

                addElement(Integer.parseInt(slot), new ClickableGuiElement() {
                  @Override
                  public ItemStack getIcon(Player player) {
                    return ItemBuilder.fromConfig(player, prefix, fileConfiguration, (material, s) ->
                        lineEditor.apply(new Pair<>(player, prefix), s)).build();
                  }

                  @Override
                  public void click(Player player, ClickType clickType, Gui gui) {
                    itemClick.accept(new Pair<>(prefix, fileConfiguration), player);
                  }
                });
              } else {
                //TODO: Section is null
                getPlugin().getSLF4JLogger().error("Section item is null for " + slot);
              }
            } catch (NumberFormatException exception) {
              exception.printStackTrace();
            }
          });
        }
      }
    };
  }

  public abstract void setup();

  public boolean onClose(Inventory inventory, Player player) {
    return true;
  }

  public boolean onInsert(ItemStack itemStack, Inventory inventory) {
    return false;
  }

  public void insert(Inventory inventory, Player player) {
    for (Entry<Integer, GuiElement> element : elements.entrySet()) {
      inventory.setItem(element.getKey(), element.getValue().getIcon(player));
    }
  }

  /**
   * Opens the menu using a PlayerHolder, supports custom implementations for attaching data to the
   * player.
   *
   * @param player the holder to open the menu for.
   */
  public final void open(Player player) {
    MenuInventoryHolder inventoryHolder = new MenuInventoryHolder(player, this);
    Inventory inventory = plugin.getServer()
        .createInventory(inventoryHolder, guiSize.getSlots(), UtilString.toBukkitColors(name));

    inventoryHolder.setInventory(inventory);
    setItems(inventory, player);

    player.openInventory(inventory);

    currentSessions.remove(player.getUniqueId());
    currentSessions.put(player.getUniqueId(), inventory);
    player.updateInventory();
  }

  public void update(Player player) {
    player.updateInventory();

    Inventory inventory = currentSessions.get(player.getUniqueId());

    Validate.notNull(inventory, "Cannot update before opening inventory!");

    this.ignoringParent = false;
    elements.clear();

    setup();
    setItems(inventory, player);
  }

  void setItems(Inventory inventory, Player player) {
    elements.forEach((key, value) -> inventory.setItem(key, value.getIcon(player)));
  }

//	private void removeFromSession(Player player){
//		for(Gui menu : plugin.getGUIManager().getMenus()) {
//			if(!menu.equals(this)) {
//				menu.getCurrentSessions().remove(player.getUniqueId());
//			}
//		}
//	}

  public void close(Player player) {
    player.closeInventory();
    currentSessions.remove(player.getUniqueId());
  }


  public void close(Player player, boolean ignoringParent) {
    this.ignoringParent = ignoringParent;
    player.closeInventory();
    currentSessions.remove(player.getUniqueId());
  }

  public Gui setParent(Gui parent) {
    this.parent = parent;
    return this;
  }

  public boolean hasParent() {
    return this.parent != null;
  }

  public Gui getOuterClazz() {
    return this;
  }

  public GuiElement getBackGuiElement() {
    return getBackGuiElement("§c§l← Go Back", new ItemBuilder(Material.AIR));
  }

  public GuiElement getBackGuiElement(ItemBuilder alternative) {
    return getBackGuiElement("§c§l← Go Back", alternative);
  }

  public GuiElement getBackGuiElement(String itemName, ItemBuilder alternative) {
    return new ClickableGuiElement() {
      @Override
      public ItemStack getIcon(Player player) {
        if (!hasParent()) {
          return alternative.build();
        }
        return new ItemBuilder(Material.ARROW)
            .setName("§7" + itemName)
            .build();
      }

      @Override
      public void click(Player player, ClickType clickType, Gui gui) {
				if (hasParent()) {
					parent.open(player);
				}
      }
    };
  }

  public void addElement(GuiElement element) {
    for (int i = 0; i < this.guiSize.getSlots(); ++i) {
      if (!this.elements.containsKey(i)) {
        this.addElement(i, element);
        return;
      }
    }
  }

  public void addUnsafeElement(int slot, GuiElement menuElement) {
    try {
      this.elements.put(slot, menuElement);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void line(int first, int last, GuiElement element) {
    for (int i = first; i <= last; i++) {
      addElement(i, element);
    }
  }

  public boolean isFull() {
    for (int i = 0; i < this.guiSize.getSlots(); ++i) {
      if (this.getElement(i) == null) {
        return false;
      }
    }

    return true;
  }

  public void surroundWith(GuiElement item) {
    if (getSize() >= GuiSize.THREE_ROWS.getSlots()) {
      Integer[] walls = new Integer[]{9, 17, 18, 26, 27, 35, 36, 44};
      List<Integer> slots = new ArrayList<>();
      final int size = this.guiSize.getSlots();

      // Outer walls
      int csize = size;
      for (int i = 0; i < 9; i++) {
        slots.add(--csize);
        slots.add(i);
      }

      slots.addAll(Arrays.asList(walls));
      Object[] slotsArray = slots.toArray();
      Arrays.sort(slotsArray);

      for (Object obj : slotsArray) {
        int i = Integer.parseInt(obj.toString());

        if (i >= size) {
          break;
        }
        if (getElement(i) == null) {
          addElement(i, item);
        }
      }
    }
  }

  public int getCenter() {
    switch (this.guiSize) {
      case TWO_ROWS:
        return 4;
      case THREE_ROWS:
      case FOUR_ROWS:
        return 13;
      case FIVE_ROWS:
      case SIX_ROWS:
        return 22;
    }
    return 4;
  }

  public List<Integer> getCenterInput() {
    List<Integer> places = new ArrayList<>();

    switch (this.guiSize) {
      case ONE_ROW:
        places.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        break;
      case TWO_ROWS:
        places.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 10, 11, 12, 13, 14, 15, 16));
        break;
      case THREE_ROWS:
        places.addAll(Arrays.asList(10, 11, 12, 13, 14, 15, 16));
        break;
      case FOUR_ROWS:
        places.addAll(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25));
        break;
      case FIVE_ROWS:
        places.addAll(
            Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31,
                32, 32, 33, 34));
        break;
      case SIX_ROWS:
        places.addAll(
            Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31,
                32, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43));
        break;
    }
    return places;
  }

  public void addElement(int slot, GuiElement menuElement) {
    if (slot >= 0) {
      this.elements.put(slot, menuElement);
    }
  }

  public void removeElement(int slot) {
    this.elements.remove(slot);
  }

  public GuiElement getElement(int slot) {
    if (elements.containsKey(slot)) {
      return elements.get(slot);
    }

    return null;
  }

  public String getName() {
    return name;
  }

  public int getSize() {
    return guiSize.getSlots();
  }

  public Map<Integer, GuiElement> getElements() {
    return elements;
  }

  public Map<UUID, Inventory> getCurrentSessions() {
    return currentSessions;
  }

}
