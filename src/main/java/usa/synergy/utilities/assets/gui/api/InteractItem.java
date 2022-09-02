package usa.synergy.utilities.assets.gui.api;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import usa.synergy.utilities.assets.utilities.ItemBuilder;

public abstract class InteractItem extends ItemBuilder {

  @Getter
  private static final List<InteractItem> interactItems = Lists.newArrayList();

  public InteractItem(Material material) {
    super(material);

    interactItems.add(this);
  }

  public abstract void onClick(Player player, Action action);

}
