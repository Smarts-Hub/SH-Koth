package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.KothAPIProvider;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.command.handler.exception.ExceptionHandler;
import dev.smartshub.shkoth.command.handler.parameter.KothParameterType;
import dev.smartshub.shkoth.command.handler.parameter.NumberParameterType;
import dev.smartshub.shkoth.command.koth.KothCommand;
import dev.smartshub.shkoth.command.team.TeamCommand;
import dev.smartshub.shkoth.gui.AddPhysicalRewardGui;
import dev.smartshub.shkoth.gui.CommandGui;
import dev.smartshub.shkoth.gui.CreateSchedulerGui;
import dev.smartshub.shkoth.hook.bstats.Metrics;
import dev.smartshub.shkoth.hook.placeholder.PlaceholderAPIHook;
import dev.smartshub.shkoth.koth.ticking.KothTicker;
import dev.smartshub.shkoth.listener.koth.*;
import dev.smartshub.shkoth.listener.player.AsyncChatListener;
import dev.smartshub.shkoth.listener.player.PlayerInteractListener;
import dev.smartshub.shkoth.listener.player.PlayerJoinListener;
import dev.smartshub.shkoth.listener.player.PlayerQuitListener;
import dev.smartshub.shkoth.listener.team.*;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.api.koth.tally.TallyFactory;
import dev.smartshub.shkoth.koth.tally.capture.CaptureTally;
import dev.smartshub.shkoth.koth.tally.score.ScoreTally;
import dev.smartshub.shkoth.service.bossbar.AdventureBossbarService;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.gui.GuiService;
import dev.smartshub.shkoth.gui.CreateKothGui;
import dev.smartshub.shkoth.service.koth.KothRegistrationFromTempDataService;
import dev.smartshub.shkoth.service.gui.menu.cache.KothToRegisterCache;
import dev.smartshub.shkoth.service.koth.RefreshInsideKothService;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.service.schedule.KothSchedulerService;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import dev.smartshub.shkoth.service.scoreboard.SendScoreboardService;
import dev.smartshub.shkoth.service.team.TeamHandlingService;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import dev.smartshub.shkoth.service.team.TeamInformationService;
import dev.smartshub.shkoth.service.team.TeamInviteService;
import dev.smartshub.shkoth.service.wand.WandService;
import dev.smartshub.shkoth.storage.cache.PlayerStatsCache;
import dev.smartshub.shkoth.storage.database.connection.DatabaseConnection;
import dev.smartshub.shkoth.storage.database.dao.PlayerStatsDAO;
import dev.smartshub.shkoth.storage.database.table.SchemaCreator;
import dev.smartshub.shkoth.task.AsyncJobTask;
import dev.smartshub.shkoth.task.UpdateTask;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitLamp;

import java.util.concurrent.CompletableFuture;

public class SHKoth extends JavaPlugin {

    private KothRegistry kothRegistry;
    private KothAPIImpl kothAPI;

    private final PlayerStatsDAO playerStatsDAO = new PlayerStatsDAO();
    private PlayerStatsCache playerStatsCache = new PlayerStatsCache(playerStatsDAO);

    private MessageParser messageParser;
    private MessageRepository messageRepository;
    private ConfigService configService;


    private NotifyService notifyService;

    private SendScoreboardService sendScoreboardService;
    private ScoreboardHandleService scoreboardHandleService;

    private AdventureBossbarService adventureBossbarService;

    private TeamHandlingService teamHandlingService;
    private TeamHookHelpService teamHookHelpService;
    private TeamInformationService teamInformationService;
    private TeamInviteService teamInviteService;

    private WandService wandService;

    private GuiService guiService;
    private KothToRegisterCache kothToRegisterCache = new KothToRegisterCache();
    private CreateKothGui createKothGui;
    private AddPhysicalRewardGui addPhysicalRewardGui;
    private CreateSchedulerGui createSchedulerGui;
    private CommandGui commandGui;

    private KothTicker kothTicker;
    private RefreshInsideKothService refreshInsideKothService;
    private KothSchedulerService kothSchedulerService;

    private UpdateTask task;
    private AsyncJobTask asyncJobTask;

