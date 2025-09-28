package dev.smartshub.shkoth.service.gui.menu.cache;

import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.reward.PhysicalReward;

import java.util.List;

public class KothBuilder {
    
    public static KothData buildKoth(KothTempData tempData) {
        return new KothData(
            tempData.getId(),
            tempData.getDisplayName(),
            tempData.getMaxTime(),
            tempData.getCaptureTime(),
            tempData.getArea(),
            tempData.getType(),
            tempData.isSolo(),
            tempData.isBossbarEnabled(),
            tempData.isDenyEnterWithoutTeam(),
            tempData.isCreateTeamIfNotExistsOnEnter(),
            tempData.getPhysicalRewards(),
            tempData.getCommands(),
            tempData.isScoreboardEnabled(),
            tempData.getScoreboardCapturingTitle(),
            tempData.getScoreboardCapturingContent(),
            tempData.getScoreboardWaitingTitle(),
            tempData.getScoreboardWaitingContent()
        );
    }

    public record KothData(
        String id,
        String displayName,
        int maxTime,
        int captureTime,
        Area area,
        KothType type,
        boolean isSolo,
        boolean isBossbarEnabled,
        boolean denyEnterWithoutTeam,
        boolean createTeamIfNotExistsOnEnter,
        List<PhysicalReward> physicalRewards,
        List<Commands> commands,
        boolean isScoreboardEnabled,
        String scoreboardCapturingTitle,
        List<String> scoreboardCapturingContent,
        String scoreboardWaitingTitle,
        List<String> scoreboardWaitingContent
    ) {}
}