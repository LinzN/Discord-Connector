package de.linzn.discordConnector.listener;

import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.cloudModule.CloudFile;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
/*
            for (String identityName : concurrentHashMapInstances.keySet()) {
                long date = this.concurrentHashMapInstances.get(identityName);
                if (date + Duration.ofMinutes(2).toMillis() < new Date().getTime()) {
                    STEMSystemApp.LOGGER.CORE("Destroying instance from " + identityName + " of openAI! Timeout to prevent to much tokens!");
                    GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().destroyAIChatCompletion(DiscordConnectorPlugin.discordConnectorPlugin, identityName);
                    this.concurrentHashMapInstances.remove(identityName);
                }
            }

 */
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
            String openAIURL = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().createAIImageCompletion().requestCompletion(content.replace("CreateImage:", ""));
            StringBuilder url;
            try {
            URL openaiImageUrl = new URL(openAIURL);
            File tempDirectory = new File(DiscordConnectorPlugin.discordConnectorPlugin.getDataFolder(), "temp");
            if (!tempDirectory.exists()) {
                tempDirectory.mkdir();
            }

            File tempFile = new File(tempDirectory, "picture.png");
            InputStream in = openaiImageUrl.openStream();
            Files.copy(in, Paths.get(tempFile.getPath()), StandardCopyOption.REPLACE_EXISTING);

            CloudFile cloudFile = STEMSystemApp.getInstance().getCloudModule().uploadFileRandomName(tempFile, "/GeneratedImages/");
            if (cloudFile != null) {
                String nextcloudURL = cloudFile.createPublicShareLink();
                url = new StringBuilder(nextcloudURL);
            } else {
                throw new IllegalArgumentException("Error while uploading file to cloud!");
            }

            } catch (Exception e) {
                STEMSystemApp.LOGGER.ERROR(e);
                url = new StringBuilder("An error was catch in kernel stacktrace! Please check STEM logs for more information!");
            }

            STEMSystemApp.LOGGER.INFO("Callback received!");
            DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToChannel(channel, url.toString());

        } else {
            STEMSystemApp.LOGGER.INFO("Chat request...");
            List<String> input = new ArrayList<>();
            input.add(sender + " sagt: " + content);

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
