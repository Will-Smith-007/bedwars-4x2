package de.will_smith_007.bedwars.teams;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamBlue implements ITeam {

    private static TeamBlue instance;
    private final Set<Player> teamPlayers = new HashSet<>();
    private boolean bedExists = true;
    private final Inventory teamChest = Bukkit.createInventory(null, 9 * 3,
            Component.text("§9Team Blue"));

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
    public void openTeamChest(@NonNull Player player) {
        player.openInventory(teamChest);
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
    public Location getTeamSpawnLocation(@NonNull World world, @NonNull BedWarsConfig bedWarsConfig) {
        return bedWarsConfig.getTeamSpawnLocation(BedWarsTeam.BLUE, world);
    }

    @Override
    public boolean bedExists() {
        return bedExists;
    }

    @Override
    public @NonNull String getTeamName() {
        return "§9" + BedWarsTeam.BLUE.getTeamName();
    }

    @Override
    public @NonNull Set<Player> getPlayers() {
        return teamPlayers;
    }

    public static @NonNull ITeam getInstance() {
        return (instance == null ? (instance = new TeamBlue()) : instance);
    }
}
