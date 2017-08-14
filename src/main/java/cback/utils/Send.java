package cback.utils;

import cback.ConfigManager;
import cback.Report;
import cback.TVBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

public class Send {
    /*
     * Simple Embeds
     */
    public static void simpleEmbed(IChannel channel, String message) {
        Color BOT_COLOR = Color.getColor(TVBot.getConfigManager().getConfigValue("bot_color"));
        Send.embed(channel, new EmbedBuilder().withDescription(message).withColor(BOT_COLOR).build());
    }

    /*
     * Send Embed
     */
    public static IMessage embed(IChannel channel, EmbedObject embedObject) {
        channel.setTypingStatus(true);
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(TVBot.getInstance().getClient()).withEmbed(embedObject)
                        .withChannel(channel).send();
            } catch (Exception e) {
            }
            return null;
        });
        channel.setTypingStatus(false);
        return future.get();
    }

    /*
     * Send Report
     */
    public static void report(Report report) {
        IDiscordClient client = TVBot.getClient();
        ConfigManager cm = TVBot.getConfigManager();

        IChannel errorChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("errors_ID")));
        Color BOT_COLOR = Color.getColor(cm.getConfigValue("bot_color"));

        IMessage message = report.getMessage();
        Exception e = report.getException();

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(Util.getAvatar(message.getAuthor()))
                .withDesc(message.getContent())
                .appendField("\u200B", "\u200B", false)

                .appendField("Stack:", e.toString(), false)
                .withTimestamp(System.currentTimeMillis());

        Send.embed(errorChannel, bld.build());

    }

    /*
     * Send BotLog
     */
    public static void botLog(IMessage message) {
        IDiscordClient client = TVBot.getClient();
        ConfigManager cm = TVBot.getConfigManager();

        IChannel botLogChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("errors_ID")));
        Color BOT_COLOR = Color.getColor(cm.getConfigValue("bot_color"));

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(Util.getAvatar(message.getAuthor()))
                .withDesc(message.getFormattedContent());

        Send.embed(botLogChannel, bld.build());
    }

}
