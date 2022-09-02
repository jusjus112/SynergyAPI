package usa.synergy.utilities.assets.utilities;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullItemBuilder extends ItemBuilder{

    private final SkullMeta skullMeta;

    public SkullItemBuilder(Player player){
        super(Material.PLAYER_HEAD, 1);

        this.skullMeta = (SkullMeta) getItemMeta();
        this.skullMeta.setOwningPlayer(player);
        this.initItemMeta(this.skullMeta);
    }

    public SkullItemBuilder(UUID uuid){
        super(Material.PLAYER_HEAD, 1);

        this.skullMeta = (SkullMeta) getItemMeta();
        this.skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        this.initItemMeta(this.skullMeta);
    }

    public SkullItemBuilder(String name){
        super(Material.PLAYER_HEAD, 1);

        this.skullMeta = (SkullMeta) getItemMeta();
        this.skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(name));
        this.initItemMeta(this.skullMeta);
    }

}
