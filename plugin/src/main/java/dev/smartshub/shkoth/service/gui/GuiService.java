package dev.smartshub.shkoth.service.gui;


import dev.smartshub.shkoth.gui.AddPhysicalRewardGui;
import dev.smartshub.shkoth.gui.CommandGui;
import dev.smartshub.shkoth.gui.CreateKothGui;
import dev.smartshub.shkoth.gui.CreateSchedulerGui;
import dev.smartshub.shkoth.gui.ScoreboardLineEditorGui;
import org.bukkit.entity.Player;

public class GuiService {

    private final CreateKothGui createKothGui;
    private final CreateSchedulerGui createSchedulerGui;
    private final AddPhysicalRewardGui addPhysicalRewardGui;
    private final CommandGui commandGui;
    private final dev.smartshub.shkoth.gui.EditKothListGui editKothListGui;
    private final ScoreboardLineEditorGui scoreboardLineEditorGui;

    public GuiService(CreateKothGui createKothGui, CreateSchedulerGui createSchedulerGui, AddPhysicalRewardGui addPhysicalRewardGui, CommandGui commandGui, dev.smartshub.shkoth.gui.EditKothListGui editKothListGui, ScoreboardLineEditorGui scoreboardLineEditorGui){
        this.createKothGui = createKothGui;
        this.createSchedulerGui = createSchedulerGui;
        this.addPhysicalRewardGui = addPhysicalRewardGui;
        this.commandGui = commandGui;
        this.editKothListGui = editKothListGui;
        this.scoreboardLineEditorGui = scoreboardLineEditorGui;
    }

    public void openScoreboardLineEditor(Player player, boolean capturing) {
        if (scoreboardLineEditorGui != null) {
            scoreboardLineEditorGui.open(player, capturing);
        }
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

    public void openEditKothListGui(Player player) {
        if (editKothListGui != null) {
            editKothListGui.open(player);
        }
    }

}
