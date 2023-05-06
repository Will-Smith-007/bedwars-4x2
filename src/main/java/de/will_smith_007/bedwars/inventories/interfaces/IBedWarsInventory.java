package de.will_smith_007.bedwars.inventories.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * This interface should be implemented in all classes which handles the creation of a
 * {@link org.bukkit.inventory.Inventory}.
 */
public interface IBedWarsInventory {

    /**
     * Opens the created {@link org.bukkit.inventory.Inventory}.
     *
     * @param player Player which should open the created inventory.
     */
    void openInventory(@NonNull Player player);

    /**
     * Gets the name of the created {@link org.bukkit.inventory.Inventory}.
     *
     * @return The name of inventory.
     */
    @NonNull String getInventoryName();
}
