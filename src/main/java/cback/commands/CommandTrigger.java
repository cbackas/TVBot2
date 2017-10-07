package cback.commands;

import cback.TestBot;
import cback.TraktManager;
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
            String theflash1 = bot.getTraktManager().searchTmdbShow("the flash");
            String theflash2 = bot.getTraktManager().searchTmdbShow("th flash");
            String theflash3 = bot.getTraktManager().searchTmdbShow("the flashh");

            System.out.println(theflash1);
            System.out.println(theflash2);
            System.out.println(theflash3);


            message.delete();
        } catch (Exception e) {
            Util.reportHome(message, e);
        }
    }
}
