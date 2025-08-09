package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.api.model.koth.Koth;
import dev.smartshub.shkoth.service.reward.GrantRewardsService;
import dev.smartshub.shkoth.service.reward.PerformRewardCommandsService;

public class KothRewardService {

    private final GrantRewardsService grantRewardsService;
    private final PerformRewardCommandsService performRewardCommandsService;

    public KothRewardService(Koth koth) {
        this.grantRewardsService = new GrantRewardsService(koth);
        this.performRewardCommandsService = new PerformRewardCommandsService(koth);

    }

    public void grantRewards() {
        grantRewardsService.grantRewards();
        performRewardCommandsService.performCommands();
    }

}
