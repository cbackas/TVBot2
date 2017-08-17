package cback.utils;

import cback.ConfigManager;
import cback.Report;
import cback.TVBot;
import cback.commands.Command;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.awt.*;

public class Util {
    static IDiscordClient client = TVBot.getClient();
    static ConfigManager cm = TVBot.getConfigManager();
    static Color BOT_COLOR = Color.decode("#" + cm.getConfigValue("bot_color"));

    /**
     * Returns a default discord avatar if a user's avatar is null for some reason
     */
    public static String getAvatar(IUser user) {
        return user.getAvatar() != null ? user.getAvatarURL() : "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png";
    }

    /**
     * Simple Embeds
     */
    public static void simpleEmbed(IChannel channel, String message) {
        embed(channel, new EmbedBuilder().withDescription(message).withColor(BOT_COLOR).build());
    }

    public static void simpleEmbed(IChannel channel, String message, Color color) {
        embed(channel, new EmbedBuilder().withDescription(message).withColor(BOT_COLOR).build());
    }

    /**
     * Send Embed
     */
    public static IMessage embed(IChannel channel, EmbedObject embedObject) {
        channel.setTypingStatus(true);
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                return new MessageBuilder(client).withEmbed(embedObject)
                        .withChannel(channel).send();
            } catch (Exception e) {
            }
            return null;
        });
        channel.setTypingStatus(false);
        return future.get();
    }

    /**
     * Send Report
     */
    public static void report(Report report) {
        IChannel errorChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("errors_ID")));

        IMessage message = report.getMessage();
        Exception e = report.getException();

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(getAvatar(message.getAuthor()))
                .withDesc(message.getContent())
                .appendField("\u200B", "\u200B", false)

                .appendField("Exeption:", e.toString(), false);

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            stack.append(s.toString());
        }

        bld
                .appendField("Stack:", stack.toString().substring(0, 1800), false)
                .withTimestamp(System.currentTimeMillis());

        embed(errorChannel, bld.build());
    }

    /**
     * Send BotLog
     */
    public static void botLog(IMessage message) {
        try {
        IChannel botLogChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("botlog_ID")));
        Color BOT_COLOR = Color.getColor(cm.getConfigValue("bot_color"));

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(getAvatar(message.getAuthor()))
                .withDesc(message.getFormattedContent());

        embed(botLogChannel, bld.build());
    } catch (Exception e) {
            Report report = new Report(message, e);
            report(report);
        }
    }

    /**
     * Command Syntax error
     */
    public static void syntaxError(Command command, IMessage message) {
        try {
            EmbedBuilder bld = new EmbedBuilder()
                    .withColor(BOT_COLOR)
                    .withAuthorName(command.getName())
                    .withAuthorIcon(TVBot.getClient().getApplicationIconURL())
                    .withDesc(command.getDescription())
                    .appendField("Syntax:", TVBot.getPrefix() + command.getSyntax(), false);

            embed(message.getChannel(), bld.build());
        } catch (Exception e) {
            Report report = new Report(message, e);
            report(report);
        }
    }

}
