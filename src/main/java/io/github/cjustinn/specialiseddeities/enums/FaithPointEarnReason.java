package io.github.cjustinn.specialiseddeities.enums;

import javax.annotation.Nullable;

public enum FaithPointEarnReason {
    Pray("prayer"),
    SacrificeItem("sacrifice_item"),
    SacrificeMob("sacrifice_mob"),
    AbandonPledge("pledge_abandoned"),
    Pledge("pledge_created");

    public final String Code;
    FaithPointEarnReason(final String code) {
        this.Code = code;
    }

    public static @Nullable FaithPointEarnReason getByCode(final String code) {
        for (FaithPointEarnReason reason : FaithPointEarnReason.values()) {
            if (reason.Code.equals(code)) {
                return reason;
            }
        }

        return null;
    }
}
