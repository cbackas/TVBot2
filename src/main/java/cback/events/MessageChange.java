package cback.events;

import cback.ConfigManager;
import cback.TestBot;
import cback.utils.Util;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageDeleteEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageUpdateEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

public class MessageChange {
    private TestBot bot;

    public MessageChange(TestBot bot) {
        this.bot = bot;
    }

    @EventSubscriber
    public void messageDeleted(MessageDeleteEvent event) {
        ConfigManager cm = TestBot.getConfigManager();
        IChannel MESSAGE_LOGS = event.getClient().getChannelByID(Long.parseLong(cm.getConfigValue("MESSAGELOGS_ID")));
        IMessage message = event.getMessage();
        IUser author = event.getAuthor();
        IChannel channel = event.getChannel();

        Boolean tripped = true;
        for (String p : bot.prefixes) {
            if (message.getContent().startsWith(p)) {
                tripped = false;
            }
        }

        if (tripped) {
            EmbedBuilder bld = new EmbedBuilder().withColor(java.awt.Color.decode("#ED4337"));
            bld
                    .withAuthorName(author.getName() + "#" + author.getDiscriminator())
                    .withAuthorIcon(Util.getAvatar(author))
                    .withDesc("**Message sent by **" + author.mention() + "** deleted in **" + channel.mention() + "\n" + message.getContent())
                    .withFooterText("User ID: " + author.getStringID())
                    .withTimestamp(System.currentTimeMillis());

            Util.sendEmbed(MESSAGE_LOGS, bld.build());
        }

    }

    @EventSubscriber
    public void messageEdited(MessageUpdateEvent event) {
        ConfigManager cm = TestBot.getConfigManager();
        IChannel MESSAGE_LOGS = event.getClient().getChannelByID(Long.parseLong(cm.getConfigValue("MESSAGELOGS_ID")));
        IMessage message = event.getMessage();
        IMessage oldMessage = event.getOldMessage();
        IMessage newMessage = event.getNewMessage();
        IUser author = event.getAuthor();
        IChannel channel = event.getChannel();

        EmbedBuilder bld = new EmbedBuilder().withColor(java.awt.Color.decode("#FFA500"));
        bld
                .withAuthorName(author.getName() + "#" + author.getDiscriminator())
                .withAuthorIcon(Util.getAvatar(author))
                .withDesc("**Message Edited in **" + channel.mention())
                .appendField("Before", oldMessage.getContent(), false)
                .appendField("After", newMessage.getContent(), false)
                .withFooterText("ID: " + message.getStringID())
                .withTimestamp(System.currentTimeMillis());

        Util.sendEmbed(MESSAGE_LOGS, bld.build());
    }

}
