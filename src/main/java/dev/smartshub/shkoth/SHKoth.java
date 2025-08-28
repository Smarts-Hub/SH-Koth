package dev.smartshub.shkoth;

import dev.smartshub.shkoth.api.KothAPIProvider;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.koth.guideline.KothType;
import dev.smartshub.shkoth.command.handler.exception.ExceptionHandler;
import dev.smartshub.shkoth.command.handler.suggestion.CommandSuggestionProvider;
import dev.smartshub.shkoth.command.koth.KothCommand;
import dev.smartshub.shkoth.command.team.TeamCommand;
import dev.smartshub.shkoth.koth.ticking.KothTicker;
import dev.smartshub.shkoth.listener.koth.*;
import dev.smartshub.shkoth.listener.team.*;
import dev.smartshub.shkoth.message.MessageParser;
import dev.smartshub.shkoth.message.MessageRepository;
import dev.smartshub.shkoth.registry.KothRegistry;
import dev.smartshub.shkoth.api.koth.tally.TallyFactory;
import dev.smartshub.shkoth.koth.tally.capture.CaptureTally;
import dev.smartshub.shkoth.koth.tally.score.ScoreTally;
import dev.smartshub.shkoth.service.config.ConfigService;
import dev.smartshub.shkoth.service.koth.RefreshInsideKothService;
import dev.smartshub.shkoth.service.notify.NotifyService;
import dev.smartshub.shkoth.service.schedule.KothSchedulerService;
import dev.smartshub.shkoth.service.scoreboard.ScoreboardHandleService;
import dev.smartshub.shkoth.service.scoreboard.SendScoreboardService;
import dev.smartshub.shkoth.service.team.TeamHandlingService;
import dev.smartshub.shkoth.service.team.TeamHookHelpService;
import dev.smartshub.shkoth.service.team.TeamInformationService;
import dev.smartshub.shkoth.service.team.TeamInviteService;
import dev.smartshub.shkoth.task.UpdateTask;
import dev.smartshub.shkoth.team.ContextualTeamTracker;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.zapper.ZapperJavaPlugin;

public class SHKoth extends ZapperJavaPlugin {

    private KothRegistry kothRegistry;
    private KothAPIImpl kothAPI;


    private final MessageParser messageParser = new MessageParser();
    private MessageRepository messageRepository;
    private ConfigService configService;

    private NotifyService notifyService;

    private SendScoreboardService sendScoreboardService;
    private ScoreboardHandleService scoreboardHandleService;

    private TeamHandlingService teamHandlingService;
    private TeamHookHelpService teamHookHelpService;
    private TeamInformationService teamInformationService;
    private TeamInviteService teamInviteService;

    private KothTicker kothTicker;
    private RefreshInsideKothService refreshInsideKothService;
    private KothSchedulerService kothSchedulerService;

    private UpdateTask task;

    private ContextualTeamTracker teamTracker;

    @Override
    public void onEnable() {
        getLogger().info("SHKoth has been enabled!");
        factoryRegister();
        setUpConfig();
        initAPI();
        initServices();
        initTracker();
        initTicking();
        setUpTasks();
        registerCommands();
        registerListeners();
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
        configService = new ConfigService(this);
        messageRepository = new MessageRepository(configService);
    }

    private void initServices() {
        notifyService = new NotifyService(messageParser, messageRepository);

        sendScoreboardService = new SendScoreboardService(configService, messageParser);
        scoreboardHandleService = new ScoreboardHandleService(sendScoreboardService);

        teamHandlingService = new TeamHandlingService(notifyService, teamTracker);
        teamHookHelpService = new TeamHookHelpService(configService.provide(ConfigType.HOOKS));
        teamInformationService = new TeamInformationService(teamHandlingService, notifyService);
        teamInviteService = new TeamInviteService(teamHandlingService, notifyService);

        refreshInsideKothService = new RefreshInsideKothService(kothRegistry);
        kothSchedulerService = new KothSchedulerService(kothRegistry);
    }

    private void initTracker() {
        teamTracker = new ContextualTeamTracker(teamHookHelpService);
    }

    private void initTicking() {
        kothTicker = new KothTicker(kothRegistry);
    }

    private void setUpTasks() {
        task = new UpdateTask(kothTicker, refreshInsideKothService, kothSchedulerService);
        task.runTaskTimer(this, 20L, 20L);
    }

    private void registerCommands() {

        final var exceptionHandler = new ExceptionHandler(notifyService);
        final var commandSuggestionProvider = new CommandSuggestionProvider(kothRegistry);

        var lamp = BukkitLamp.builder(this)
                .suggestionProviders(providers -> {
                    providers.addProvider(Koth.class, commandSuggestionProvider.getKothProvider());
                    providers.addProvider(Player.class, commandSuggestionProvider.getPlayerProvider());
                    providers.addProvider(int.class, commandSuggestionProvider.getNumberProvider());
                })

                .exceptionHandler(exceptionHandler)
                .build();


        lamp.register(
                new KothCommand(kothRegistry, notifyService, configService),
                new TeamCommand(teamHandlingService, teamInviteService, teamInformationService));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new KothEndListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new KothStartListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new KothStateChangeListener(scoreboardHandleService), this);
        getServer().getPluginManager().registerEvents(new PlayerEnterKothDuringRunListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new PlayerLeavekothDuringRunListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new PlayerStartKothCaptureListener(notifyService) , this);
        getServer().getPluginManager().registerEvents(new PlayerStopKothCaptureListener(notifyService) , this);
        //TODO: Team event dispatcher and remove notifications from team events
        getServer().getPluginManager().registerEvents(new TeamChangeLeaderListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new TeamCreatedListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new TeamDissolvedListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new MemberJoinedTeamListener(notifyService), this);
        getServer().getPluginManager().registerEvents(new MemberLeavedTeamListener(notifyService), this);
    }

}
