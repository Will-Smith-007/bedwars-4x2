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

    void addPlayer(@NonNull Player player);

    void removePlayer(@NonNull Player player);

    void setBedExists(boolean bedExists);

    @NonNull Team getTeam(@NonNull Scoreboard scoreboard);

    Location getTeamSpawnLocation(@NonNull World world, @NonNull BedWarsConfig bedWarsConfig);

    boolean bedExists();

    @NonNull String getTeamName();

    @NonNull Set<Player> getPlayers();
}
