package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQueryValueType;
import io.github.cjustinn.specialiseddeities.models.SQL.DatabaseQueryValue;
import io.github.cjustinn.specialiseddeities.services.DatabaseService;
import io.github.cjustinn.specialiseddeities.services.DeityService;

import java.util.Date;

public class DeityUser {
    // Static Functions
    public static boolean pledgeUser(final String uuid, final int deity) {
        if (!DeityService.users.containsKey(uuid)) {
            final boolean userIsGod = false;
            final boolean assumeLeadership = DeityService.getDeityFollowerCount(deity) == 0;

            if (DatabaseService.RunUpdate(DatabaseQuery.InsertUser, new DatabaseQueryValue[]{
                    new DatabaseQueryValue(1, uuid, DatabaseQueryValueType.String),
                    new DatabaseQueryValue(2, deity, DatabaseQueryValueType.Integer),
                    new DatabaseQueryValue(3, assumeLeadership, DatabaseQueryValueType.Boolean),
                    new DatabaseQueryValue(4, userIsGod, DatabaseQueryValueType.Boolean)
            })) {
                DeityService.users.put(uuid, new DeityUser(uuid, deity, assumeLeadership, false, userIsGod, new Date()));
                return true;
            } else return false;
        } else return false;
    }

    // Data Members
    public final String uuid;
    public int patronId;
    public int faithPoints;
    public final boolean isLeader;
    public final boolean isDemigod;
    public final boolean isGod;
    public final Date pledgedDate;

    // Constructors
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

    // Member Functions
}
