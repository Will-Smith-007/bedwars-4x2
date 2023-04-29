package de.will_smith_007.bedwars.game_assets;

import de.will_smith_007.bedwars.enums.GameState;
import lombok.Getter;
import lombok.Setter;

public class GameAssets {

    @Getter
    @Setter
    private GameState gameState = GameState.LOBBY;
}
