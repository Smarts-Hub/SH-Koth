package dev.smartshub.shkoth.message;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.List;

public class MessageRepository {

    private final ConfigContainer messages;
    private final ConfigContainer broadcast;
    private final ConfigContainer title;
    private final ConfigContainer actionbar;
    private final ConfigContainer sound;

    public MessageRepository(ConfigService configService) {
        this.messages = configService.provide(ConfigType.MESSAGES);
        this.broadcast = configService.provide(ConfigType.BROADCAST);
        this.title = configService.provide(ConfigType.TITLE);
        this.actionbar = configService.provide(ConfigType.ACTIONBAR);
        this.sound = configService.provide(ConfigType.SOUND);
    }

    public String getMessage(String path) {
        return messages.getString(path, "<red>Message not found: " + path);
    }

    public List<String> getMessageList(String path) {
        return messages.getStringList(path, List.of("<gray>Empty message list: " + path));
    }

    public String getBroadcastMessage(String path) {
        return broadcast.getString(path, "<red>Broadcast message not found: " + path);
    }

    public List<String> getBroadcastMessageList(String path) {
        return broadcast.getStringList(path, List.of("<gray>Empty broadcast list: " + path));
    }

    public String getTitle(String path) {
        return title.getString(path, "");
    }

    public String getSubtittle(String path) {
        return title.getString(path, "");
    }

    public String getActionbar(String path) {
        return actionbar.getString(path, "");
    }

    public String getSound(String path) {
        return sound.getString(path, "");
    }
}
