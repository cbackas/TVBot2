package cback;

import cback.commands.Command;
import cback.events.ChannelChange;
import cback.events.MemberChange;
import cback.events.MessageChange;
import cback.utils.*;
import org.reflections.Reflections;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.Configuration;
import sx.blah.discord.util.DiscordException;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestBot {

    private static TestBot instance;

    private IDiscordClient client;
    private static ConfigManager configManager;
    private TraktManager traktManager;

    public static List<Command> registeredCommands = new ArrayList<>();

    static private String prefix = ">";
    private Pattern COMMAND_PATTERN = Pattern.compile("^\\>([^\\s]+) ?(.*)", Pattern.CASE_INSENSITIVE);
    public List<String> prefixes = new ArrayList<>();

    public static final long ERRORLOG_CH_ID = 346104666796589056l;
    public static final long BOTLOG_CH_ID = 346483682376286208l;
    public static final long BOTPM_CH_ID = 346104720903110656l;

    private static IGuild homeGuild;
    private static IGuild hubGuild;

    private long startTime;

    public static void main(String[] args) {
        new TestBot();
    }

    public TestBot() {
        instance = this;
        registerAllCommands();

        //instantiate config manager first as connect() relies on tokens
        configManager = new ConfigManager(this);
        prefixes.add(TestBot.getPrefix());
        prefixes.add("t!");
        prefixes.add("!");
        prefixes.add("!g");
        prefixes.add("--");
        prefixes.add(".");

        connect();
        client.getDispatcher().registerListener(this);
        client.getDispatcher().registerListener(new MessageChange(this));
        client.getDispatcher().registerListener(new MemberChange(this));
        client.getDispatcher().registerListener(new ChannelChange(this));

        traktManager = new TraktManager(this);
    }

    private void connect() {
        //don't load external modules and don't attempt to create modules folder
        Configuration.LOAD_EXTERNAL_MODULES = false;

        Optional<String> token = configManager.getTokenValue("botToken");
        if (!token.isPresent()) {
            System.out.println("-------------------------------------");
            System.out.println("Insert your bot's token in the config.");
            System.out.println("Exiting......");
            System.out.println("-------------------------------------");
            System.exit(0);
            return;
        }

        prefix = configManager.getConfigValue("command_prefix");

        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token.get());
        clientBuilder.setMaxReconnectAttempts(5);
        try {
            client = clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace();
        }
    }

    /*
     * Message Central Choo Choo
     */
    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) {
        if (event.getMessage().getAuthor().isBot()) return; //ignore bot messages
        IMessage message = event.getMessage();
        IGuild guild = null;
        boolean isPrivate = message.getChannel().isPrivate();
        if (!isPrivate) guild = message.getGuild();
        String text = message.getContent();
        Matcher matcher = COMMAND_PATTERN.matcher(text);
        if (matcher.matches()) {
            String baseCommand = matcher.group(1).toLowerCase();
            Optional<Command> command = registeredCommands.stream()
                    .filter(com -> com.getName().equalsIgnoreCase(baseCommand) || (com.getAliases() != null && com.getAliases().contains(baseCommand)))
                    .findAny();
            if (command.isPresent()) {
                System.out.println("@" + message.getAuthor().getName() + " issued \"" + text + "\" in " +
                        (isPrivate ? ("@" + message.getAuthor().getName()) : guild.getName()));

                String args = matcher.group(2);
                String[] argsArr = args.isEmpty() ? new String[0] : args.split(" ");

                List<Long> roleIDs = message.getAuthor().getRolesForGuild(guild).stream().map(role -> role.getLongID()).collect(Collectors.toList());

                IUser author = message.getAuthor();
                String content = message.getContent();

                Command cCommand = command.get();

                /*
                 * If user has permission to run the command: Command executes and botlogs
                 */
                if (cCommand.getPermissions() == null || !Collections.disjoint(roleIDs, cCommand.getPermissions())) {
                    cCommand.execute(message, content, argsArr, author, guild, roleIDs, isPrivate, client, this);
                    Util.botLog(message);
                } else {
                    Util.simpleEmbed(message.getChannel(), "You don't have permission to perform this command.");
                }
            }
        } else if (!message.getChannel().isPrivate()) {
            /**
             * Deletes messages/bans users for using too many @ mentions
             */
            if (Util.mentionsCount(message.getContent()) > 1) {
                try {
                    Util.simpleEmbed(message.getChannel(), "I ban u");
                } catch (Exception e) {
                    Util.reportHome(e);
                }
            } else if (Util.mentionsCount(message.getContent()) > 0) {
                Util.simpleEmbed(message.getChannel(), "I delete you");
            }
        }
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {
        checkChannels();
        System.out.println("Logged in.");

        startTime = System.currentTimeMillis();
    }


    /*
     * Misc Utilities
     */
    public static TestBot getInstance() {
        return instance;
    }

    public static IDiscordClient getClient() {
        return instance.client;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public TraktManager getTraktManager() {
        return traktManager;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static IGuild getHomeGuild() {
        homeGuild = getClient().getGuildByID(Long.parseLong(configManager.getConfigValue("HOMESERVER_ID")));
        return homeGuild;
    }

    public static IGuild getHubGuild() {
        hubGuild = getClient().getGuildByID(346104115169853440l);
        return hubGuild;
    }


    private void registerAllCommands() {
        new Reflections("cback.commands").getSubTypesOf(Command.class).forEach(commandImpl -> {
            try {
                Command command = commandImpl.newInstance();
                Optional<Command> existingCommand = registeredCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(command.getName())).findAny();
                if (!existingCommand.isPresent()) {
                    registeredCommands.add(command);
                    System.out.println("Registered command: " + command.getName());
                } else {
                    System.out.println("Attempted to register two commands with the same name: " + existingCommand.get().getName());
                    System.out.println("Existing: " + existingCommand.get().getClass().getName());
                    System.out.println("Attempted: " + commandImpl.getName());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private void checkChannels() {
        List<String> homeChannels = new ArrayList<>();
        homeChannels.add(configManager.getConfigValue("SERVERLOG_ID"));
        homeChannels.add(configManager.getConfigValue("MESSAGELOGS_ID"));

        for (String id : homeChannels) {
            try {
                IChannel channel = getHomeGuild().getChannelByID(Long.parseLong(id));
                System.out.printf("%s channel matched from config\n", channel.getName());
            } catch (Exception e) {
                System.out.printf("ID %s did not match a channel in the home server\n", id);
            }
        }

        List<String> hubChannels = new ArrayList<>();
        hubChannels.add(configManager.getConfigValue("COMMANDLOG_ID"));
        hubChannels.add(configManager.getConfigValue("ERORRLOG_ID"));

        for (String id : hubChannels) {
            try {
                IChannel channel = getHubGuild().getChannelByID(Long.parseLong(id));
            } catch (Exception e) {
                System.out.printf("ID %s did not match a channel in the hub server\n", id);
            }
        }
    }

    public String getUptime() {
        long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600);
        return (hours < 10 ? "0" + hours : hours) + "h " + (minutes < 10 ? "0" + minutes : minutes) + "m " + (seconds < 10 ? "0" + seconds : seconds) + "s";
    }
}