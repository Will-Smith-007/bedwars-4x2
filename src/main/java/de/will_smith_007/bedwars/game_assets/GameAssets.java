package de.will_smith_007.bedwars.game_assets;

import com.google.inject.Singleton;
import de.will_smith_007.bedwars.enums.GameState;
import de.will_smith_007.bedwars.game_config.GameConfiguration;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class holds all information about the current game.
 * e.g. the current {@link GameState} and the {@link Set} of built blocks.
 */
@Getter
@Singleton
public class GameAssets {

    @Setter
    private GameState gameState = GameState.LOBBY;

    @Setter
    private GameConfiguration gameConfiguration = null;

    private final Set<Block> builtBlocks = new HashSet<>();

    private final Map<Location, ITeam> teamChestLocations = new HashMap<>();
}
