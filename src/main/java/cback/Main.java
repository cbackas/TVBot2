package cback;

import sx.blah.discord.util.DiscordException;

public class Main {

    public static void main(String[] args) {

        //add shutdown hook to logout on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down...");
            if (TVBot.getClient() != null) {
                try {
                    TVBot.getClient().logout();
                } catch (DiscordException e) {
                    e.printStackTrace();
                }
            }
        }));

        //initiate SoundBot
        new TVBot();
    }

}
