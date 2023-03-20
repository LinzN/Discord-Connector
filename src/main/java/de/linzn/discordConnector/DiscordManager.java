package de.linzn.discordConnector;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.discordConnector.listener.DiscordReceiveListener;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

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

        this.jda = jdaBuilder.build();
        STEMSystemApp.LOGGER.CORE("Login to Discord API...");
        try {
            jda.awaitReady();
            STEMSystemApp.LOGGER.CORE("Login Discord API success!");
        } catch (InterruptedException e) {
            STEMSystemApp.LOGGER.ERROR("Login Discord API failed!");
            STEMSystemApp.LOGGER.ERROR(e);
        }

        this.jda.getPresence().setActivity(Activity.playing("API tests"));
    }

    public JDA getJda() {
        return jda;
    }

    public void sendMessageToUser(String userId, String message) {
        User user = this.getJda().retrieveUserById(userId).complete();
        user.openPrivateChannel().complete().sendMessage(message).complete();
    }
}
