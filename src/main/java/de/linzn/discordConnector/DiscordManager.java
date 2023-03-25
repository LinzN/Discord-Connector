package de.linzn.discordConnector;

import de.linzn.discordConnector.listener.DiscordReceiveListener;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.nio.channels.Channel;

public class DiscordManager {
    private final String token;
    private JDA jda;

    public DiscordManager(final String token) {
        this.token = token;
        STEMSystemApp.getInstance().getScheduler().runTask(DiscordConnectorPlugin.discordConnectorPlugin, this::setupAndLogin);
    }

    private void setupAndLogin() {
        STEMSystemApp.LOGGER.CORE("Setup Discord API...");
        JDABuilder jdaBuilder = JDABuilder.createLight(token);
        jdaBuilder.addEventListeners(new DiscordReceiveListener());
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        this.jda = jdaBuilder.build();
        STEMSystemApp.LOGGER.CORE("Login to Discord API...");
        try {
            jda.awaitReady();
            STEMSystemApp.LOGGER.CORE("Login Discord API success!");
        } catch (InterruptedException e) {
            STEMSystemApp.LOGGER.ERROR("Login Discord API failed!");
            STEMSystemApp.LOGGER.ERROR(e);
        }

        this.jda.getPresence().setActivity(Activity.playing("Working on STEM stuff..."));
    }

    public JDA getJda() {
        return jda;
    }

    public void sendMessageToUser(String userId, String message) {
        User user = this.getJda().retrieveUserById(userId).complete();
        user.openPrivateChannel().complete().sendMessage(message).complete();
    }

    public void sendMessageToChannel(MessageChannel channel, String message) {

        channel.sendMessage(message).complete();
    }
}
