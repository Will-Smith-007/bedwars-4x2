package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameChatListener implements Listener {

    private final GameAssets GAME_ASSETS;

    public GameChatListener(@NonNull GameAssets gameAssets) {
        GAME_ASSETS = gameAssets;
    }

    @EventHandler
    public void onGameChat(@NonNull AsyncChatEvent asyncChatEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();
        //TODO: Game Chat
    }
}
