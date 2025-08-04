package dev.smartshub.shkoth.api.model.koth.tally;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.api.model.koth.guideline.KothType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TallyFactory {
    private static final Map<KothType, Function<Koth, Tally>> registry = new HashMap<>();

    public static void register(KothType type, Function<Koth, Tally> constructor) {
        registry.put(type, constructor);
    }

    public static Tally create(KothType type, Koth koth) {
        Function<Koth, Tally> constructor = registry.get(type);
        if (constructor == null)
            throw new IllegalStateException("No Tally registered for " + type);
        return constructor.apply(koth);
    }
}
