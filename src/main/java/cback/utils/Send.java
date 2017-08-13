package cback.utils;

import ca.momoperes.canarywebhooks.DiscordMessage;
import ca.momoperes.canarywebhooks.embed.DiscordEmbed;
import cback.Report;
import cback.TVBot;
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
        Send.embed(channel, new EmbedBuilder().withDescription(message).withColor(TVBot.BOT_COLOR).build());
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
        String REPORT_WEBHOOK = TVBot.getConfigManager().getConfigValue("errors_webhook");
        Report sReport = report;

        DiscordEmbed embed = new DiscordEmbed.Builder()
                .withTitle("Our title") // The title of the embed element
                .withURL("https://github.com/momothereal") // The URL of the embed element
                .withDescription("momothereal's github page") // The description of the embed object
                .withColor(TVBot.BOT_COLOR)
                .build(); // Build the embed element

        DiscordMessage message = new DiscordMessage.Builder("Check out this link:") // The content of the message
                .withEmbed(embed) // Add our embed object
                .withUsername("cool guy") // Override the username of the bot
                .build(); // Build the message

        try {
            Util.webhook(REPORT_WEBHOOK).sendPayload(message);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
