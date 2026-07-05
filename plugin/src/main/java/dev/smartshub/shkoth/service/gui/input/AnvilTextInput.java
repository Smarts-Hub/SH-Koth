package dev.smartshub.shkoth.service.gui.input;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Collects a single line of text from a player through a real anvil rename field.
 *
 * <p>Backed by the AnvilGUI library, which opens a genuine anvil menu (so the rename
 * text field actually works), cancels item movement (players cannot steal the input
 * item), and manages its own listeners. This replaces the previous hand-rolled
 * {@code Bukkit.createInventory(ANVIL)} approach, which produced an anvil with no menu
 * backend: rename did not apply, items could be dragged into the inventory, and
 * closing did not reliably return to the caller.
 *
 * <p>Flow:
 * <ol>
 *   <li>{@link #prompt(Player, String, Consumer, Runnable)} opens an anvil pre-filled
 *       with {@code initial}.</li>
 *   <li>Clicking the output slot confirms: {@code onConfirm} is invoked with the current
 *       text on the next tick.</li>
 *   <li>Closing the anvil without confirming cancels: {@code onCancel} is invoked on the
 *       next tick.</li>
 * </ol>
 * Both callbacks are deferred by one tick so they may safely open another inventory.
 */
public final class AnvilTextInput {

    private static volatile AnvilTextInput instance;

    private final Plugin plugin;

    public AnvilTextInput(Plugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public static AnvilTextInput get() {
        return instance;
    }

    public void prompt(Player player, String initial,
                       Consumer<String> onConfirm, Runnable onCancel) {
        // Guards against onClose firing the cancel path after a confirm.
        AtomicBoolean confirmed = new AtomicBoolean(false);

        new AnvilGUI.Builder()
                .plugin(plugin)
                .text(initial == null ? "" : initial)
                .itemLeft(new ItemStack(Material.PAPER))
                .onClick((slot, state) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    confirmed.set(true);
                    String text = state.getText() == null ? "" : state.getText();
                    return List.of(
                            AnvilGUI.ResponseAction.close(),
                            AnvilGUI.ResponseAction.run(() -> runNextTick(() -> onConfirm.accept(text))));
                })
                .onClose(state -> {
                    if (confirmed.get()) {
                        return;
                    }
                    if (onCancel != null) {
                        runNextTick(onCancel);
                    }
                })
                .open(player);
    }

    private void runNextTick(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                task.run();
            } catch (Throwable t) {
                plugin.getLogger().severe("AnvilTextInput callback threw: " + t);
            }
        });
    }
}
