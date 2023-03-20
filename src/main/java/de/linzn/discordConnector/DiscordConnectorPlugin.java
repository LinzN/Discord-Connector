/*
 * Copyright (C) 2020. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 * this file. If not, please write to: niklas.linz@enigmar.de
 *
 */

package de.linzn.discordConnector;


import de.linzn.discordConnector.listener.StemEventListener;
import de.stem.stemSystem.STEMSystemApp;
import de.stem.stemSystem.modules.pluginModule.STEMPlugin;


public class DiscordConnectorPlugin extends STEMPlugin {

    public static DiscordConnectorPlugin discordConnectorPlugin;
    public DiscordManager discordManager;

    public DiscordConnectorPlugin() {
        discordConnectorPlugin = this;
    }

    @Override
    public void onEnable() {
        String token = this.getDefaultConfig().getString("discord.botToken", "xxx");
        this.getDefaultConfig().getString("discord.keyUser", "xxx");
        this.getDefaultConfig().save();
        this.discordManager = new DiscordManager(token);
        STEMSystemApp.getInstance().getEventModule().getStemEventBus().register(new StemEventListener());
    }


    @Override
    public void onDisable() {
        this.discordManager.getJda().shutdownNow();
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }
}
