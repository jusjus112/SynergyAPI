package usa.synergy.utilities.assets.gui.api;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import usa.synergy.utilities.SynergyAPI;
import usa.synergy.utilities.SynergyUtilitiesAPI;

public abstract class GuiInteract {

  @Getter
  private final SynergyAPI synergyUtilitiesAPI;
  @Getter
  private final String name;
  @Getter
  private final Map<GuiInteractElement, Integer> elements;

  public GuiInteract(SynergyAPI synergyUtilitiesAPI) {
    this(synergyUtilitiesAPI, true);
  }

  public GuiInteract(SynergyAPI synergyUtilitiesAPI, boolean setup) {
    this.synergyUtilitiesAPI = synergyUtilitiesAPI;
    this.name = "Player Inventory";
    this.elements = Maps.newHashMap();

    synergyUtilitiesAPI.getGuiManager().getInteractMenus().add(this);

    if (setup) {
      setup();
    }
  }

  public abstract void setup();

  public void insert(Inventory inventory, Player player) {
    for (Map.Entry<GuiInteractElement, Integer> element : elements.entrySet()) {
      inventory.setItem(element.getValue(), element.getKey().getIcon(player));
    }
  }

  public void add(Inventory inventory, Player player) {
    for (Map.Entry<GuiInteractElement, Integer> element : elements.entrySet()) {
      inventory.addItem(element.getKey().getIcon(player));
    }
  }

  public void addElement(GuiInteractElement menuElement, int slot) {
    if (slot >= 0) {
      if (!this.elements.containsValue(slot)) {
        this.elements.put(menuElement, slot);
      }
    }
  }

}
