package de.linzn.discordConnector.listener;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import de.linzn.discordConnector.DiscordConnectorPlugin;
import de.stem.stemSystem.STEMSystemApp;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DiscordReceiveListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            if(event.getAuthor() != DiscordConnectorPlugin.discordConnectorPlugin.discordManager.getJda().getSelfUser()) {
                STEMSystemApp.LOGGER.CORE("Incoming chat from " + event.getAuthor().getName());
                STEMSystemApp.LOGGER.CORE("ID " + event.getAuthor().getId());
                STEMSystemApp.LOGGER.CORE("Value: " + event.getMessage().getContentDisplay());

                try {
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), askOpenAI(event.getMessage().getContentDisplay()));
                } catch (OpenAiHttpException e){
                    DiscordConnectorPlugin.discordConnectorPlugin.discordManager.sendMessageToUser(event.getAuthor().getId(), "Hello " + event.getAuthor().getName() + ", i can not give you a valid answer. I think my openAI billing plan has no more free quota.");
                }
            }
        }
    }

    private String askOpenAI(String msg){
        String token = DiscordConnectorPlugin.discordConnectorPlugin.getDefaultConfig().getString("openAI.token");
        List<ChatMessage> list = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRole("client");
        chatMessage.setContent(msg);
        list.add(chatMessage);
        OpenAiService service = new OpenAiService(token);
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .messages(list)
                .model("gpt-3.5-turbo")
                .build();
        return service.createChatCompletion(completionRequest).getChoices().get(0).getMessage().getContent();
    }
}
