package dev.smartshub.shkoth.api.model.koth.tally;

import dev.smartshub.shkoth.api.model.koth.Koth;

public interface Tally {
    void handleTally(Koth koth);
}
