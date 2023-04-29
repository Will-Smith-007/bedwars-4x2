package de.will_smith_007.bedwars.listeners;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final GameAssets GAME_ASSETS;

    public PlayerConnectionListener(@NonNull GameAssets gameAssets) {
        GAME_ASSETS = gameAssets;
    }

    @EventHandler
    public void onPlayerJoin(@NonNull PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case LOBBY -> {
                playerJoinEvent.joinMessage(Component.text("§e" + player.getName() +
                        "§7 joined the game!"));
                player.setGameMode(GameMode.ADVENTURE);
            }
            case PROTECTION, INGAME, ENDING -> {
                playerJoinEvent.joinMessage(null);
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(@NonNull PlayerQuitEvent playerQuitEvent) {
        final Player player = playerQuitEvent.getPlayer();
        final GameState gameState = GAME_ASSETS.getGameState();

        switch (gameState) {
            case LOBBY -> {
                playerQuitEvent.quitMessage(Component.text("§e" + player.getName() +
                        "§7 left the game."));
            }
            case PROTECTION, INGAME, ENDING -> {
                playerQuitEvent.quitMessage(null);
            }
        }
    }
}
