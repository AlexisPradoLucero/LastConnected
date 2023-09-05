package me.alexisprado.lastconnected;

import gearth.extensions.Extension;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.parsers.HUserProfile;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import java.text.SimpleDateFormat;
import java.util.Date;

@ExtensionInfo(
        Title = "LastConnected",
        Description = "Find out when a user last connected",
        Version = "1.0",
        Author = "AlexisPrado"
)

public class LastConnected extends Extension {

    int seconds;

    private LastConnected(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        new LastConnected(args).run();
    }

    protected void initExtension() {
        intercept(HMessage.Direction.TOCLIENT, "ExtendedProfile", hMessage -> {
            HPacket hPacket = hMessage.getPacket();
            try {
                HUserProfile userProfile = new HUserProfile(hPacket);
                seconds = userProfile.getLastAccessSince();

                Date lastConnected = new Date(System.currentTimeMillis() - (seconds * 1000L));

                if (isToday(lastConnected)) {
                    System.out.println("Last connected today at " + formatTime(lastConnected));
                    sendToClient(new HPacket("IssueCloseNotification", HMessage.Direction.TOCLIENT, 1, "Last connected today at " + formatTime(lastConnected)));
                } else if (isYesterday(lastConnected)) {
                    System.out.println("Last connected yesterday at " + formatTime(lastConnected));
                    sendToClient(new HPacket("IssueCloseNotification", HMessage.Direction.TOCLIENT, 1, "Last connected yesterday at " + formatTime(lastConnected)));
                } else {
                    System.out.println("Last connected on " + formatDate(lastConnected) + " at " + formatTime(lastConnected));
                    sendToClient(new HPacket("IssueCloseNotification", HMessage.Direction.TOCLIENT, 1, "Last connected on " + formatDate(lastConnected) + " at " + formatTime(lastConnected)));
                }
            } catch (Exception e) {
            }
        });
    }

    public static boolean isToday(Date date) {
        Date today = new Date();
        return (date.getDate() == today.getDate() &&
                date.getMonth() == today.getMonth() &&
                date.getYear() == today.getYear());
    }

    public static boolean isYesterday(Date date) {
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        return (date.getDate() == yesterday.getDate() &&
                date.getMonth() == yesterday.getMonth() &&
                date.getYear() == yesterday.getYear());
    }

    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
        return format.format(date);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }
}