    private ContextualTeamTracker teamTracker;

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");
        factoryRegister();
        setUpConfig();
        setUpStorage();
        initTracker();
        initAPI();
        initServices();
        guis();
        initTicking();
        setUpTasks();
        registerCommands();
        registerListeners();
        registerHooks();
    }

    @Override
    public void onDisable() {
        getLogger().info("SHKoth has been disabled!");
        KothAPIProvider.unload();
    }

    private void factoryRegister() {
        TallyFactory.register(KothType.CAPTURE, CaptureTally::new);
        TallyFactory.register(KothType.SCORE, ScoreTally::new);
    }

    private void initAPI() {
        kothRegistry = new KothRegistry(configService, teamTracker);
        kothAPI = new KothAPIImpl(kothRegistry);
        KothAPIProvider.setInstance(kothAPI);
    }

    private void setUpConfig() {
        messageParser = new MessageParser();
        configService = new ConfigService(this);
        messageRepository = new MessageRepository(configService);
    }

    private void setUpStorage() {
        CompletableFuture.runAsync(() -> {
            DatabaseConnection.init(configService.provide(ConfigType.DATABASE));
            SchemaCreator.createSchema();
        });
    }

    private void initServices() {
        notifyService = new NotifyService(messageParser, messageRepository);

        sendScoreboardService = new SendScoreboardService(configService, messageParser);
        scoreboardHandleService = new ScoreboardHandleService(sendScoreboardService, kothRegistry);

        teamHandlingService = new TeamHandlingService(notifyService, teamTracker);
        teamInformationService = new TeamInformationService(teamHandlingService, notifyService);
        teamInviteService = new TeamInviteService(teamHandlingService, notifyService);

        refreshInsideKothService = new RefreshInsideKothService(kothRegistry);
        kothSchedulerService = new KothSchedulerService(kothRegistry, configService);

        adventureBossbarService = new AdventureBossbarService(kothRegistry, configService.provide(ConfigType.BOSSBAR), messageParser);
        wandService = new WandService(this, messageParser);
    }

    // What a mess
    public void guis(){
        kothToRegisterCache = new KothToRegisterCache();

        addPhysicalRewardGui = new AddPhysicalRewardGui(kothToRegisterCache, messageParser);
        createSchedulerGui = new CreateSchedulerGui(kothToRegisterCache, messageParser);
        commandGui = new CommandGui(kothToRegisterCache, messageParser);
        createKothGui = new CreateKothGui(messageParser, kothToRegisterCache, wandService);
        guiService = new GuiService(createKothGui, createSchedulerGui, addPhysicalRewardGui, commandGui);

        kothToRegisterCache.setGuiService(guiService);
        createSchedulerGui.setGuiService(guiService);
        commandGui.setGuiService(guiService);
        addPhysicalRewardGui.setGuiService(guiService);
        createKothGui.setGuiService(guiService);

        KothRegistrationFromTempDataService registrationService = new KothRegistrationFromTempDataService(
                kothToRegisterCache,
                configService,
                kothRegistry,
                teamTracker,
                "plugins/SH-Koth/koths"
        );

        kothToRegisterCache.setRegistrationService(registrationService);
    }

    private void initTracker() {
        // My flow is broken, allow me to do this here for now
        teamHookHelpService = new TeamHookHelpService(configService.provide(ConfigType.HOOKS));
        teamTracker = new ContextualTeamTracker(teamHookHelpService);
    }

    private void initTicking() {
        kothTicker = new KothTicker(kothRegistry);
    }

    private void setUpTasks() {
        task = new UpdateTask(kothTicker, refreshInsideKothService, kothSchedulerService, scoreboardHandleService);
        task.runTaskTimer(this, 20L, 20L);

        asyncJobTask = new AsyncJobTask(scoreboardHandleService, teamTracker, adventureBossbarService);
        asyncJobTask.runTaskTimerAsynchronously(this, 20L, 20L);
    }

    private void registerCommands() {

        final var exceptionHandler = new ExceptionHandler(notifyService);

        var lamp = BukkitLamp.builder(this)
                .parameterTypes(builder -> {
                    builder.addParameterType(Koth.class, new KothParameterType(kothRegistry));
                    builder.addParameterType(Integer.class, new NumberParameterType());
                })
                .exceptionHandler(exceptionHandler)
                .build();

        lamp.register(
                new KothCommand(kothRegistry, notifyService, configService, guiService),
                new TeamCommand(teamHandlingService, teamInviteService, teamInformationService));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new KothEndListener(notifyService, playerStatsDAO), this);
        getServer().getPluginManager().registerEvents(new KothStartListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new KothStateChangeListener(scoreboardHandleService, adventureBossbarService), this);
        getServer().getPluginManager().registerEvents(new PlayerEnterKothDuringRunListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new PlayerLeavekothDuringRunListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new PlayerStartKothCaptureListener(notifyService) , this);
        getServer().getPluginManager().registerEvents(new PlayerStopKothCaptureListener(notifyService) , this);

        getServer().getPluginManager().registerEvents(new TeamChangeLeaderListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new TeamCreatedListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new TeamDissolvedListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new MemberJoinedTeamListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new MemberLeavedTeamListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new MemberKickedFromTeamListener(notifyService), this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(playerStatsCache), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(playerStatsCache), this);
        getServer().getPluginManager().registerEvents(new AsyncChatListener(this, kothToRegisterCache, messageParser, guiService), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(kothToRegisterCache, guiService, messageParser, wandService), this);
    }

    private void registerHooks(){
        //PlaceholderAPI
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderAPIHook(kothRegistry, playerStatsCache).register();
        }

        // BStats
        Metrics metrics = new Metrics(this, 27090);
    }

}
