package dev.smartshub.shkoth.service.koth;

import dev.smartshub.shkoth.registry.KothRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RefreshInsideKothService {

    private final KothRegistry kothRegistry;

    public RefreshInsideKothService(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    public void refreshInsideKoth() {
        kothRegistry.getRunning().forEach(koth -> {
            for(Player player : Bukkit.getWorld(koth.getArea().worldName()).getPlayers()) {
               if(koth.isInsideArea(player)){
                   koth.playerEnter(player);
                   continue;
               }

               if(!koth.isInsideArea(player) && koth.getPlayersInside().contains(player.getUniqueId())) {
                   koth.playerLeave(player);
               }
            }
        });
    }

}
