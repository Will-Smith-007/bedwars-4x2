package de.will_smith_007.bedwars.game_assets;

import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;

@Getter
public class GameAssets {

    @Setter
    private GameState gameState = GameState.LOBBY;

    @Setter
    private GameConfiguration gameConfiguration = null;

    private final Set<Block> BUILDING_BLOCKS = new HashSet<>();
}
