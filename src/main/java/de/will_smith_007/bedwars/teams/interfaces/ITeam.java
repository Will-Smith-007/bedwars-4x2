package de.will_smith_007.bedwars.teams.interfaces;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Set;

public interface ITeam {

    BedWarsConfig BED_WARS_CONFIG = BedWarsConfig.getInstance();

    void addPlayer(@NonNull Player player);

    void removePlayer(@NonNull Player player);

    @NonNull Team getTeam(@NonNull Scoreboard scoreboard);

    Location getTeamSpawnLocation(@NonNull World world);

    boolean bedExists(@NonNull World world);

    @NonNull Set<Player> getPlayers();
}
