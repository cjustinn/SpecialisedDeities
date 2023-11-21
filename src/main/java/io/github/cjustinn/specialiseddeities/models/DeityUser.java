package io.github.cjustinn.specialiseddeities.models;

import java.util.Date;

public class DeityUser {
    public final String uuid;
    public int patronId;
    public int faithPoints;
    public final boolean isLeader;
    public final boolean isDemigod;
    public final boolean isGod;
    public final Date pledgedDate;

    public DeityUser(
            String userId,
            int userPatronId,
            boolean userIsLeader,
            boolean userIsDemigod,
            boolean userIsGod,
            Date userPledged
    ) {
        this.uuid = userId;
        this.patronId = userPatronId;
        this.isLeader = userIsLeader;
        this.isDemigod = userIsDemigod;
        this.isGod = userIsGod;
        this.pledgedDate = userPledged;
    }
}
