package de.will_smith_007.bedwars.listeners.game;

import com.google.inject.Inject;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_assets.GameAssets;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * This {@link Listener} handles the {@link FoodLevelChangeEvent} to deny food level change
 * only if the {@link GameState} is currently set to {@link GameState#LOBBY}.
 */
public class FoodLevelChangeListener implements Listener {

    private final GameAssets gameAssets;

    @Inject
    public FoodLevelChangeListener(@NonNull GameAssets gameAssets) {
        this.gameAssets = gameAssets;
    }

    @EventHandler
    public void onFoodLevelChange(@NonNull FoodLevelChangeEvent foodLevelChangeEvent) {
        final GameState gameState = gameAssets.getGameState();
        if (gameState == GameState.LOBBY) foodLevelChangeEvent.setCancelled(true);
    }
}
