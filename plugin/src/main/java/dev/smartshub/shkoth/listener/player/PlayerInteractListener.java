package dev.smartshub.shkoth.listener.player;

import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.location.Corner;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.service.wand.WandService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final KothToRegisterCache kothToRegisterCache;
    private final GuiService guiService;
    private final MessageParser parser;
    private final WandService wandService;

    public PlayerInteractListener(KothToRegisterCache kothToRegisterCache, GuiService guiService, MessageParser parser, WandService wandService) {
        this.kothToRegisterCache = kothToRegisterCache;
        this.guiService = guiService;
        this.parser = parser;
        this.wandService = wandService;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(!event.getPlayer().hasPermission("shkoth.admin")) return;

        var kothData = kothToRegisterCache.getKothToRegister(event.getPlayer().getUniqueId());

        if(!wandService.isWand(event.getItem())) return;
        event.setCancelled(true);

        if(event.getPlayer().isSneaking()){
            event.setCancelled(true);
            var coner1 = kothData.getCorner1();
            var coner2 = kothData.getCorner2();
            if(coner1 == null || coner2 == null){
                event.getPlayer().sendMessage(parser.parse("<red>You need to select both corners before saving the KoTH!"));
                return;
            }

            event.getItem().setAmount(0);
            kothData.setArea(new Area(kothData.getWorldName(), coner1, coner2));
            event.getPlayer().sendMessage(parser.parse("<green>Arena saved correctly!"));
            guiService.openCreateKothGui(event.getPlayer());
            return;
        }

        if(event.getAction().isRightClick()){
            var loc = event.getClickedBlock().getLocation();
            kothData.setCorner1(new Corner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            kothData.setWorldName(loc.getWorld().getName());
            event.getPlayer().sendMessage(parser.parse("<gold>Position 1 set to <yellow>" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
            return;
        }

        if(event.getAction().isLeftClick()){
            var loc = event.getClickedBlock().getLocation();
            kothData.setCorner2(new Corner(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            event.getPlayer().sendMessage(parser.parse("<gold>Position 2 set to <yellow>" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
        }

    }

}
