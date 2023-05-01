package de.will_smith_007.bedwars.listeners;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.enums.Message;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import de.will_smith_007.bedwars.lobby_countdown.interfaces.ILobbyCountdownHelper;
import de.will_smith_007.bedwars.teams.helper.interfaces.ITeamHelper;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Optional;

public class PlayerConnectionListener implements Listener {

    private final GameAssets GAME_ASSETS;
    private final ITeamHelper TEAM_HELPER;
    private final ILobbyCountdownHelper LOBBY_COUNTDOWN_HELPER;

    public PlayerConnectionListener(@NonNull GameAssets gameAssets,
                                    @NonNull ILobbyCountdownHelper lobbyCountdownHelper,
                                    @NonNull ITeamHelper teamHelper) {
        GAME_ASSETS = gameAssets;
        LOBBY_COUNTDOWN_HELPER = lobbyCountdownHelper;
        TEAM_HELPER = teamHelper;
    }

    @EventHandler
    public void onPlayerJoin(@NonNull PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case LOBBY -> {
                playerJoinEvent.joinMessage(Component.text(Message.PREFIX + "§e" + player.getName() +
                        "§7 joined the game!"));
                player.setGameMode(GameMode.ADVENTURE);

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
            }
            case INGAME, PROTECTION -> {
                playerQuitEvent.quitMessage(Component.text(Message.PREFIX.toString())
                        .append(player.displayName())
                        .append(Component.text("§7 left the game")));

                final Optional<ITeam> optionalITeam = TEAM_HELPER.getTeam(player);
                optionalITeam.ifPresent(team -> {
                    team.removePlayer(player);
                    TEAM_HELPER.handleTeamElimination(team, Bukkit.getOnlinePlayers());
                });
            }
            case ENDING -> playerQuitEvent.quitMessage(null);
        }
    }
}
