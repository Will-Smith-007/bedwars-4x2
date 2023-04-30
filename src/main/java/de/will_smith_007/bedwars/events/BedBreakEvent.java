package de.will_smith_007.bedwars.events;

import de.will_smith_007.bedwars.file_config.BedWarsConfig;
import de.will_smith_007.bedwars.teams.enums.BedWarsTeam;
import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BedBreakEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter
    private final Location BED_LOCATION;
    @Getter
    private final Player PLAYER;
    private final BedWarsConfig BEDWARS_CONFIG = BedWarsConfig.getInstance();
    private final BedWarsTeam[] BEDWARS_TEAMS = BedWarsTeam.values();

    public BedBreakEvent(@NonNull Player player, @NonNull Location bedLocation) {
        BED_LOCATION = bedLocation;
        PLAYER = player;
    }

    public Optional<ITeam> getTeamFromBed() {
        final World playerWorld = PLAYER.getWorld();

        for (BedWarsTeam bedwarsTeam : BEDWARS_TEAMS) {
            final Location configuredBedLocation = BEDWARS_CONFIG.getBedLocation(bedwarsTeam, playerWorld);
            if (configuredBedLocation.distance(BED_LOCATION) > 1) continue;
            return Optional.of(bedwarsTeam.getTeam());
        }

        return Optional.empty();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
