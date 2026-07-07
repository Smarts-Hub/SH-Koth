package dev.smartshub.shkoth.api.koth.guideline;

import java.util.EnumSet;
import java.util.Set;

/**
 * Preset notify display modes. Internally these are handled as sets of
 * {@link NotifyChannel}; this enum is just a convenient alias for the
 * {@code notify-type} config option.
 */
public enum NotifyType {
    TITLE("Title", EnumSet.of(NotifyChannel.TITLE, NotifyChannel.ACTIONBAR)),
    CHAT("Chat", EnumSet.of(NotifyChannel.CHAT)),
    BOSSBAR("Bossbar", EnumSet.of(NotifyChannel.BOSSBAR)),
    ALL("All", EnumSet.allOf(NotifyChannel.class));

    private final String displayName;
    private final Set<NotifyChannel> channels;

    NotifyType(String displayName, Set<NotifyChannel> channels) {
        this.displayName = displayName;
        this.channels = channels;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the set of channels this NotifyType maps to. NotifyService uses it
     * to decide whether a given channel should be sent.
     */
    public Set<NotifyChannel> channels() {
        return EnumSet.copyOf(channels);
    }

    public boolean includes(NotifyChannel channel) {
        return channels.contains(channel);
    }
}
