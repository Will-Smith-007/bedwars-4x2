package de.will_smith_007.bedwars.teams;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamGreen implements ITeam {

    private static TeamGreen instance;
    private final Set<Player> teamPlayers = new HashSet<>();
    private boolean bedExists = true;

    @Override
    public void addPlayer(@NonNull Player player) {
        teamPlayers.add(player);
    }

    @Override
    public void removePlayer(@NonNull Player player) {
        teamPlayers.remove(player);
    }

    @Override
    public void setBedExists(boolean bedExists) {
        this.bedExists = bedExists;
    }

    @Override
    public @NonNull Team getTeam(@NonNull Scoreboard scoreboard) {
        Team team;
        if ((team = scoreboard.getTeam("Green")) == null) {
            team = scoreboard.registerNewTeam("Green");
        }
        team.color(NamedTextColor.GREEN);
        return team;
    }

    @Override
    public Location getTeamSpawnLocation(@NonNull World world, @NonNull BedWarsConfig bedWarsConfig) {
        return bedWarsConfig.getTeamSpawnLocation(BedWarsTeam.GREEN, world);
    }

    @Override
    public boolean bedExists() {
        return bedExists;
    }

    @Override
    public @NonNull String getTeamName() {
        return "§a" + BedWarsTeam.GREEN.getTeamName();
    }

    @Override
    public @NonNull Set<Player> getPlayers() {
        return teamPlayers;
    }

    public static @NonNull TeamGreen getInstance() {
        return (instance == null ? (instance = new TeamGreen()) : instance);
    }
}
