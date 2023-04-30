package de.will_smith_007.bedwars.teams;

import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamBlue implements ITeam {

    private static TeamBlue instance;
    private final Set<Player> TEAM_PLAYERS = new HashSet<>();
    private final BedWarsTeam BEDWARS_TEAM = BedWarsTeam.BLUE;

    @Override
    public void addPlayer(@NonNull Player player) {
        TEAM_PLAYERS.add(player);
    }

    @Override
    public void removePlayer(@NonNull Player player) {
        TEAM_PLAYERS.remove(player);
    }

    @Override
    public @NonNull Team getTeam(@NonNull Scoreboard scoreboard) {
        Team team;
        if ((team = scoreboard.getTeam("Blue")) == null) {
            team = scoreboard.registerNewTeam("Blue");
        }
        team.color(NamedTextColor.BLUE);
        return team;
    }

    @Override
    public Location getTeamSpawnLocation(@NonNull World world) {
        return BED_WARS_CONFIG.getTeamSpawnLocation(BEDWARS_TEAM, world);
    }

    @Override
    public boolean bedExists(@NonNull World world) {
        final Location bedLocation = BED_WARS_CONFIG.getBedLocation(BEDWARS_TEAM, world);
        if (bedLocation == null) return false;
        final Block block = bedLocation.getBlock();
        return (block.getType().toString().endsWith("BED"));
    }

    @Override
    public @NonNull String getTeamName() {
        return "§9" + BEDWARS_TEAM.getTeamName();
    }

    @Override
    public @NonNull Set<Player> getPlayers() {
        return TEAM_PLAYERS;
    }

    public static @NonNull ITeam getInstance() {
        return (instance == null ? (instance = new TeamBlue()) : instance);
    }
}
