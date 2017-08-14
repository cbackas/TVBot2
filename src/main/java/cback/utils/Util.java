package cback.utils;

import cback.TVBot;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.net.URISyntaxException;

public class Util {

    /*
     * Establishes config file path
     */
    public static File botPath;

    static {
        try {
            botPath = new File(TVBot.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /*
     * Returns a default discord avatar if a user's avatar is null for some reason
     */
    public static String getAvatar(IUser user) {
        return user.getAvatar() != null ? user.getAvatarURL() : "https://discordapp.com/assets/322c936a8c8be1b803cd94861bdfa868.png";
    }
}
