package de.will_smith_007.bedwars.spawner;

import de.will_smith_007.bedwars.setup.BedWarsSetup;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public record Spawner(@NonNull Location location,
                      @NonNull BedWarsSetup.SpawnerType spawnerType) {

    public void spawnItem() {
        final World world = location.getWorld();
        final Material material;

        switch (spawnerType) {
            case IRON -> material = Material.IRON_INGOT;
            case GOLD -> material = Material.GOLD_INGOT;
            default -> material = Material.BRICK;
        }

        world.dropItem(location, new ItemStack(material));
    }
}
