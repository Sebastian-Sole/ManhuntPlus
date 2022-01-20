package manhunt_plus.glow_handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import manhunt_plus.PluginMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;

public class GlowHandler {

    private final PluginMain main;
    private List<Player> seeGlow = new ArrayList<>();

    public GlowHandler(PluginMain main) {
        this.main = main;
    }

    public void showGlow() {
        var protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(main, PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    seeGlow = main.getTeam(player);
                    if (seeGlow.contains(event.getPlayer())) {
                        if (player.getEntityId() == event.getPacket().getIntegers().read(0)) {
                            if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
                                List<WrappedWatchableObject> watchableObjectList = event.getPacket().getWatchableCollectionModifier().read(0);
                                for (WrappedWatchableObject metadata : watchableObjectList) {
                                    if (metadata.getIndex() == 0) {
                                        byte b = (byte) metadata.getValue();
                                        b |= 0b01000000;
                                        metadata.setValue(b);
                                    }
                                }
                            }
                            else {
                                WrappedDataWatcher watcher = event.getPacket().getDataWatcherModifier().read(0);
                                if (watcher.hasIndex(0)) {
                                    byte b = watcher.getByte(0);
                                    b |= 0b01000000;
                                    watcher.setObject(0, b);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
