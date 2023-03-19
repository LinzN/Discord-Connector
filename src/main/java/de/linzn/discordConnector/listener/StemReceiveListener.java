package de.linzn.discordConnector.listener;

import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.eventModule.handler.StemEventHandler;
import de.stem.stemSystem.modules.informationModule.InformationBlock;
import de.stem.stemSystem.modules.informationModule.events.InformationEvent;

public class StemReceiveListener {

    @StemEventHandler()
    public void inInformationEvent(InformationEvent informationEvent) {
        InformationBlock informationBlock = informationEvent.getInformationBlock();
        if (informationBlock.getSourcePlugin().getPluginName().equalsIgnoreCase("system-chain")) {
            sendInformationBlock(informationBlock);
        } else if (informationBlock.getSourcePlugin().getPluginName().equalsIgnoreCase("home-devices")) {
            sendInformationBlock(informationBlock);
        }
    }

    private void sendInformationBlock(InformationBlock informationBlock) {
        STEMSystemApp.LOGGER.INFO("Listen to InformationEvent and forward to discord: " );
        String userID = DiscordConnectorPlugin.discordConnectorPlugin.getDefaultConfig().getString("discord.keyUser");
        DiscordConnectorPlugin.discordConnectorPlugin.getDiscordManager().sendMessageToUser(userID, informationBlock.getDescription());
    }
}
