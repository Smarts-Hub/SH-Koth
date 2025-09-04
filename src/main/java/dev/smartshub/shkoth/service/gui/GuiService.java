package dev.smartshub.shkoth.service.gui;


import dev.smartshub.shkoth.gui.AddPhysicalRewardGui;
import dev.smartshub.shkoth.gui.CommandGui;
import dev.smartshub.shkoth.gui.CreateKothGui;
import dev.smartshub.shkoth.gui.CreateSchedulerGui;
import org.bukkit.entity.Player;

public class GuiService {

    private final CreateKothGui createKothGui;
    private final CreateSchedulerGui createSchedulerGui;
    private final AddPhysicalRewardGui addPhysicalRewardGui;
    private final CommandGui commandGui;

    public GuiService(CreateKothGui createKothGui, CreateSchedulerGui createSchedulerGui, AddPhysicalRewardGui addPhysicalRewardGui, CommandGui commandGui){
        this.createKothGui = createKothGui;
        this.createSchedulerGui = createSchedulerGui;
        this.addPhysicalRewardGui = addPhysicalRewardGui;
        this.commandGui = commandGui;
    }

    public void openCreateKothGui(Player player) {
        createKothGui.open(player);
    }

    public void openCreateSchedulerGui(Player player) {
        createSchedulerGui.open(player);
    }

    public void openAddPhysicalRewardGui(Player player) {
        addPhysicalRewardGui.open(player);
    }

    public void openCommandGui(Player player) {
        commandGui.open(player);
    }

}
