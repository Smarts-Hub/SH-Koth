package dev.smartshub.shkoth.service.gui.menu.cache;

import dev.smartshub.shkoth.api.koth.command.Commands;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.api.location.Area;
import dev.smartshub.shkoth.api.location.Corner;
import dev.smartshub.shkoth.api.location.schedule.Schedule;
import dev.smartshub.shkoth.api.reward.PhysicalReward;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.service.gui.menu.aux.WaitingToFill;
import org.bukkit.Bukkit;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
 * This class holds temporary data for a KOTH (King of the Hill) event being created via a GUI.
 * The code is awful and needs to be refactored at some point.
 */


public class KothTempData {

    private final GuiService guiService;

    private final UUID creatorUUID;
    private boolean waitingToChat = false;
    private WaitingToFill waitingToFill = WaitingToFill.NONE;

    private String id = "example";
    private String displayName = "Example";
    private int maxTime = 600;
    private int captureTime = 30;
    private Area area = null;
    private Corner corner1 = null;
    private Corner corner2 = null;
    private String worldName = null;
    private KothType type = KothType.CAPTURE;
    private boolean isSolo = true;
    private boolean isBossbarEnabled = true;
    private boolean denyEnterWithoutTeam = false;
    private boolean createTeamIfNotExistsOnEnter = true;
    private List<PhysicalReward> physicalRewards = new ArrayList<>();
    private List<Schedule> schedules = new ArrayList<>();
    private DayOfWeek tempDay = DayOfWeek.FRIDAY;
    private int hour = 20;
    private int minute = 30;
    private Commands commands = null;
    private List<String> startCommands = new ArrayList<>();
    private List<String> endCommands = new ArrayList<>();
    private List<String> winnersCommands = new ArrayList<>();
    private boolean isScoreboardEnabled = true;
    private String scoreboardCapturingTitle = "";
    private String scoreboardWaitingTitle = "";
    private List<String> scoreboardCapturingContent = new ArrayList<>();
    private List<String> scoreboardWaitingContent = new ArrayList<>();

    private Schedule tempSchedule = null;

    public void fillChatInput(String input){
        if(!waitingToChat) return;
        try {
            switch (waitingToFill){
                case ID -> setId(input);
                case DISPLAYNAME -> setDisplayName(input);
                case SCHEDULE_HOUR -> {
                    int hour = Integer.parseInt(input);
                    if(hour >= 0 && hour <= 23) {
                        if(tempSchedule == null) {
                            tempSchedule = new Schedule(DayOfWeek.MONDAY, LocalTime.of(hour, 0));
                        } else {
                            tempSchedule = new Schedule(tempSchedule.day(), LocalTime.of(hour, tempSchedule.time().getMinute()));
                        }
                    }
                }
                case SCHDULE_DAY -> {
                    int dayNum = Integer.parseInt(input);
                    if(dayNum >= 1 && dayNum <= 7) {
                        DayOfWeek day = DayOfWeek.of(dayNum);
                        if(tempSchedule == null) {
                            tempSchedule = new Schedule(day, LocalTime.of(12, 0));
                        } else {
                            tempSchedule = new Schedule(day, tempSchedule.time());
                        }
                        addSchedule(tempSchedule);
                        tempSchedule = null;
                    }
                }
                case WIN_COMMAND -> {
                    winnersCommands.add(input);
                    guiService.openCommandGui(Bukkit.getPlayer(creatorUUID));
                }
                case START_COMMAND -> {
                    startCommands.add(input);
                    guiService.openCommandGui(Bukkit.getPlayer(creatorUUID));
                }
                case END_COMMAND -> {
                    endCommands.add(input);
                    guiService.openCommandGui(Bukkit.getPlayer(creatorUUID));
                }
                case BOARD_CAPTURING_TITLE -> setScoreboardCapturingTitle(input);
                case BOARD_CAPTURING_LINE -> addCapturingLine(input);
                case BOARD_WAITING_TITLE -> setScoreboardWaitingTitle(input);
                case BOARD_WAITING_LINE -> addWaitingLine(input);
                case NONE -> {}
            }
        } catch (NumberFormatException e) {

        }
        waitingToFill = WaitingToFill.NONE;
        waitingToChat = false;
    }

    public KothTempData(GuiService guiService, UUID creatorUUID) {
        this.guiService = guiService;
        this.creatorUUID = creatorUUID;
    }

    public void addCapturingLine(String line) {
        scoreboardCapturingContent.add(line);
    }

    public void removeLastCapturingLine() {
        if(!scoreboardCapturingContent.isEmpty()) {
            scoreboardCapturingContent.remove(scoreboardCapturingContent.size() - 1);
        }
    }

    public void clearCapturingLines() {
        scoreboardCapturingContent.clear();
    }

    public void confirmCapturingLines() {
        scoreboardCapturingContent = new ArrayList<>(scoreboardCapturingContent);
    }

    public void addWaitingLine(String line) {
        scoreboardWaitingContent.add(line);
    }

    public void removeLastWaitingLine() {
        if(!scoreboardWaitingContent.isEmpty()) {
            scoreboardWaitingContent.remove(scoreboardWaitingContent.size() - 1);
        }
    }

    public void clearWaitingLines() {
        scoreboardWaitingContent.clear();
    }

