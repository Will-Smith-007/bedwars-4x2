package de.will_smith_007.bedwars.listeners;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.lobby.enums.LobbyItem;
import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.scoreboard.interfaces.IScoreboardManager;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Optional;

public class PlayerConnectionListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final ILobbyCountdownHelper LOBBY_COUNTDOWN_HELPER;
    private final IScoreboardManager SCOREBOARD_MANAGER;
    private final BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    public PlayerConnectionListener(@NonNull GameAssets gameAssets,
                                    @NonNull ILobbyCountdownHelper lobbyCountdownHelper,
                                    @NonNull ITeamHelper teamHelper,
                                    @NonNull IScoreboardManager scoreboardManager) {
        GAME_ASSETS = gameAssets;
        LOBBY_COUNTDOWN_HELPER = lobbyCountdownHelper;
        TEAM_HELPER = teamHelper;
        SCOREBOARD_MANAGER = scoreboardManager;
    }

    @EventHandler
    public void onPlayerJoin(@NonNull PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        final GameState gameState = GAME_ASSETS.getGameState();

        SCOREBOARD_MANAGER.setScoreboardAndTablist(player);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equals(player.getName())) continue;

            SCOREBOARD_MANAGER.updateScoreboard(onlinePlayer);
            SCOREBOARD_MANAGER.setTablist(onlinePlayer);
        }

        switch (gameState) {
            case LOBBY -> {
                playerJoinEvent.joinMessage(Component.text(Message.PREFIX + "§e" + player.getName() +
                        "§7 joined the game!"));
                player.setGameMode(GameMode.ADVENTURE);

                final String lobbyWorldName = BED_WARS_CONFIG.getLobbyWorld();
                if (lobbyWorldName == null) return;
                final World world = Bukkit.createWorld(new WorldCreator(lobbyWorldName));
                if (world == null) return;

                player.teleport(world.getSpawnLocation());

                final ItemStack teamSelectorItem = LobbyItem.TEAM_SELECTOR.buildItem();
                player.getInventory().setItem(0, teamSelectorItem);

                LOBBY_COUNTDOWN_HELPER.startCountdownIfEnoughPlayers();
            }
            case PROTECTION, INGAME, ENDING -> {
                playerJoinEvent.joinMessage(null);
                player.setGameMode(GameMode.SPECTATOR);
                //TODO: Hide from game players
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(@NonNull PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case LOBBY -> {
                playerQuitEvent.quitMessage(Component.text(Message.PREFIX + "§e" + player.getName() +
                        "§7 left the game."));

                final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
                final int playerSize = (players.size() - 1);

                LOBBY_COUNTDOWN_HELPER.cancelCountdownIfNotEnoughPlayers(playerSize);
                LOBBY_COUNTDOWN_HELPER.cancelCountdownIfNotEnoughTeams();

                final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                optionalITeam.ifPresent(team -> team.removePlayer(player));
            }
            case INGAME, PROTECTION -> {
                final Scoreboard scoreboard = player.getScoreboard();
                final Team playerTeam = scoreboard.getPlayerTeam(player);

                if (playerTeam == null) return;

                final TextColor textColor = playerTeam.color();
                final String playerName = player.getName();
                final String prefix = Message.PREFIX.toString();

                playerQuitEvent.quitMessage(Component.text(prefix)
                        .append(Component.text(playerName).color(textColor))
                        .append(Component.text("§7 left the game")));

                final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                optionalITeam.ifPresent(team -> {
                    team.removePlayer(player);
                    TEAM_HELPER.handleTeamElimination(team, Bukkit.getOnlinePlayers());
                });
            }
            case ENDING -> playerQuitEvent.quitMessage(null);
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getName().equals(player.getName())) continue;

            SCOREBOARD_MANAGER.updateScoreboard(onlinePlayer);
            SCOREBOARD_MANAGER.setTablist(onlinePlayer);
        }
    }
}
