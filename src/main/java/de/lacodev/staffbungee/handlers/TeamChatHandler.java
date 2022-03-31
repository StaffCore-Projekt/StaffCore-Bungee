package de.lacodev.staffbungee.handlers;

import de.lacodev.staffbungee.Main;
import java.util.ArrayList;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TeamChatHandler {

    private ArrayList<ProxiedPlayer> participants = new ArrayList<>();
    private ArrayList<ProxiedPlayer> ghosts = new ArrayList<>();
    private String prefix = "§cTC §8| §e";

    public TeamChatHandler() {
        super();
    }

    public void login(ProxiedPlayer player) {

        if (!participants.contains(player)) {

            for (ProxiedPlayer all : participants) {
                all.sendMessage(
                    new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Staff.Joined").replace("%staff%", player.getName())));
            }

            participants.add(player);

            player.sendMessage(new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Self.Joined")));
        }

    }

    public void logout(ProxiedPlayer player) {

        if (participants.contains(player)) {

            participants.remove(player);

            for (ProxiedPlayer all : participants) {
                all.sendMessage(
                    new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Staff.Left").replace("%staff%", player.getName())));
            }

            player.sendMessage(new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Self.Left")));
        }

    }

    public void ghostLogin(ProxiedPlayer player) {

        if (!participants.contains(player)) {

            participants.add(player);
            ghosts.add(player);

            player.sendMessage(new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Self.Joined")));
        }

    }

    public void ghostLogout(ProxiedPlayer player) {

        if (participants.contains(player)) {

            participants.remove(player);
            ghosts.remove(player);

            player.sendMessage(new TextComponent(prefix + Main.getMSG("Messages.TeamChat.Self.Left")));
        }

    }

    public boolean isParticipant(ProxiedPlayer player) {
        return participants.contains(player);
    }

    public boolean isGhost(ProxiedPlayer player) {
        return ghosts.contains(player);
    }

    public ArrayList<ProxiedPlayer> get() {
        return participants;
    }

    public void sendMessage(ProxiedPlayer player, String message) {

        for (ProxiedPlayer all : participants) {

            TextComponent tc = new TextComponent();
            tc.setText(prefix + player.getName() + "§8 » §7" + message);
            tc.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new Text("§7Server §8» §c" + player.getServer().getInfo().getName())));

            all.sendMessage(tc);

        }

    }

}
