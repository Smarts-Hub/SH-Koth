package dev.smartshub.shkoth.api.model.koth;

import dev.smartshub.shkoth.api.event.koth.*;
import dev.smartshub.shkoth.api.model.koth.guideline.KothState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KothEventDispatcher {

    //TODO: make cancellable events return isCancelled() instead of void
    // and integrate with (firstly) abstract Koth class and then impl at plugin level
    // also do it with team events, but TeamKoth should be implemented ;))

    public void fireKothStartEvent(Koth koth) {
        KothStartEvent event = new KothStartEvent(koth);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void fireKothEndEvent(Koth koth, KothEndEvent.EndReason reason) {
        KothEndEvent event = new KothEndEvent(koth, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public boolean fireKothStateChangeEvent(Koth koth, KothState oldState, KothState newState) {
        KothStateChangeEvent event = new KothStateChangeEvent(koth, oldState, newState);
        Bukkit.getPluginManager().callEvent(event);

        return event.isCancelled();
    }

    public void firePlayerEnterKothDuringRunEvent(Koth koth, Player player) {
        PlayerEnterKothDuringRunEvent event = new PlayerEnterKothDuringRunEvent(koth, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void firePlayerLeaveKothDuringRunEvent(Koth koth, Player player, boolean wasCapturing) {
        PlayerLeaveKothDuringRunEvent event = new PlayerLeaveKothDuringRunEvent(koth, player, wasCapturing);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void firePlayerStartKothCaptureEvent(Koth koth, Player player, Player previousCapturer) {
        PlayerStartKothCaptureEvent event = new PlayerStartKothCaptureEvent(koth, player, previousCapturer);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void firePlayerStopKothCaptureEvent(Koth koth, Player player, long elapsed, PlayerStopKothCaptureEvent.StopReason reason) {
        PlayerStopKothCaptureEvent event = new PlayerStopKothCaptureEvent(koth, player, elapsed, reason);
        Bukkit.getPluginManager().callEvent(event);
    }


}
