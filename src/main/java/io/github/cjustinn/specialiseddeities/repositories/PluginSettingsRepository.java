package io.github.cjustinn.specialiseddeities.repositories;

public class PluginSettingsRepository {
    // "Limits" settings.
    public static int maxGlobalDeities = -1;
    public static int maxFollowers = -1;
    public static int maxCollectiveFaith = 10000;

    // "Rates" settings.
    public static int collectiveAltarReward = 250;
    public static int collectiveItemReward = 350;
    public static int collectiveMobReward = 350;
    public static int collectiveAbandonmentPenalty = 250;

    // "Deity Management" settings.
    public static boolean allowGenderlessDeities = true;
    public static boolean removeCreatedAltarsOnAbandon = true;
    public static boolean leaderOnlyAltarManagement = false;
}
