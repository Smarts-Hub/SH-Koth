package dev.smartshub.shkoth.api.koth;

import dev.smartshub.shkoth.api.event.koth.*;
import dev.smartshub.shkoth.api.koth.guideline.KothState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KothEventDispatcher {

    public KothStartEvent fireKothStartEvent(Koth koth) {
        KothStartEvent event = new KothStartEvent(koth);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public void fireKothEndEvent(Koth koth, KothEndEvent.EndReason reason) {
        KothEndEvent event = new KothEndEvent(koth, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public KothStateChangeEvent fireKothStateChangeEvent(Koth koth, KothState oldState, KothState newState) {
        KothStateChangeEvent event = new KothStateChangeEvent(koth, oldState, newState);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public PlayerEnterKothDuringRunEvent firePlayerEnterKothDuringRunEvent(Koth koth, Player player) {
        PlayerEnterKothDuringRunEvent event = new PlayerEnterKothDuringRunEvent(koth, player);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public void firePlayerLeaveKothDuringRunEvent(Koth koth, Player player, boolean wasCapturing) {
        PlayerLeaveKothDuringRunEvent event = new PlayerLeaveKothDuringRunEvent(koth, player, wasCapturing);
        Bukkit.getPluginManager().callEvent(event);
    }

    public PlayerStartKothCaptureEvent firePlayerStartKothCaptureEvent(Koth koth, Player player, UUID previousCapturer) {
        PlayerStartKothCaptureEvent event = new PlayerStartKothCaptureEvent(koth, player, previousCapturer);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public void firePlayerStopKothCaptureEvent(Koth koth, Player player, long elapsed, PlayerStopKothCaptureEvent.StopReason reason) {
        PlayerStopKothCaptureEvent event = new PlayerStopKothCaptureEvent(koth, player, elapsed, reason);
        Bukkit.getPluginManager().callEvent(event);
    }


}
