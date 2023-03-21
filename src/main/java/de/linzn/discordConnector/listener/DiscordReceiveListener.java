package de.linzn.discordConnector.listener;

import com.theokanning.openai.completion.chat.ChatMessage;
import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordReceiveListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor() != DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getSelfUser()) {
                STEMSystemApp.LOGGER.CORE("Incoming chat from " + event.getAuthor().getName());
                STEMSystemApp.LOGGER.CORE("ID " + event.getAuthor().getId());
                STEMSystemApp.LOGGER.CORE("Value: " + event.getMessage().getContentDisplay());

                STEMSystemApp.getInstance().getScheduler().runTask(DiscordConnectorPlugin.discordConnectorPlugin, () -> {
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Working on GPT result..."));
                    List<String> input = new ArrayList<>();
                    input.add(event.getMessage().getContentDisplay());
                    ChatMessage chatMessage = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().getAIChatCompletion("Discord").requestCompletion(input);
                    STEMSystemApp.LOGGER.CORE("Receiving output...");
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), chatMessage.getContent());
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getPresence().setActivity(Activity.playing("Nothing"));
                });
            }
        }
    }
}
