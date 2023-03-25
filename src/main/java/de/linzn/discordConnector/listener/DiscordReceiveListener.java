package de.linzn.discordConnector.listener;

import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DiscordReceiveListener extends ListenerAdapter {
    private final ConcurrentHashMap<String, Long> concurrentHashMapInstances;

    public DiscordReceiveListener() {
        concurrentHashMapInstances = new ConcurrentHashMap<>();

        STEMSystemApp.getInstance().getScheduler().runRepeatScheduler(DiscordConnectorPlugin.discordConnectorPlugin, () -> {

            for (String identityName : concurrentHashMapInstances.keySet()) {
                long date = this.concurrentHashMapInstances.get(identityName);
                if (date + Duration.ofMinutes(2).toMillis() < new Date().getTime()) {
                    STEMSystemApp.LOGGER.CORE("Destroying instance from " + identityName + " of openAI! Timeout to prevent to much tokens!");
                    GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().destroyAIChatCompletion(DiscordConnectorPlugin.discordConnectorPlugin, identityName);
                    this.concurrentHashMapInstances.remove(identityName);
                }
            }
            DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Instances: " + this.concurrentHashMapInstances.size()));
        }, 4, 1, TimeUnit.SECONDS);
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor() != DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getSelfUser()) {
                String inputData = event.getMessage().getContentDisplay();
                User user = event.getAuthor();

                STEMSystemApp.LOGGER.INFO("Incoming communication from " + user.getName());
                STEMSystemApp.LOGGER.INFO("Input: " + inputData);

                this.processGPT(user.getName(), inputData, event.getChannel());

            }
        } else {
            if (event.getAuthor() != DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getSelfUser()) {
                if (event.getChannel().getId().equalsIgnoreCase("1088490251116359750")) {
                    String inputData = event.getMessage().getContentDisplay();
                    User user = event.getAuthor();

                    STEMSystemApp.LOGGER.INFO("Incoming communication channel from " + user.getName());
                    STEMSystemApp.LOGGER.INFO("Input: " + inputData);

                    this.processGPT(user.getName(), inputData, event.getChannel());
                }
            }
        }
    }

    private void processGPT(String sender, String content, MessageChannel channel) {

        if (content.startsWith("CreateImage: ")) {
            STEMSystemApp.LOGGER.INFO("Image request...");
            String url = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().createAIImageCompletion().requestCompletion(content.replace("CreateImage:", ""));
            STEMSystemApp.LOGGER.INFO("Callback received!");
            DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToChannel(channel, url);

        } else {
            STEMSystemApp.LOGGER.INFO("Chat request...");
            List<String> input = new ArrayList<>();
            input.add("[" + sender + "]-> " + content);

            setInstance(channel.getId(), new Date().getTime());
            String chatMessage = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().createAIChatCompletion(DiscordConnectorPlugin.discordConnectorPlugin, channel.getId()).requestCompletion(input);
            STEMSystemApp.LOGGER.INFO("Callback received!");
            DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToChannel(channel, chatMessage);

            if (chatMessage.startsWith("An error was catch")) {
                input = new ArrayList<>();
                input.add("An error was generated by you. Please inform me: " + chatMessage);
                chatMessage = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().createAIChatCompletion(DiscordConnectorPlugin.discordConnectorPlugin, channel.getId()).requestCompletion(input);
                DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToChannel(channel, chatMessage);
            }
        }
    }

    private void setInstance(String identity, long date) {
        this.concurrentHashMapInstances.put(identity, date);
    }
}
