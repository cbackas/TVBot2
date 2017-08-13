package cback.utils;

import ca.momoperes.canarywebhooks.WebhookClient;
import ca.momoperes.canarywebhooks.WebhookClientBuilder;
import cback.TVBot;

import java.io.File;
import java.net.URI;
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
     * Give webhook object
     */
    public static WebhookClient webhook(String URL) {
        try {
            WebhookClient client = new WebhookClientBuilder()
                    .withURI(new URI(URL))
                    .build();

            return client;
        } catch (Exception e) {
        }
        return null;
    }
}
