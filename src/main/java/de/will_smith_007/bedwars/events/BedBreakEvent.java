package de.will_smith_007.bedwars.events;

import de.will_smith_007.bedwars.teams.interfaces.ITeam;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class BedBreakEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    @Getter
    private final Location BED_LOCATION;
    @Getter
    private final Player PLAYER;
    @Getter
    private final ITeam BED_TEAM;

    public BedBreakEvent(@NonNull Player player, @NonNull Location bedLocation, @NonNull ITeam bedTeam) {
        BED_LOCATION = bedLocation;
        PLAYER = player;
        BED_TEAM = bedTeam;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
