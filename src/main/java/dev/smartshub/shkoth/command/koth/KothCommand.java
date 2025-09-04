package dev.smartshub.shkoth.command.koth;

import dev.smartshub.shkoth.api.event.koth.KothEndEvent;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.koth.reward.PhysicalRewardAdder;
import dev.smartshub.shkoth.koth.reward.PhysicalRewardAdderFactory;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.storage.cache.PushStackCache;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("koth")
@CommandPermission("shkoth.admin")
public class KothCommand {

    private final KothRegistry kothRegistry;
    private final NotifyService notifyService;
    private final ConfigService configService;
    private final GuiService guiService;
    private final PhysicalRewardAdder physicalRewardAdder;

    public KothCommand(KothRegistry kothRegistry, NotifyService notifyService, ConfigService configService, GuiService guiService) {
        this.kothRegistry = kothRegistry;
        this.notifyService = notifyService;
        this.configService = configService;
        this.physicalRewardAdder = PhysicalRewardAdderFactory.create(configService);
        this.guiService = guiService;
    }

    @Subcommand("create")
    public void createKoth(BukkitCommandActor actor) {
        if(!actor.isPlayer()) return;
        guiService.openCreateKothGui(actor.asPlayer());
    }

    @Subcommand("force-start")
    public void forceStart(BukkitCommandActor actor, Koth koth){
        kothRegistry.startKoth(koth.getId());
        PushStackCache.pushArgs(koth.getDisplayName());
        notifyService.sendChat(actor.sender(), "koth.force-start");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-start");
    }

    @Subcommand("force-stop")
    public void forceStop(BukkitCommandActor actor,Koth koth){
       kothRegistry.stopKoth(koth.getId());
        PushStackCache.pushArgs(koth.getDisplayName());
        notifyService.sendChat(actor.sender(), "koth.force-stop");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-stop");
    }

    @Subcommand("force-stop all")
    public void forceStopAll(BukkitCommandActor actor) {
        kothRegistry.getRunning().forEach(koth -> koth.stop(KothEndEvent.EndReason.MANUAL_STOP));
        notifyService.sendChat(actor.sender(), "koth.force-stop-all");
        notifyService.sendBroadcastListToOnlinePlayers("koth.force-stop-all");
    }

    @Subcommand("add-physical-reward")
    public void addPhysicalReward(BukkitCommandActor actor, Koth koth, int amount) {
        if(!actor.isPlayer()) return;
        // Not so clean but works for now (will be refactored later)
        physicalRewardAdder.addRewards(koth, actor.asPlayer().getItemInHand(), amount);
        notifyService.sendChat(actor.asPlayer(), "koth.add-physical-reward");
    }

    @Subcommand("tp")
    public void teleportToKoth(BukkitCommandActor actor, Koth koth) {
        if(!actor.isPlayer()) return;
        // Not so clean but works for now (will be refactored later) x2
        actor.asPlayer().teleport(koth.getArea().getCenter());
        PushStackCache.pushArgs(koth.getDisplayName());
        notifyService.sendChat(actor.asPlayer(), "koth.teleport");
    }

    @Subcommand("list")
    public void list(BukkitCommandActor actor) {
        // Not so clean but works for now (will be refactored later) (too) x3
        kothRegistry.getAll().forEach(koth -> {
            String message = String.format("Koth ID: %s, Status: %s", koth.getId(), koth.isRunning() ? "Running" : "Not Running");
            notifyService.sendRawMessage(actor.sender(), message);
        });
    }

    @Subcommand("reload")
    public void reload(BukkitCommandActor actor) {
        notifyService.sendChat(actor.sender(), "koth.reload");
        configService.reloadAll();
        kothRegistry.reloadKoths();
    }

}
