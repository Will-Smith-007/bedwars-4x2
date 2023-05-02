package de.will_smith_007.bedwars.listeners.game;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChangeListener implements Listener {

    private final GameAssets GAME_ASSETS;

    public FoodLevelChangeListener(@NonNull GameAssets gameAssets) {
        GAME_ASSETS = gameAssets;
    }

    @EventHandler
    public void onFoodLevelChange(@NonNull FoodLevelChangeEvent foodLevelChangeEvent) {
        final GameState gameState = GAME_ASSETS.getGameState();
        if (gameState == GameState.LOBBY) foodLevelChangeEvent.setCancelled(true);
    }
}
