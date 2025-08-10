package dev.smartshub.shkoth.message;

import dev.smartshub.shkoth.api.config.ConfigContainer;
import dev.smartshub.shkoth.api.config.ConfigType;
import dev.smartshub.shkoth.service.config.ConfigService;

import java.util.List;

public class MessageRepository {

    private final ConfigContainer config;

    public MessageRepository(ConfigService configService) {
        this.config = configService.provide(ConfigType.MESSAGES);
    }

    public String getMessage(String path) {
        String fullPath = "messages." + path;
        return config.getString(fullPath, "<red>Message not found: " + fullPath);
    }

    public List<String> getMessageList(String path) {
        String fullPath = "messages." + path;
        return config.getStringList(fullPath, List.of("<gray>Empty message list: " + fullPath));
    }

    public String getBroadcastMessage(String path) {
        String fullPath = "broadcast." + path;
        return config.getString(fullPath, "<red>Broadcast message not found: " + fullPath);
    }

    public List<String> getBroadcastMessageList(String path) {
        String fullPath = "broadcast." + path;
        return config.getStringList(fullPath, List.of("<gray>Empty broadcast list: " + fullPath));
    }
}
