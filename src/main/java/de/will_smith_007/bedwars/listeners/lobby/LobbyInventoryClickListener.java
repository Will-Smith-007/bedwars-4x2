package de.will_smith_007.bedwars.listeners.lobby;

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

public class LobbyInventoryClickListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final TeamParser TEAM_PARSER;
    private final TeamSelectorInventory TEAM_SELECTOR_INVENTORY;
    private final IScoreboardManager SCOREBOARD_MANAGER;
    private final ITeamHelper TEAM_HELPER;
    private final BedWarsTeam[] BED_WARS_TEAMS = BedWarsTeam.values();

    public LobbyInventoryClickListener(@NonNull GameAssets gameAssets,
                                       @NonNull TeamParser teamParser,
                                       @NonNull TeamSelectorInventory teamSelectorInventory,
                                       @NonNull IScoreboardManager scoreboardManager,
                                       @NonNull ITeamHelper teamHelper) {
        GAME_ASSETS = gameAssets;
        TEAM_PARSER = teamParser;
        TEAM_SELECTOR_INVENTORY = teamSelectorInventory;
        SCOREBOARD_MANAGER = scoreboardManager;
        TEAM_HELPER = teamHelper;
    }

    @EventHandler
    public void onInventoryClick(@NonNull InventoryClickEvent inventoryClickEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();
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

        if (inventoryName.equals(TEAM_SELECTOR_INVENTORY.getInventoryName())) {
            final ItemMeta itemMeta = currentItem.getItemMeta();
            final Component displayNameComponent = itemMeta.displayName();

            if (displayNameComponent == null) return;

            final String displayName = PlainTextComponentSerializer.plainText().serialize(displayNameComponent);

            TEAM_PARSER.parseTeam(displayName).ifPresent(bedWarsTeam -> {
                final ITeam iTeam = bedWarsTeam.getTeam();
                final int teamPlayers = iTeam.getPlayers().size();
                final String teamName = iTeam.getTeamName();

                final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                final int playerSize = players.size();
                final int maxPlayersPerTeam = (int) Math.ceil((double) playerSize / BED_WARS_TEAMS.length);

                if (teamPlayers >= maxPlayersPerTeam) {
                    player.sendPlainMessage(Message.PREFIX + "§cYou can't join " + teamName + "§c, because it is full.");
                    return;
                }

                final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                optionalITeam.ifPresent(team -> team.removePlayer(player));

                iTeam.addPlayer(player);
                player.sendPlainMessage(Message.PREFIX + "§aYou joined " + teamName + "§a.");

                player.closeInventory();

                for (Player onlinePlayer : players) {
                    SCOREBOARD_MANAGER.setTablist(onlinePlayer);
                }
            });
        }
    }
}
