package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQuery;
import io.github.cjustinn.specialiseddeities.enums.queries.DatabaseQueryValueType;
import io.github.cjustinn.specialiseddeities.models.Deity;
import io.github.cjustinn.specialiseddeities.models.DeityDomain;
import io.github.cjustinn.specialiseddeities.models.DeityUser;
import io.github.cjustinn.specialiseddeities.models.SQL.DatabaseQueryValue;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class DeityService {
    public static List<DeityDomain> domains = new ArrayList<>();
    public static Map<Integer, Deity> deities = new HashMap<Integer, Deity>();
    public static Map<String, DeityUser> users = new HashMap<String, DeityUser>();

    // Domain Functions
    public static @Nullable DeityDomain getDomainById(final String id) {
        for (final DeityDomain domain : domains) {
            if (domain.id.equals(id)) {
                return domain;
            }
        }

        return null;
    }

    // Deity Functions
    public static int getDeityCountByDomain(final String domainId) {
        return deities.values().stream().filter((deity) -> deity.domainId == domainId).collect(Collectors.toList()).size();
    }

    public static int getDeityFollowerCount(final int deityId) {
        return users.values().stream().filter((user) -> user.patronId == deityId).collect(Collectors.toList()).size();
    }

    public static boolean deityExistsByName(final String name) {
        return deities.values().stream().anyMatch((deity) -> deity.name.toLowerCase().equals(name.toLowerCase()));
    }

    public static @Nullable Deity getDeityByName(final String name) {
        for (final Deity deity : deities.values()) {
            if (deity.name.toLowerCase().equals(name.toLowerCase())) {
                return deity;
            }
        }
        return null;
    }

    // User Functions
    public static boolean createUser(final String uuid, final int deityId, final boolean isLeader, final boolean isGod) {
        if (DatabaseService.RunUpdate(DatabaseQuery.InsertUser, new DatabaseQueryValue[] {
                new DatabaseQueryValue(1, uuid, DatabaseQueryValueType.String),
                new DatabaseQueryValue(2, deityId, DatabaseQueryValueType.Integer),
                new DatabaseQueryValue(3, isLeader, DatabaseQueryValueType.Boolean),
                new DatabaseQueryValue(4, isGod, DatabaseQueryValueType.Boolean)
        })) {
            users.put(uuid, new DeityUser(
                    uuid, deityId, isLeader, false, isGod, new Date()
            ));

            return true;
        } else return false;
    }

    public static boolean createUser(final String uuid, final int deityId) {
        return createUser(uuid, deityId, false, false);
    }
}
