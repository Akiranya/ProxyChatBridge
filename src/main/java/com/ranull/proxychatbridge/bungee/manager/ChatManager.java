package com.ranull.proxychatbridge.bungee.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.ranull.proxychatbridge.bungee.ProxyChatBridge;
import com.ranull.proxychatbridge.bungee.event.ExternalChatSendEvent;
import com.ranull.proxychatbridge.common.util.StringUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ChatManager {
    private final ProxyChatBridge plugin;

    public ChatManager(ProxyChatBridge plugin) {
        this.plugin = plugin;
    }

    public void bridgeServerChat(UUID uuid, String name, String format, String message, ServerInfo serverInfo) {
        String serverName = replaceText(serverInfo.getName());

        if (plugin.getConfig().getBoolean("settings.title-case", true)) {
            serverName = StringUtil.toTitleCase(serverName);
        }

        String prefix = plugin.getConfig().getString("settings.servers." + serverInfo.getName() + ".prefix", "");

        if (prefix.equals("")) {
            prefix = plugin.getConfig().getString("settings.prefix", "");
        }

        format = replaceText(prefix.replace("%server%", serverName).replace("&", "§") + ChatColor.RESET + format);
        message = replaceText(message);
        String group = getGroup(serverInfo.getName());

        for (Entry<String, ServerInfo> server : plugin.getProxy().getServers().entrySet()) {
            if (!server.getValue().getPlayers().isEmpty() && !server.getKey().equals(serverInfo.getName())) {
                String externalGroup = getGroup(server.getKey());

                if ((externalGroup.equals("global") || externalGroup.equals(group))) {
                    ExternalChatSendEvent externalChatSendEvent = new ExternalChatSendEvent(uuid, name, format, message,
                            group, serverInfo.getName(), server.getValue());

                    plugin.getProxy().getPluginManager().callEvent(externalChatSendEvent);

                    if (!externalChatSendEvent.isCancelled()) {
                        sendChatData(externalChatSendEvent.getUUID(), externalChatSendEvent.getName(),
                                externalChatSendEvent.getFormat(), externalChatSendEvent.getMessage(),
                                "server:" + serverInfo.getName(), externalChatSendEvent.getDestination());
                    }
                }
            }
        }
    }

    public void sendMessage(String name, String format, String message, String group, String source) {
        for (Entry<String, ServerInfo> server : plugin.getProxy().getServers().entrySet()) {
            String externalGroup = getGroup(server.getKey());

            if ((externalGroup.equals("global") || externalGroup.equals(group))) {
                ExternalChatSendEvent externalChatSendEvent = new ExternalChatSendEvent(null, name, format, message,
                        group, source, server.getValue());

                plugin.getProxy().getPluginManager().callEvent(externalChatSendEvent);

                if (!externalChatSendEvent.isCancelled()) {
                    sendChatData(externalChatSendEvent.getUUID(), externalChatSendEvent.getName(),
                            externalChatSendEvent.getFormat(), externalChatSendEvent.getMessage(),
                            source, externalChatSendEvent.getDestination());
                }
            }
        }
    }

    public String replaceText(String string) {
        Map<String, String> stringMap = new HashMap<>();

        for (String key : plugin.getConfig().getSection("settings.replace-text").getKeys()) {
            stringMap.put(key, plugin.getConfig().getString("settings.replace-text." + key));
        }

        return StringUtil.replaceAll(string, stringMap);
    }

    public String getGroup(String serverName) {
        return plugin.getConfig().getString("settings.servers." + serverName + ".group",
                plugin.getConfig().getString("settings.group", "global"));
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendChatData(UUID uuid, String name, String format, String message, String source,
                              ServerInfo destination) {
        ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();

        byteArrayDataOutput.writeUTF("ProxyChatBridge");
        byteArrayDataOutput.writeUTF("Message");
        byteArrayDataOutput.writeUTF(source != null ? source : "");
        byteArrayDataOutput.writeUTF(uuid != null ? uuid.toString() : "");
        byteArrayDataOutput.writeUTF(name);
        byteArrayDataOutput.writeUTF(format);
        byteArrayDataOutput.writeUTF(message);

        destination.sendData("BungeeCord", byteArrayDataOutput.toByteArray());
    }
}
