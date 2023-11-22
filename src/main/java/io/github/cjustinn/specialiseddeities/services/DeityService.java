package io.github.cjustinn.specialiseddeities.services;

import io.github.cjustinn.specialiseddeities.models.Deity;
import io.github.cjustinn.specialiseddeities.models.DeityDomain;
import io.github.cjustinn.specialiseddeities.models.DeityUser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    // User Functions
}
