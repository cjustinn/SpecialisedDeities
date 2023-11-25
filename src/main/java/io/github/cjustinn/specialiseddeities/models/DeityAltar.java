package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.services.DeityService;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.Date;

public class DeityAltar {
    public final Location location;
    public final int deityId;
    public final String creatorId;
    public final Date created;

    public DeityAltar(final Location loc, final int deity, final String creator, final Date createdDate) {
        this.location = loc;
        this.deityId = deity;
        this.creatorId = creator;
        this.created = createdDate;
    }

    public @Nullable Deity getDeity() {
        return DeityService.deities.getOrDefault(this.deityId, null);
    }
}
