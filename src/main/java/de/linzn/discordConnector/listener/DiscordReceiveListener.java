package de.linzn.discordConnector.listener;

import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DiscordReceiveListener extends ListenerAdapter {

    private long date;

    public DiscordReceiveListener(){
        this.date= new Date().getTime();

        STEMSystemApp.getInstance().getScheduler().runRepeatScheduler(DiscordConnectorPlugin.discordConnectorPlugin, () -> {
            if(GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().hasAIChatCompletion("Discord")) {
                if (this.date + Duration.ofMinutes(2).toMillis() < new Date().getTime()) {
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Cleaning memory..."));
                    STEMSystemApp.LOGGER.CORE("Destroying current instance of openAI! Timeout to prevent to much tokens!");
                    GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().destroyAIChatCompletion("Discord");
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Memory empty"));
                }
            }
        }, 4, 1, TimeUnit.SECONDS);
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor() != DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getSelfUser()) {
                STEMSystemApp.LOGGER.CORE("Incoming chat from " + event.getAuthor().getName());
                STEMSystemApp.LOGGER.CORE("ID " + event.getAuthor().getId());
                STEMSystemApp.LOGGER.CORE("Value: " + event.getMessage().getContentDisplay());

                STEMSystemApp.getInstance().getScheduler().runTask(DiscordConnectorPlugin.discordConnectorPlugin, () -> {
                    this.date = new Date().getTime();
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Working on GPT result..."));
                    List<String> input = new ArrayList<>();
                    input.add(event.getMessage().getContentDisplay());
                    String chatMessage = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().getAIChatCompletion("Discord").requestCompletion(input);
                    STEMSystemApp.LOGGER.CORE("Receiving output...");
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), chatMessage);
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Instance in Memory"));
                });
            }
        }
    }
}
