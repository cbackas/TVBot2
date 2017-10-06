package cback.commands;

import cback.TestBot;
import cback.utils.Util;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CommandSort implements Command {

    @Override
    public String getName() {
        return "sort";
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "sort";
    }

    @Override
    public String getDescription() {
        return "Alphabetically sorts the channels";
    }

    @Override
    public List<Long> getPermissions() {
        return null;
    }

    @Override
    public void execute(IMessage message, String content, String[] args, IUser author, IGuild guild, List<Long> roleIDs, boolean isPrivate, IDiscordClient client, TestBot bot) {
        /*Util.simpleEmbed(message.getChannel(), "Sorting time! Here we go.");
        //permanent channels sorted by position to keep on top or bottom
        List<IChannel> permChannels = bot.getConfigManager().getConfigArray("permanentchannels").stream()
                .map(id -> guild.getChannelByID(Long.parseLong(id)))
                .sorted((chan1, chan2) -> Integer.compare(chan1.getPosition(), chan2.getPosition()))
                .collect(Collectors.toList());
        List<IChannel> permChannelsTop = new ArrayList<>();
        List<IChannel> permChannelsBottom = new ArrayList<>();
        //add top and bottom channels to their respective lists
        IntStream.range(0, permChannels.size())
                .forEach(index -> {
                    IChannel channel = permChannels.get(index);
                    if (channel.getPosition() == index)
                        permChannelsTop.add(channel);
                    else
                        permChannelsBottom.add(channel);

                });

        //sort non permanent channels
        List<IChannel> showsChannelsSorted = guild.getChannels().stream()
                .filter(chan -> !permChannels.contains(chan))
                .sorted((chan1, chan2) -> getSortName(chan1.getName()).compareTo(getSortName(chan2.getName())))
                .collect(Collectors.toList());

        //add newly sorted channels in order
        List<IChannel> allChannelsSorted = new ArrayList<>();
        allChannelsSorted.addAll(permChannelsTop);
        allChannelsSorted.addAll(showsChannelsSorted);
        allChannelsSorted.addAll(permChannelsBottom);

        //apply new positions
        IntStream.range(0, allChannelsSorted.size()).forEach(position -> {
            IChannel channel = allChannelsSorted.get(position);
            if (!(channel.getPosition() == position)) //don't sort if position is already correct
                RequestBuffer.request(() -> {
                    try {
                        channel.changePosition(position);
                    } catch (DiscordException e) {
                        Util.reportHome(message, e);
                    } catch (MissingPermissionsException e) {
                        Util.reportHome(message, e);
                    }
                });
        });

        Util.simpleEmbed(message.getChannel(), "Sorting complete!");
        Util.deleteMessage(message);
        */

        Util.simpleEmbed(message.getChannel(), "Lets get sorting!");

        ICategory staff = guild.getCategoryByID(365618058070458369l);
        ICategory info = guild.getCategoryByID(365618028005556225l);
        ICategory af = guild.getCategoryByID(365617813462712322l);
        ICategory gl = guild.getCategoryByID(365617925945556993l);
        ICategory mr = guild.getCategoryByID(365617949358424066l);
        ICategory sz = guild.getCategoryByID(365617973555101697l);
        ICategory unsorted = guild.getCategoryByID(365617784354504706l);
        ICategory closed = guild.getCategoryByID(365618492587900929l);

        List<ICategory> permCategories = new ArrayList<>();
        permCategories.add(staff);
        permCategories.add(info);
        permCategories.add(closed);

        List<IChannel> permChannels = new ArrayList<>();
        for (ICategory cat : permCategories) {
            permChannels.addAll(cat.getChannels());
        }

        //permanent channels sorted by position to keep on top or bottom
        List<IChannel> permChannelsTop = new ArrayList<>();
        List<IChannel> permChannelsBottom = new ArrayList<>();
        //add top and bottom channels to their respective lists
        IntStream.range(0, permChannels.size())
                .forEach(index -> {
                    IChannel channel = permChannels.get(index);
                    if (channel.getPosition() == index)
                        permChannelsTop.add(channel);
                    else
                        permChannelsBottom.add(channel);

                });

        for (IChannel c : guild.getChannels()) {
            if (c.getCategory() != null) {
                try {
                    c.changeCategory(unsorted);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //put all the unsorted channels into their categories
        if (!unsorted.getChannels().isEmpty()) {
            for (IChannel c : unsorted.getChannels()) {
                String channelName = getSortName(c.getName());
                String alph = "abcdefghijklmnopqrstuvwxyz";
                char firstLetter = channelName.toLowerCase().charAt(0);
                int index = alph.indexOf(firstLetter) + 1;
                try {
                    if (index <= 6) {
                        c.changeCategory(af);
                    } else if (index > 6 && index <= 12) {
                        c.changeCategory(gl);
                    } else if (index > 12 && index <= 18) {
                        c.changeCategory(mr);
                    } else if (index > 18 && index <= 26) {
                        c.changeCategory(sz);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //sort non permanent channels
        List<IChannel> showsChannelsSorted = guild.getChannels().stream()
                .filter(chan -> !permChannels.contains(chan))
                .sorted(Comparator.comparing(chan -> getSortName(chan.getName())))
                .collect(Collectors.toList());

        //add newly sorted channels in order
        List<IChannel> allChannelsSorted = new ArrayList<>();
        allChannelsSorted.addAll(permChannelsTop);
        allChannelsSorted.addAll(showsChannelsSorted);
        allChannelsSorted.addAll(permChannelsBottom);

        //apply new positions
        IntStream.range(0, allChannelsSorted.size()).forEach(position -> {
            IChannel channel = allChannelsSorted.get(position);
            if (!(channel.getPosition() == position)) //don't sort if position is already correct
                RequestBuffer.request(() -> {
                    try {
                        incCount();
                        channel.changePosition(position);
                    } catch (DiscordException e) {
                        Util.reportHome(message, e);
                    } catch (MissingPermissionsException e) {
                        Util.reportHome(message, e);
                    }
                });
        });

        for (IChannel ch : allChannelsSorted) {
            for (ICategory cat : permCategories) {
                if (cat.getChannels().contains(ch)) {
                    ch.changeCategory(cat);
                }
            }
        }

        Util.simpleEmbed(message.getChannel(), "All done! " + getCount() + " channel(s) sorted.");
    }

    public static String getSortName(String channelName) {
        String newName = channelName.replaceAll("-", " ");
        Matcher matcher = Pattern.compile("^(the|a) ").matcher(newName);
        if (matcher.find()) {
            newName = matcher.replaceFirst("");
        }
        return newName;
    }

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void incCount() {
        this.count++;
    }
}
