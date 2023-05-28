package de.will_smith_007.bedwars.listeners.lobby;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.inventories.lobby.TeamSelectorInventory;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import de.will_smith_007.bedwars.teams.parser.TeamParser;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Optional;

/**
 * This {@link Listener} handles the {@link InventoryClickEvent} where for e.g. a player can choose
 * a player team. Also handles tablist updates on team selection.
 */
public class LobbyInventoryClickListener implements Listener {

    private final GameAssets gameAssets;
    private final TeamParser teamParser;
    private final TeamSelectorInventory teamSelectorInventory;
    private final IScoreboardManager scoreboardManager;
    private final ITeamHelper teamHelper;
    private final BedWarsTeam[] bedWarsTeams = BedWarsTeam.values();

    @Inject
    public LobbyInventoryClickListener(@NonNull GameAssets gameAssets,
                                       @NonNull TeamParser teamParser,
                                       @NonNull TeamSelectorInventory teamSelectorInventory,
                                       @NonNull IScoreboardManager scoreboardManager,
                                       @NonNull ITeamHelper teamHelper) {
        this.gameAssets = gameAssets;
        this.teamParser = teamParser;
        this.teamSelectorInventory = teamSelectorInventory;
        this.scoreboardManager = scoreboardManager;
        this.teamHelper = teamHelper;
    }

    @EventHandler
    public void onLobbyInventoryClick(@NonNull InventoryClickEvent inventoryClickEvent) {
        final GameState gameState = gameAssets.getGameState();
        if (gameState != GameState.LOBBY) return;

        final Player player = (Player) inventoryClickEvent.getWhoClicked();

        final GameMode playerGameMode = player.getGameMode();
        if (playerGameMode != GameMode.ADVENTURE
                && playerGameMode != GameMode.SURVIVAL) return;

        inventoryClickEvent.setCancelled(true);

        if (inventoryClickEvent.isShiftClick()) return;
        if (inventoryClickEvent.getSlotType() == InventoryType.SlotType.OUTSIDE) return;
        if (inventoryClickEvent.getClickedInventory() == null) return;

        final ItemStack currentItem = inventoryClickEvent.getCurrentItem();

        if (currentItem == null) return;
        if (!currentItem.hasItemMeta()) return;

        final InventoryView inventoryView = inventoryClickEvent.getView();
        final String inventoryName = PlainTextComponentSerializer.plainText().serialize(inventoryView.title());

        if (!inventoryName.equals(teamSelectorInventory.getInventoryName())) {
            return;
        }
        final ItemMeta itemMeta = currentItem.getItemMeta();
        final Component displayNameComponent = itemMeta.displayName();

        if (displayNameComponent == null) return;

        final String displayName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);

        // Checks the display name of an item and tries to parse the team of it
        teamParser.parseTeam(displayName).ifPresent(bedWarsTeam -> {
            final ITeam iTeam = bedWarsTeam.getTeam();
            final int teamPlayers = iTeam.getPlayers().size();
            final String teamName = iTeam.getTeamName();

            final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            final int playerSize = players.size();
            // Rounds the amount of max players per team to the next integer
            final int maxPlayersPerTeam = (int) Math.ceil((double) playerSize / bedWarsTeams.length);

            if (teamPlayers >= maxPlayersPerTeam) {
                player.sendPlainMessage(Message.PREFIX + "§cYou can't join " + teamName + "§c, because it is full.");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1.0f, 1.0f);
                return;
            }

            // If the player is already in a team, then remove the player first from the previous team
            final Optional<ITeam> optionalITeam = teamHelper.getTeam(player);
            optionalITeam.ifPresent(team -> team.removePlayer(player));

            // Adds the player to the team parsed from the ItemStack display name
            iTeam.addPlayer(player);
            player.sendPlainMessage(Message.PREFIX + "§aYou joined " + teamName + "§a.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.0f);

            player.closeInventory();

            // Updates the tablist of all online players
            for (Player onlinePlayer : players) {
                scoreboardManager.setTablist(onlinePlayer);
            }
        });
    }
}
