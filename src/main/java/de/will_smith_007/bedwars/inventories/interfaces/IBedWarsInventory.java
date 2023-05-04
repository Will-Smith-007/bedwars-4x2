package de.will_smith_007.bedwars.inventories.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;

public interface IBedWarsInventory {

    void openInventory(@NonNull Player player);

    @NonNull String getInventoryName();
}
