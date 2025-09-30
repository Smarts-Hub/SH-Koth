package dev.smartshub.shkoth.listener.player;

import dev.smartshub.shkoth.SHKoth;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {

    private final SHKoth plugin;
    private final KothToRegisterCache kothToRegisterCache;
    private final MessageParser parser;
    private final GuiService guiService;

    public AsyncChatListener(SHKoth plugin, KothToRegisterCache kothToRegisterCache, MessageParser parser, GuiService guiService) {
        this.plugin = plugin;
        this.kothToRegisterCache = kothToRegisterCache;
        this.parser = parser;
        this.guiService = guiService;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (!kothToRegisterCache.isWaitingToChat(event.getPlayer().getUniqueId())) return;
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(plugin, () -> {
            String message = PlainTextComponentSerializer.plainText().serialize(event.message());

            if (message.equalsIgnoreCase("cancel")) {
                kothToRegisterCache.cancelWaiting(event.getPlayer().getUniqueId());
                event.getPlayer().sendMessage(parser.parse("<red>KoTH filling cancelled."));
                guiService.openCreateKothGui(event.getPlayer());
                return;
            }

            kothToRegisterCache.fillChatInput(event.getPlayer().getUniqueId(), message);
            kothToRegisterCache.cancelWaiting(event.getPlayer().getUniqueId());
        });
    }

}