    public void confirmWaitingLines() {
        scoreboardWaitingContent = new ArrayList<>(scoreboardWaitingContent);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public void removeLastSchedule() {
        if(!schedules.isEmpty()) {
            schedules.remove(schedules.size() - 1);
        }
    }

    public void clearSchedules() {
        schedules.clear();
    }

    public void setTempDay(DayOfWeek day) {
        this.tempDay = day;
    }

    public DayOfWeek getTempDay() {
        return tempDay;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void removeLastPhysicalReward() {
        if(!physicalRewards.isEmpty()) {
            physicalRewards.remove(physicalRewards.size() - 1);
        }
    }

    public void clearPhysicalRewards() {
        physicalRewards.clear();
    }

    public void addStartCommand(String command) {
        startCommands.add(command);
    }

    public void removeLastStartCommand() {
        if(!startCommands.isEmpty()) {
            startCommands.remove(startCommands.size() - 1);
        }
    }

    public void clearStartCommands() {
        startCommands.clear();
    }

    public void addEndCommand(String command) {
        endCommands.add(command);
    }

    public void removeLastEndCommand() {
        if(!endCommands.isEmpty()) {
            endCommands.remove(endCommands.size() - 1);
        }
    }

    public void clearEndCommands() {
        endCommands.clear();
    }

    public void addWinnerCommand(String command) {
        winnersCommands.add(command);
    }

    public void removeLastWinnerCommand() {
        if(!winnersCommands.isEmpty()) {
            winnersCommands.remove(winnersCommands.size() - 1);
        }
    }

    public void clearWinnerCommands() {
        winnersCommands.clear();
    }

    public Commands buildCommands() {
        return new Commands(
                new ArrayList<>(startCommands),
                new ArrayList<>(endCommands),
                new ArrayList<>(winnersCommands)
        );
    }

    public List<String> getTempCapturingLines() {
        return new ArrayList<>(scoreboardCapturingContent);
    }

    public List<String> getTempWaitingLines() {
        return new ArrayList<>(scoreboardWaitingContent);
    }

    public List<String> getStartCommands() {
        return new ArrayList<>(startCommands);
    }

    public List<String> getEndCommands() {
        return new ArrayList<>(endCommands);
    }

    public List<String> getWinnersCommands() {
        return new ArrayList<>(winnersCommands);
    }

    public void setWaitingToFill(WaitingToFill waitingToFill) {
        this.waitingToFill = waitingToFill;
        this.waitingToChat = true;
    }

    public void cancelWaiting() {
        this.waitingToChat = false;
        this.waitingToFill = WaitingToFill.NONE;
    }

    public WaitingToFill getWaitingToFill() {
        return waitingToFill;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(int captureTime) {
        this.captureTime = captureTime;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setCorner1(Corner corner1) {
        this.corner1 = corner1;
    }

    public void setCorner2(Corner corner2) {
        this.corner2 = corner2;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Corner getCorner1() {
        return corner1;
    }

    public Corner getCorner2() {
        return corner2;
    }

    public String getWorldName() {
        return worldName;
    }

    public KothType getType() {
        return type;
    }

    public boolean isCaptureType() {
        return type == KothType.CAPTURE;
    }

    public void setType(KothType type) {
        this.type = type;
    }

    public boolean isSolo() {
        return isSolo;
    }

    public void setSolo(boolean solo) {
        isSolo = solo;
    }

    public boolean isBossbarEnabled() {
        return isBossbarEnabled;
    }

    public void setBossbarEnabled(boolean bossbarEnabled) {
        isBossbarEnabled = bossbarEnabled;
    }

    public boolean isDenyEnterWithoutTeam() {
        return denyEnterWithoutTeam;
    }

    public void setDenyEnterWithoutTeam(boolean denyEnterWithoutTeam) {
        this.denyEnterWithoutTeam = denyEnterWithoutTeam;
    }

    public boolean isCreateTeamIfNotExistsOnEnter() {
        return createTeamIfNotExistsOnEnter;
    }

    public void setCreateTeamIfNotExistsOnEnter(boolean createTeamIfNotExistsOnEnter) {
        this.createTeamIfNotExistsOnEnter = createTeamIfNotExistsOnEnter;
    }

    public List<PhysicalReward> getPhysicalRewards() {
        return physicalRewards;
    }

    public void setPhysicalRewards(List<PhysicalReward> physicalRewards) {
        this.physicalRewards = physicalRewards;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public List<Commands> getCommands() {
        List<Commands> commandsList = new ArrayList<>();
        if(!startCommands.isEmpty() || !endCommands.isEmpty() || !winnersCommands.isEmpty()) {
            commandsList.add(buildCommands());
        }
        return commandsList;
    }

    public void setCommands(Commands commands) {
        this.commands = commands;
    }

    public boolean isScoreboardEnabled() {
        return isScoreboardEnabled;
    }

    public void setScoreboardEnabled(boolean scoreboardEnabled) {
        isScoreboardEnabled = scoreboardEnabled;
    }

    public String getScoreboardCapturingTitle() {
        return scoreboardCapturingTitle;
    }

    public void setScoreboardCapturingTitle(String scoreboardCapturingTitle) {
        this.scoreboardCapturingTitle = scoreboardCapturingTitle;
    }

    public List<String> getScoreboardCapturingContent() {
        return scoreboardCapturingContent;
    }

    public void setScoreboardCapturingContent(List<String> scoreboardCapturingContent) {
        this.scoreboardCapturingContent = scoreboardCapturingContent;
    }

    public String getScoreboardWaitingTitle() {
        return scoreboardWaitingTitle;
    }

    public void setScoreboardWaitingTitle(String scoreboardWaitingTitle) {
        this.scoreboardWaitingTitle = scoreboardWaitingTitle;
    }

    public List<String> getScoreboardWaitingContent() {
        return scoreboardWaitingContent;
    }

    public void setScoreboardWaitingContent(List<String> scoreboardWaitingContent) {
        this.scoreboardWaitingContent = scoreboardWaitingContent;
    }

    public UUID getCreatorUUID() {
        return creatorUUID;
    }

    public boolean isWaitingToChat() {
        return waitingToChat;
    }
}