package dev.smartshub.shkoth.command;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.notify.NotifyService;
import me.lucko.spark.paper.common.command.sender.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Suggest;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

@Command("koth")
public class KothCommand {

    private final KothRegistry kothRegistry;
    private final NotifyService notifyService;

    public KothCommand(KothRegistry kothRegistry, NotifyService notifyService) {
        this.kothRegistry = kothRegistry;
        this.notifyService = notifyService;
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
        //TODO
    }

    @Subcommand("tp <kothId>")
    public void teleportToKoth(BukkitCommandActor actor, @Suggest({"kothIdExample"}) String kothId) {
        //TODO
    }

    @Subcommand("list")
    public void list(BukkitCommandActor actor) {
        //TODO
    }

    @Subcommand("reload")
    public void reload(BukkitCommandActor actor) {
        //TODO
    }
}
