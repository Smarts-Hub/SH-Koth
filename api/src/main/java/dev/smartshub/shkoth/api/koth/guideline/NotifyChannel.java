package dev.smartshub.shkoth.api.koth.guideline;

import java.util.EnumSet;
import java.util.Set;

/**
 * Individual channels available for KOTH notifications. NotifyService can filter
 * on this EnumSet. For the mapping to the legacy {@link NotifyType}, see
 * {@link NotifyType#channels()}.
 */
public enum NotifyChannel {
    TITLE,
    ACTIONBAR,
    CHAT,
    BOSSBAR;

    public static final Set<NotifyChannel> ALL_CHANNELS = EnumSet.allOf(NotifyChannel.class);
}
