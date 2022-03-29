package de.lacodev.staffbungee.managers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChannelManager {

    public ChannelManager() {
        super();
    }

    public void sendInventory(String subChannel, ProxiedPlayer target, String inv) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(target.getName());
        out.writeUTF(inv);

        target.getServer().getInfo().sendData("ProxyServer", out.toByteArray());
    }

}
