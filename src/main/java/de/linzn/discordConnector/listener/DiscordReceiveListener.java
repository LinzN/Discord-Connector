package de.linzn.discordConnector.listener;

import com.theokanning.openai.completion.chat.ChatMessage;
import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.linzn.gptFramework.GPTFrameworkPlugin;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;
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

                //DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), "Hello " + event.getAuthor().getName() + ", i can not give you a valid answer. I think my openAI billing plan has no more free quota.");

                List<String> input = new ArrayList<>();
                input.add(event.getMessage().getContentDisplay());
                ChatMessage chatMessage = GPTFrameworkPlugin.gptFrameworkPlugin.getGptManager().getAIChatCompletion("Discord").requestCompletion(input);
                DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), chatMessage.getContent());
            }
        }
    }
}
