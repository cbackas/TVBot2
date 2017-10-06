package cback.utils;

import cback.ConfigManager;
import cback.TestBot;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    static IDiscordClient client = TestBot.getClient();
    static ConfigManager cm = TestBot.getConfigManager();
    static Color BOT_COLOR = Color.decode("#" + cm.getConfigValue("bot_color"));

    private static final Pattern USER_MENTION_PATTERN = Pattern.compile("^<@!?(\\d+)>$");

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
    public static IMessage simpleEmbed(IChannel channel, String message) {
        return sendEmbed(channel, new EmbedBuilder().withDescription(message).withColor(BOT_COLOR).build());
    }

    public static IMessage simpleEmbed(IChannel channel, String message, Color color) {
        return sendEmbed(channel, new EmbedBuilder().withDescription(message).withColor(color).build());
    }

    /**
     * Send sendEmbed objects
     */
    public static IMessage sendEmbed(IChannel channel, EmbedObject embedObject) {
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
        e.printStackTrace();

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
        if (stackString.length() > 1024) {
            stackString = stackString.substring(0, 1800);
        }

        bld
                .appendField("Stack:", stackString, false);

        sendEmbed(errorChannel, bld.build());
    }

    public static void reportHome(Exception e) {
        e.printStackTrace();

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
        if (stackString.length() > 1024) {
            stackString = stackString.substring(0, 1800);
        }

        bld
                .appendField("Stack:", stackString, false);

        sendEmbed(errorChannel, bld.build());
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

        sendEmbed(botLogChannel, bld.build());
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
                    .withAuthorIcon(TestBot.getClient().getApplicationIconURL())
                    .withDesc(command.getDescription())
                    .appendField("Syntax:", TestBot.getPrefix() + command.getSyntax(), false);

            sendEmbed(message.getChannel(), bld.build());
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

                IDiscordClient client = TestBot.getInstance().getClient();
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

                IDiscordClient client = TestBot.getInstance().getClient();
                return new MessageBuilder(client).withEmbed(embed.withColor(color).build())
                        .withChannel(serverLogChannel).send();
            } catch (Exception e) {
                reportHome(e);
            }
            return null;
        });
        return future.get();
    }

    /**
     * Changes the time to a 12 hour format
     */
    public static String to12Hour(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("K:mm").format(dateObj);
        } catch (Exception e) {
            reportHome(e);
        }
        return time;
    }

    /**
     * returns a count of mentions
     */
    public static int mentionsCount(String content) {
        String[] args = content.split(" ");
        if (args.length > 0) {
            int count = 0;
            for (String arg : args) {
                Matcher matcher = USER_MENTION_PATTERN.matcher(arg);
                if (matcher.matches()) {
                    count++;
                }
            }
            System.out.println(count);
            return count;
        } else {
            System.out.println("0");
            return 0;
        }
    }


}
