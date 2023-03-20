package de.linzn.discordConnector.listener;

import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.eventModule.handler.StemEventHandler;
import de.stem.stemSystem.modules.informationModule.InformationBlock;
import de.stem.stemSystem.modules.informationModule.InformationIntent;
import de.stem.stemSystem.modules.informationModule.events.InformationEvent;

public class StemEventListener {

    @StemEventHandler()
    public void inInformationEvent(InformationEvent informationEvent) {
        InformationBlock informationBlock = informationEvent.getInformationBlock();

        if (informationBlock.hasIntent(InformationIntent.NOTIFY_USER)) {
            STEMSystemApp.LOGGER.INFO("Listen to InformationEvent and forward to discord: ");
            String userID = DiscordConnectorPlugin.discordConnectorPlugin.getDefaultConfig().getString("discord.keyUser");
            DiscordConnectorPlugin.discordConnectorPlugin.getDiscordManager().sendMessageToUser(userID, informationBlock.getDescription());
        }
    }
}
