package cback.commands;

import cback.TestBot;
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
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, TestBot bot) {
        try {
            List<IChannel> channels = guild.getChannels();
            StringBuilder bld = new StringBuilder();
            for (IChannel c : channels) {
                bld.append("**" + c.getName() + "** - " + c.getStringID());
                bld.append("\n__CH_POS:__ " + c.getPosition());
                if (c.getCategory() != null) {
                    bld.append("\n__CAT:__ " + c.getCategory().getName());
                    bld.append("\n__CAT_POS:__ " + c.getCategory().getPosition());
                }
                bld.append("\n");
            }

            System.out.println(bld.toString());
            Util.sendMessage(message.getChannel(), bld.toString());

            message.delete();
        } catch (Exception e) {
            Util.reportHome(message, e);
        }
    }
}
