package dev.smartshub.shkoth.registry.cache;

import dev.smartshub.shkoth.api.koth.Koth;

public class SimpleKothCache {

    private Koth lastStarted;
    private Koth lastEnded;
    private Koth lastStartCaptured;
    private Koth lastStopCaptured;

    public Koth getLastStarted() {
        return lastStarted;
    }

    public void setLastStarted(Koth lastStarted) {
        this.lastStarted = lastStarted;
    }

    public Koth getLastStartCaptured() {
        return lastStartCaptured;
    }

    public void setLastStartCaptured(Koth lastStartCaptured) {
        this.lastStartCaptured = lastStartCaptured;
    }

    public Koth getLastEnded() {
        return lastEnded;
    }

    public void setLastEnded(Koth lastEnded) {
        this.lastEnded = lastEnded;
    }

    public Koth getLastStopCaptured() {
        return lastStopCaptured;
    }

    public void setLastStopCaptured(Koth lastStopCaptured) {
        this.lastStopCaptured = lastStopCaptured;
    }
}
