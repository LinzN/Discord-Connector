package de.linzn.discordConnector.listener;

import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordReceiveListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromType(ChannelType.PRIVATE))
        {
            STEMSystemApp.LOGGER.CORE("Incoming chat from " + event.getAuthor().getName());
            STEMSystemApp.LOGGER.CORE("Value: " + event.getMessage().getContentDisplay());
        }
    }
}
