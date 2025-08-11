package dev.smartshub.shkoth.koth.reward;

import dev.smartshub.shkoth.builder.serializer.RTagItemSerializer;
import dev.smartshub.shkoth.service.config.ConfigService;

public class PhysicalRewardAdderFactory {
    public static PhysicalRewardAdder create(ConfigService configService) {
        return new PhysicalRewardAdder(
            new ConfigRewardPersistence(configService),
            new RTagItemSerializer(),
            new BasicRewardValidator()
        );
    }
}