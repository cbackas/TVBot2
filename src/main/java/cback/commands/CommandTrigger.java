package cback.commands;

import cback.Report;
import cback.TVBot;
import cback.utils.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;

public class CommandTrigger implements Command {
    @Override
    public String getName() {
        return "Trigger";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<Long> getPermissions() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, TVBot bot) {
        try {
            List<IChannel> channels = client.getChannels();
            for (IChannel c : channels) {
                System.out.println(c.getName() + " - - - - - " + c.getStringID());
            }

            message.delete();
        } catch (Exception e) {
            Util.report(new Report(message, e));
        }
    }
}
