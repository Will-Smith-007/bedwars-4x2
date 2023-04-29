package de.will_smith_007.bedwars.game_assets;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import lombok.Getter;
import lombok.Setter;

public class GameAssets {

    @Getter
    @Setter
    private GameState gameState = GameState.LOBBY;
    @Getter
    @Setter
    private GameConfiguration gameConfiguration = null;
}
