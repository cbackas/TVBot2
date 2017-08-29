package cback.utils;

import cback.ConfigManager;
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
     * Returns the bot's color as a Color object
     */
    public static Color getBotColor() {
        return BOT_COLOR;
    }

    /**
     * Returns a easy informational sexy tag for a user
     */
    public static String getTag(IUser user) {
        return '@' + user.getName() + '#' + user.getDiscriminator();
    }

    /**
     * Returns current time in a format that stuff needs
     */
    public static int getCurrentTime() {
        try {
            return Math.toIntExact(System.currentTimeMillis() / 1000);
        } catch (Exception e) {
            reportHome(e);
        }
        return 0;
    }

    /**
     * Send a regular ol' string message
     */
    public static void sendMessage(IChannel channel, String message) {
        try {
            channel.sendMessage(message);
        } catch (Exception e) {
            reportHome(e);
        }
    }

    /**
     * Send simple fast embeds
     */
    public static void simpleEmbed(IChannel channel, String message) {
        embed(channel, new EmbedBuilder().withDescription(message).withColor(BOT_COLOR).build());
    }

    public static void simpleEmbed(IChannel channel, String message, Color color) {
        embed(channel, new EmbedBuilder().withDescription(message).withColor(color).build());
    }

    /**
     * Send embed objects
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
     * Send report
     */
    public static void reportHome(IMessage message, Exception e) {
        IChannel errorChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("ERORRLOG_ID")));

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withTimestamp(System.currentTimeMillis())
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(getAvatar(message.getAuthor()))
                .withDesc(message.getContent())
                .appendField("\u200B", "\u200B", false)

                .appendField("Exeption:", e.toString(), false);

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            stack.append(s.toString());
            stack.append("\n");
        }

        String stackString = stack.toString();
        if (stackString.length() > 1800) {
            stackString = stackString.substring(0, 1800);
        }

            bld
                    .appendField("Stack:", stackString, false);

        embed(errorChannel, bld.build());
    }

    public static void reportHome(Exception e) {
        IChannel errorChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("ERORRLOG_ID")));

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withTimestamp(System.currentTimeMillis())
                .appendField("Exeption:", e.toString(), false);

        StringBuilder stack = new StringBuilder();
        for (StackTraceElement s : e.getStackTrace()) {
            stack.append(s.toString());
            stack.append("\n");
        }

        String stackString = stack.toString();
        if (stackString.length() > 1800) {
            stackString = stackString.substring(0, 1800);
        }

        bld
                .appendField("Stack:", stackString, false);

        embed(errorChannel, bld.build());
    }

    /**
     * Send botLog
     */
    public static void botLog(IMessage message) {
        try {
        IChannel botLogChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("COMMANDLOG_ID")));

        EmbedBuilder bld = new EmbedBuilder()
                .withColor(BOT_COLOR)
                .withAuthorName(message.getAuthor().getName() + '#' + message.getAuthor().getDiscriminator())
                .withAuthorIcon(getAvatar(message.getAuthor()))
                .withDesc(message.getFormattedContent())
                .withFooterText(message.getGuild().getName() + "/#" + message.getChannel().getName());

        embed(botLogChannel, bld.build());
    } catch (Exception e) {
            reportHome(message, e);
        }
    }

    /**
     * Command syntax error
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
            reportHome(message, e);
        }
    }

    /**
     * Delete a message
     */
    public static void deleteMessage(IMessage message) {
        try {
            message.delete();
        } catch (Exception e) {
            reportHome(message, e);
        }
    }

    /**
     * Add a server log
     */
    public static IMessage sendLog(IMessage message, String text) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                IUser user = message.getAuthor();
                IChannel serverLogChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("SERVERLOG_ID")));

                new EmbedBuilder();
                EmbedBuilder embed = new EmbedBuilder();

                embed.withFooterIcon(getAvatar(user));
                embed.withFooterText("Action by @" + getTag(user));

                embed.withDescription(text);

                embed.withTimestamp(System.currentTimeMillis());

                IDiscordClient client = TVBot.getInstance().getClient();
                return new MessageBuilder(client).withEmbed(embed.withColor(Color.GRAY).build())
                        .withChannel(serverLogChannel).send();
            } catch (Exception e) {
                reportHome(e);
            }
            return null;
        });
        return future.get();
    }

    public static IMessage sendLog(IMessage message, String text, Color color) {
        RequestBuffer.RequestFuture<IMessage> future = RequestBuffer.request(() -> {
            try {
                IUser user = message.getAuthor();
                IChannel serverLogChannel = client.getChannelByID(Long.parseLong(cm.getConfigValue("SERVERLOG_ID")));

                new EmbedBuilder();
                EmbedBuilder embed = new EmbedBuilder();

                embed.withFooterIcon(getAvatar(user));
                embed.withFooterText("Action by @" + getTag(user));

                embed.withDescription(text);

                embed.withTimestamp(System.currentTimeMillis());

                IDiscordClient client = TVBot.getInstance().getClient();
                return new MessageBuilder(client).withEmbed(embed.withColor(color).build())
                        .withChannel(serverLogChannel).send();
            } catch (Exception e) {
                reportHome(e);
            }
            return null;
        });
        return future.get();
    }


}
