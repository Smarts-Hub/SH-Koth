package dev.smartshub.shkoth.command.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.koth.reward.PhysicalRewardAdder;
import dev.smartshub.shkoth.koth.reward.PhysicalRewardAdderFactory;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.notify.NotifyService;
import me.lucko.spark.paper.common.command.sender.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Suggest;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("koth")
@CommandPermission("shkoth.command.admin")
public class KothCommand {

    private final KothRegistry kothRegistry;
    private final NotifyService notifyService;
    private final ConfigService configService;
    private final PhysicalRewardAdder physicalRewardAdder;

    public KothCommand(KothRegistry kothRegistry, NotifyService notifyService, ConfigService configService) {
        this.kothRegistry = kothRegistry;
        this.notifyService = notifyService;
        this.configService = configService;
        this.physicalRewardAdder = PhysicalRewardAdderFactory.create(configService);
    }

    @Subcommand("force-start <kothId>")
    public void forceStart(BukkitCommandActor actor, @Suggest({"kothIdExample"}) String kothId){
        kothRegistry.startKoth(kothId);
        notifyService.sendChat((CommandSender) actor.sender(), "koth.force-start");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-start");
    }

    @Subcommand("force-stop <kothId>")
    public void forceStop(BukkitCommandActor actor, @Suggest({"kothIdExample"}) String kothId){
        kothRegistry.stopKoth(kothId);
        notifyService.sendChat((CommandSender) actor.sender(), "koth.force-stop");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-stop");
    }

    @Subcommand("force-stop all")
    public void forceStopAll(BukkitCommandActor actor) {
        kothRegistry.getRunning().forEach(koth -> koth.stop(KothEndEvent.EndReason.MANUAL_STOP));
        notifyService.sendChat((CommandSender) actor.sender(), "koth.force-stop-all");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-stop-all");
    }

    @Subcommand("add-physical-reward <kothId> <amount>")
    public void addPhysicalReward(BukkitCommandActor actor, @Suggest({"kothIdExample"}) String kothId, @Suggest({"1", "2", "3", "4"}) int amount) {
        // Not so clean but works for now (will be refactored later)
        Koth koth = kothRegistry.get(kothId);
        physicalRewardAdder.addRewards(koth, actor.asPlayer().getItemInHand(), amount);
        notifyService.sendChat(actor.asPlayer(), "koth.add-physical-reward");
    }

    @Subcommand("tp <kothId>")
    public void teleportToKoth(BukkitCommandActor actor, @Suggest({"kothIdExample"}) String kothId) {
        // Not so clean but works for now (will be refactored later) x2
        if(!actor.isPlayer()) return;
        actor.asPlayer().teleport(kothRegistry.get(kothId).getArea().getCenter());
        notifyService.sendChat(actor.asPlayer(), "koth.teleport");
    }

    @Subcommand("list")
    public void list(BukkitCommandActor actor) {
        // Not so clean but works for now (will be refactored later) (too) x3
        kothRegistry.getAll().forEach(koth -> {
            String message = String.format("Koth ID: %s, Status: %s", koth.getId(), koth.isRunning() ? "Running" : "Not Running");
            notifyService.sendChat((CommandSender) actor.sender(), message);
        });
    }

    @Subcommand("reload koths")
    public void reload(BukkitCommandActor actor) {
        kothRegistry.reloadKoths();
        notifyService.sendChat((CommandSender) actor.sender(), "koth.reload");
    }
}
