package dev.smartshub.shkoth.service.gui.menu.cache;

import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.service.config.ConfigService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

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

    /**
     * Used by the editor flow: build the YAML in memory on the main thread, then
     * write it asynchronously. The I/O result is not awaited; failures are only logged.
     */
    public CompletableFuture<Boolean> saveToYamlAsync(KothTempData tempData) {
        try {
            YamlConfiguration yaml = buildYaml(tempData);
            File kothFile = resolveFile(tempData.getId());
            return CompletableFuture.supplyAsync(() -> {
                try {
                    yaml.save(kothFile);
                    return true;
                } catch (IOException e) {
                    JavaPlugin.getProvidingPlugin(KothYamlSaver.class).getLogger()
                            .log(Level.SEVERE, "Async save of KOTH config failed (" + tempData.getId() + ")", e);
                    return false;
                }
            });
        } catch (Exception e) {
            JavaPlugin.getProvidingPlugin(KothYamlSaver.class).getLogger()
                    .log(Level.SEVERE, "Failed to assemble KOTH YAML (" + tempData.getId() + ")", e);
            return CompletableFuture.completedFuture(false);
        }
    }

    public boolean saveToYaml(KothTempData tempData) {
        try {
            YamlConfiguration yaml = buildYaml(tempData);
            File kothFile = resolveFile(tempData.getId());
            yaml.save(kothFile);
            return true;
        } catch (IOException e) {
            JavaPlugin.getProvidingPlugin(KothYamlSaver.class).getLogger()
                    .log(Level.SEVERE, "Failed to save KOTH config (" + tempData.getId() + ")", e);
            return false;
        } catch (Exception e) {
            JavaPlugin.getProvidingPlugin(KothYamlSaver.class).getLogger()
                    .log(Level.SEVERE, "Failed to assemble KOTH YAML (" + tempData.getId() + ")", e);
            return false;
        }
    }

    private File resolveFile(String id) {
        File kothFolder = new File(kothsFolderPath);
        if (!kothFolder.exists()) kothFolder.mkdirs();
        return new File(kothFolder, id + ".yml");
    }

    private YamlConfiguration buildYaml(KothTempData tempData) {
        YamlConfiguration yaml = new YamlConfiguration();

            yaml.set("world", tempData.getWorldName());
            yaml.set("display-name", tempData.getDisplayName());
            yaml.set("boss-bar", tempData.isBossbarEnabled());
            yaml.set("max-duration", tempData.getMaxTime());
            yaml.set("capture-time", tempData.getCaptureTime());
            yaml.set("type", tempData.getType().name().toLowerCase());
            yaml.set("notify-type", tempData.getNotifyType().name().toLowerCase());
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

            saveCommands(yaml, tempData.getCommands());
            savePhysicalRewards(yaml, tempData.getPhysicalRewards());
            saveScoreboard(yaml, tempData);

            return yaml;
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
        rewardMap.put("amount", reward.amount());
        rewardMap.put("item", com.saicone.rtag.item.ItemTagStream.INSTANCE.toBase64(new org.bukkit.inventory.ItemStack[]{reward.item()}));
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