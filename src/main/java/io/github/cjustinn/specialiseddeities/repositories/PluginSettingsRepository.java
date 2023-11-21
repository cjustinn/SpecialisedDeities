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

    // "Deity Creation" settings.
    public static boolean allowGenderlessDeities = true;
}
