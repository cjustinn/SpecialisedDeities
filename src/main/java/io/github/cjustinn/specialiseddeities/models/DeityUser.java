package io.github.cjustinn.specialiseddeities.models;

import io.github.cjustinn.specialiseddeities.enums.FaithPointEarnReason;
import io.github.cjustinn.specialiseddeities.enums.modelfields.UserTableField;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQueryValueType;
import io.github.cjustinn.specialiseddeities.models.SQL.DatabaseQueryValue;
import io.github.cjustinn.specialiseddeities.repositories.PluginSettingsRepository;
import io.github.cjustinn.specialiseddeities.services.DatabaseService;
import io.github.cjustinn.specialiseddeities.services.DeityService;
import io.github.cjustinn.specialiseddeities.services.LoggingService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    public boolean isLeader;
    public boolean isDemigod;
    public boolean isGod;
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
    public Deity getDeity() {
        return DeityService.deities.get(this.patronId);
    }
    public boolean abandonPledge() {
        // Remove the user from the sd_users table
        if (DatabaseService.RunUpdate(DatabaseQuery.DeleteUser, new DatabaseQueryValue[] {
                new DatabaseQueryValue(1, this.uuid, DatabaseQueryValueType.String)
        })) {
            // If the config says to remove altars created by the abandoning player, remove all altars with the current user uuid as the creator value.
            if (PluginSettingsRepository.removeCreatedAltarsOnAbandon) {
                // This needs to be implemented when the altars SQL is available.
            }

            // If the config says to apply a faith point penalty to the abandoned deity, add the transaction.
            if (PluginSettingsRepository.collectiveAbandonmentPenalty > 0) {
                if (!this.getDeity().modifyFaithPoints(-(PluginSettingsRepository.collectiveAbandonmentPenalty), this.uuid, FaithPointEarnReason.AbandonPledge)) {
                    LoggingService.writeLog(Level.WARNING, String.format(
                            "Unable to deduct configured pledge abandonment penalty from deity: %s (%d).",
                            this.getDeity().name,
                            this.patronId
                    ));
                }
            }

            // If this user was the final follower of a deity, deactivate the deity if it is not protected. If the user is NOT the final follower, and they were the leader, assign the now-oldest follower of the deity as the new leader.
            final int remainingFollowerCount = DeityService.users.values().stream().filter((user) -> user.patronId == this.patronId && !user.uuid.equals(this.uuid)).collect(Collectors.toList()).size();
            if (remainingFollowerCount > 0 && this.isLeader) {
                // Assign the oldest remaining follower as the new leader.
                Optional<DeityUser> oldestFollowerFetch = DeityService.users.values().stream()
                        .filter((user) -> user.patronId == this.patronId && !user.uuid.equals(this.uuid))
                        .reduce((oldest, current) -> oldest.pledgedDate.before(current.pledgedDate) ? oldest : current);

                if (oldestFollowerFetch.isPresent()) {
                    final DeityUser oldestFollower = oldestFollowerFetch.get();
                    if (!oldestFollower.update(new HashMap<UserTableField, DatabaseQueryValue>() {{
                        put(UserTableField.Leader, new DatabaseQueryValue(1, true, DatabaseQueryValueType.Boolean));
                    }})) {
                        LoggingService.writeLog(Level.SEVERE, String.format("Failed to update leader for deity: %s (%d).", this.getDeity().name, this.patronId));
                    }
                } else {
                    LoggingService.writeLog(Level.SEVERE, String.format("Failed to update leader for deity: %s (%d).", this.getDeity().name, this.patronId));
                }
            } else if (remainingFollowerCount == 0 && !this.getDeity().isProtected) {
                // Remove the deity from the sd_deities table.
                this.getDeity().deactivateDeity();
            }

            return true;
        } else return false;
    }

    public boolean update(final Map<UserTableField, DatabaseQueryValue> fields) {
        if (DatabaseService.RunUpdate(DatabaseQuery.UpdateUser, new DatabaseQueryValue[] {
                new DatabaseQueryValue(fields.size() + 1, this.uuid, DatabaseQueryValueType.String)
        }, fields)) {
            // Update the fields in the user object based on the UserTableField value.
            for (Map.Entry<UserTableField, DatabaseQueryValue> entry : fields.entrySet()) {
                switch(entry.getKey()) {
                    case Patron:
                        this.patronId = (int) entry.getValue().value;
                        break;
                    case Leader:
                        this.isLeader = (boolean) entry.getValue().value;
                        break;
                    case Demigod:
                        this.isDemigod = (boolean) entry.getValue().value;
                        break;
                    case God:
                        this.isGod = (boolean) entry.getValue().value;
                        break;
                }
            }

            return true;
        } else return false;
    }
}
