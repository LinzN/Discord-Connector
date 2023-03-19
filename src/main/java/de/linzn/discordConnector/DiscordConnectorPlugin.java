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


import de.stem.stemSystem.modules.pluginModule.STEMPlugin;


public class DiscordConnectorPlugin extends STEMPlugin {

    public static DiscordConnectorPlugin discordConnectorPlugin;


    public DiscordConnectorPlugin() {
        discordConnectorPlugin = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }
}