package de.will_smith_007.bedwars.inventories.lobby;

import de.will_smith_007.bedwars.inventories.interfaces.IBedWarsInventory;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;

public class TeamSelectorInventory implements IBedWarsInventory {

    private final String INVENTORY_NAME = "Team selection";
    private final BedWarsTeam[] BED_WARS_TEAM = BedWarsTeam.values();

    @Override
    public void openInventory(@NonNull Player player) {
        final Inventory inventory = Bukkit.createInventory(null, 9,
                Component.text(INVENTORY_NAME, NamedTextColor.AQUA));

        int currentSlot = 0;
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        final int playerSize = players.size();
        final int maxPlayersPerTeam = (int) Math.ceil((double) playerSize / BED_WARS_TEAM.length);

        for (BedWarsTeam bedWarsTeam : BED_WARS_TEAM) {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            final String teamName = iTeam.getTeamName();

            final Material teamMaterial;
            switch (bedWarsTeam) {
                case BLUE -> teamMaterial = Material.BLUE_WOOL;
                case RED -> teamMaterial = Material.RED_WOOL;
                case GREEN -> teamMaterial = Material.GREEN_WOOL;
                default -> teamMaterial = Material.YELLOW_WOOL;
            }

            final ItemStack itemStack = new ItemStack(teamMaterial);
            itemStack.editMeta(itemMeta -> {
                itemMeta.lore(Arrays.asList(
                        Component.text(""),
                        Component.text("§7Players: §e" + teamPlayers + "§7/§c" + maxPlayersPerTeam),
                        Component.text("")
                ));

                itemMeta.displayName(Component.text(teamName).decoration(TextDecoration.ITALIC, false));
            });

            inventory.setItem(currentSlot, itemStack);
            currentSlot++;
        }

        player.openInventory(inventory);
    }

    @Override
    public @NonNull String getInventoryName() {
        return INVENTORY_NAME;
    }
}
