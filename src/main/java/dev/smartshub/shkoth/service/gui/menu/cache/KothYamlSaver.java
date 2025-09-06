package dev.smartshub.shkoth.service.gui.menu.cache;

import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.location.schedule.Schedule;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.service.config.ConfigService;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * The code is awful and needs to be refactored at some point.
 */
public class KothYamlSaver {
    private final ConfigService configService;
    private final String kothsFolderPath;

    public KothYamlSaver(ConfigService configService, String kothsFolderPath) {
        this.configService = configService;
        this.kothsFolderPath = kothsFolderPath;
    }

    public boolean saveToYaml(KothTempData tempData) {
        try {

            File kothFolder = new File(kothsFolderPath);
            if (!kothFolder.exists()) {
                boolean created = kothFolder.mkdirs();
            }

            File kothFile = new File(kothFolder, tempData.getId() + ".yml");

            YamlConfiguration yaml = new YamlConfiguration();

            yaml.set("world", tempData.getWorldName());
            yaml.set("display-name", tempData.getDisplayName());
            yaml.set("boss-bar", tempData.isBossbarEnabled());
            yaml.set("max-duration", tempData.getMaxTime());
            yaml.set("capture-time", tempData.getCaptureTime());
            yaml.set("type", tempData.getType().name().toLowerCase());
            yaml.set("solo-koth", tempData.isSolo());
            yaml.set("create-team-if-not-exists", tempData.isCreateTeamIfNotExistsOnEnter());
            yaml.set("deny-entry-if-not-in-team", tempData.isDenyEnterWithoutTeam());


            if (tempData.getCorner1() != null) {
                yaml.set("corner-1.x", tempData.getCorner1().x());
                yaml.set("corner-1.y", tempData.getCorner1().y());
                yaml.set("corner-1.z", tempData.getCorner1().z());
            }

            if (tempData.getCorner2() != null) {
                yaml.set("corner-2.x", tempData.getCorner2().x());
                yaml.set("corner-2.y", tempData.getCorner2().y());
                yaml.set("corner-2.z", tempData.getCorner2().z());
            }

            saveSchedules(yaml, tempData.getSchedules());
            saveCommands(yaml, tempData.getCommands());
            savePhysicalRewards(yaml, tempData.getPhysicalRewards());
            saveScoreboard(yaml, tempData);

            yaml.save(kothFile);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveSchedules(YamlConfiguration yaml, List<Schedule> schedules) {

        if (schedules.isEmpty()) {
            return;
        }

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            String scheduleKey = "schedule." + (i + 1);

            yaml.set(scheduleKey + ".day", schedule.day().name().toLowerCase());
            yaml.set(scheduleKey + ".hour", schedule.time().format(timeFormatter));

        }
    }

    private void saveCommands(YamlConfiguration yaml, List<Commands> commandsList) {

        if (commandsList.isEmpty()) {
            yaml.set("commands-perform.start", List.of());
            yaml.set("commands-perform.end", List.of());
            yaml.set("commands-perform.to-winners", List.of());
            return;
        }

        Commands commands = commandsList.get(0);

        yaml.set("commands-perform.start", commands.startCommands());
        yaml.set("commands-perform.end", commands.endCommands());
        yaml.set("commands-perform.to-winners", commands.winnersCommands());
    }

    private void savePhysicalRewards(YamlConfiguration yaml, List<PhysicalReward> physicalRewards) {
        if (physicalRewards.isEmpty()) {
            yaml.set("physical-rewards", Map.of());
            return;
        }

        Map<String, Object> rewardsMap = new HashMap<>();
        for (int i = 0; i < physicalRewards.size(); i++) {
            PhysicalReward reward = physicalRewards.get(i);
            rewardsMap.put(String.valueOf(i + 1), convertPhysicalRewardToMap(reward));
        }

        yaml.set("physical-rewards", rewardsMap);
    }

    private Map<String, Object> convertPhysicalRewardToMap(PhysicalReward reward) {
        Map<String, Object> rewardMap = new HashMap<>();
        return rewardMap;
    }

    private void saveScoreboard(YamlConfiguration yaml, KothTempData tempData) {

        yaml.set("scoreboard.enabled", tempData.isScoreboardEnabled());

        if (tempData.isScoreboardEnabled()) {
            yaml.set("scoreboard.running.title", tempData.getScoreboardWaitingTitle());
            yaml.set("scoreboard.running.lines", tempData.getScoreboardWaitingContent());

            yaml.set("scoreboard.capturing.title", tempData.getScoreboardCapturingTitle());
            yaml.set("scoreboard.capturing.lines", tempData.getScoreboardCapturingContent());

        }
    }
}