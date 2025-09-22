package dev.smartshub.shkoth.loader.scheduler;

import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.loader.Loader;
import dev.smartshub.shkoth.api.schedule.SchedulerConfig;
import dev.smartshub.shkoth.builder.mapper.SchedulerConfigMapper;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.List;

public class SchedulerLoader implements Loader<List<SchedulerConfig>> {

    private final ConfigService configService;
    private final SchedulerConfigMapper schedulerMapper;

    public SchedulerLoader(ConfigService configService) {
        this.configService = configService;
        this.schedulerMapper = new SchedulerConfigMapper();
    }

    @Override
    public List<SchedulerConfig> load() {
        return schedulerMapper.map(configService.provide(ConfigType.SCHEDULERS));
    }
}
