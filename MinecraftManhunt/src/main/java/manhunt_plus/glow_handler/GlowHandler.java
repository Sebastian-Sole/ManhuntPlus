package manhunt_plus.glow_handler;

import io.netty.buffer.Unpooled;
import manhunt_plus.PluginMain;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.reflection.resolver.ConstructorResolver;
import org.inventivetalent.glow.reflection.resolver.FieldResolver;
import org.inventivetalent.packetlistener.reflection.minecraft.Minecraft;
import org.inventivetalent.packetlistener.reflection.minecraft.MinecraftVersion;
import org.inventivetalent.packetlistener.reflection.resolver.MethodResolver;
import org.inventivetalent.packetlistener.reflection.resolver.minecraft.NMSClassResolver;


import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class GlowHandler {

    private static Class<?> PacketPlayOutEntityMetadata;
    static Class<?> DataWatcher;
    static Class<?> DataWatcherItem;
    private static Class<?> Entity;

    private static FieldResolver PacketPlayOutMetadataFieldResolver;
    private static FieldResolver EntityFieldResolver;
    private static FieldResolver DataWatcherFieldResolver;
    static FieldResolver DataWatcherItemFieldResolver;

    private static ConstructorResolver PacketPlayOutMetadataResolver;
    private static ConstructorResolver DataWatcherItemConstructorResolver;

    private static MethodResolver DataWatcherMethodResolver;
    static MethodResolver DataWatcherItemMethodResolver;
    private static MethodResolver EntityMethodResolver;

    //Packets
    private static FieldResolver EntityPlayerFieldResolver;
    private static MethodResolver PlayerConnectionMethodResolver;

    private static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();

    static boolean isPaper = false;

    private PluginMain main;

    public GlowHandler(PluginMain main) {
        this.main = main;
    }

    public void addGlow(Player player) throws IllegalAccessException {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();

        var prevDataWatcherList = entityPlayer.ai().c();
        prevDataWatcherList.get(6).a(true);

        PacketDataSerializer dataSerializer = new PacketDataSerializer(Unpooled.buffer());
        PacketPlayOutEntityMetadata entityMetaData = new PacketPlayOutEntityMetadata(entityPlayer.getBukkitEntity().getEntityId(), entityPlayer.ai(),false);

        PlayerConnection playerConnection = entityPlayer.b; // This is a player connection
        playerConnection.a(entityMetaData);

    }

//    public void setGlowing(Player glowingPlayer, Player sendPacketPlayer, boolean glow) {
//        try {
//            if (PacketPlayOutEntityMetadata == null) {
//                PacketPlayOutEntityMetadata = NMS_CLASS_RESOLVER.resolve("network.protocol.game.PacketPlayOutEntityMetadata");
//            }
//            if (DataWatcher == null) {
//                DataWatcher = NMS_CLASS_RESOLVER.resolve("network.syncher.DataWatcher");
//            }
//            if (DataWatcherItem == null) {
//                DataWatcherItem = NMS_CLASS_RESOLVER.resolve("network.syncher.DataWatcher$Item");
//            }
//            if (Entity == null) {
//                Entity = NMS_CLASS_RESOLVER.resolve("world.entity.Entity");
//            }
//            if (PacketPlayOutMetadataFieldResolver == null) {
//                PacketPlayOutMetadataFieldResolver = new FieldResolver(PacketPlayOutEntityMetadata);
//            }
//            if (PacketPlayOutMetadataResolver == null) {
//                PacketPlayOutMetadataResolver = new ConstructorResolver(PacketPlayOutEntityMetadata);
//            }
//            if (DataWatcherItemConstructorResolver == null) {
//                DataWatcherItemConstructorResolver = new ConstructorResolver(DataWatcherItem);
//            }
//            if (EntityFieldResolver == null) {
//                EntityFieldResolver = new FieldResolver(Entity);
//            }
//            if (DataWatcherMethodResolver == null) {
//                DataWatcherMethodResolver = new MethodResolver(DataWatcher);
//            }
//            if (DataWatcherItemMethodResolver == null) {
//                DataWatcherItemMethodResolver = new MethodResolver(DataWatcherItem);
//            }
//            if (EntityMethodResolver == null) {
//                EntityMethodResolver = new MethodResolver(Entity);
//            }
//            if (DataWatcherFieldResolver == null) {
//                DataWatcherFieldResolver = new FieldResolver(DataWatcher);
//            }
//            EntityPlayer entityPlayer = ((CraftPlayer) glowingPlayer).getHandle();
//
//            DataWatcher toCloneDataWatcher = entityPlayer.ai();
//            DataWatcher newDataWatcher = new DataWatcher(entityPlayer);
//
//            List list = new ArrayList();
//
//            // The map that stores the DataWatcherItems is private within the DataWatcher Object.
//            // We need to use Reflection to access it from Apache Commons and change it.
//            toCloneDataWatcher.b(new DataWatcherObject<>(0, DataWatcherRegistry.a), (byte) 0x40);
////            Map<Integer, Object> dataWatcherItems = (Map<Integer, Object>) DataWatcherFieldResolver
////                    .resolveByLastType(Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap"))
////                    .get(EntityMethodResolver.resolve("getDataWatcher").invoke(Minecraft.getHandle(glowingPlayer)));
//            Map<Integer, DataWatcher.Item<?>> newMap = new HashMap<>();
//
//
//            // We need to clone the DataWatcher.Items because we don't want to point to those values anymore.
//            byte prev = (byte) DataWatcherItemMethodResolver.resolve("b").invoke(Objects.requireNonNull(toCloneDataWatcher.b()).get(0));
//            byte b = (byte) (glow ? (prev | 1 << 6) : (prev & ~(1 << 6)));//6 = glowing index
//            Object dataWatcherItem = DataWatcherItemConstructorResolver.resolveFirstConstructor().newInstance(newDataWatcher, b);
//
//
//            //The glowing item
//            list.add(dataWatcherItem);
//
//            Object packetMetadata = PacketPlayOutMetadataResolver
//                    .resolve(new Class[]{int.class, DataWatcher, boolean.class})
//                    .newInstance(-glowingPlayer.getEntityId(), toCloneDataWatcher, true);
//            List dataWatcherList = (List) PacketPlayOutMetadataFieldResolver.resolve("b").get(packetMetadata);
//            dataWatcherList.clear();
//            dataWatcherList.addAll(list);
//            sendPacket(packetMetadata, sendPacketPlayer);
//
//            // Set the newDataWatcher's (unlinked) map data
////            FieldUtils.writeDeclaredField(newDataWatcher, "d", newMap, true);
////
////            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(glowingPlayer.getEntityId(), toCloneDataWatcher, true);
////
////            ((CraftPlayer) sendPacketPlayer).getHandle().b.a(metadataPacket);
//
//
//
//
//        } catch (IllegalAccessException e) { // Catch statement necessary for FieldUtils.readDeclaredField()
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (ReflectiveOperationException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected static void sendPacket(Object packet, Player p) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
//        if (EntityPlayerFieldResolver == null) {
//            EntityPlayerFieldResolver = new FieldResolver(NMS_CLASS_RESOLVER.resolve("server.level.EntityPlayer"));
//        }
//        if (PlayerConnectionMethodResolver == null) {
//            PlayerConnectionMethodResolver = new MethodResolver(NMS_CLASS_RESOLVER.resolve("server.network.PlayerConnection"));
//        }
//
//        try {
//            Object handle = Minecraft.getHandle(p);
//            final Object connection;
//
//            if (MinecraftVersion.VERSION.newerThan(Minecraft.Version.v1_17_R1)) { // even playerConnection got changed!
//                connection = EntityPlayerFieldResolver.resolve("b").get(handle);
//            } else {
//                connection = EntityPlayerFieldResolver.resolve("playerConnection").get(handle);
//            }
//
//            PlayerConnectionMethodResolver.resolve("sendPacket").invoke(connection, packet);
//        } catch (ReflectiveOperationException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    protected static void sendGlowPacket(Entity entity, boolean wasGlowing, boolean glowing, Player receiver) {
//        try {
//            if (PacketPlayOutEntityMetadata == null) {
//                PacketPlayOutEntityMetadata = NMS_CLASS_RESOLVER.resolve("network.protocol.game.PacketPlayOutEntityMetadata");
//            }
//            if (DataWatcher == null) {
//                DataWatcher = NMS_CLASS_RESOLVER.resolve("network.syncher.DataWatcher");
//            }
//            if (DataWatcherItem == null) {
//                DataWatcherItem = NMS_CLASS_RESOLVER.resolve("network.syncher.DataWatcher$Item");
//            }
//            if (Entity == null) {
//                Entity = NMS_CLASS_RESOLVER.resolve("world.entity.Entity");
//            }
//            if (PacketPlayOutMetadataFieldResolver == null) {
//                PacketPlayOutMetadataFieldResolver = new FieldResolver(PacketPlayOutEntityMetadata);
//            }
//            if (PacketPlayOutMetadataResolver == null) {
//                PacketPlayOutMetadataResolver = new ConstructorResolver(PacketPlayOutEntityMetadata);
//            }
//            if (DataWatcherItemConstructorResolver == null) {
//                DataWatcherItemConstructorResolver = new ConstructorResolver(DataWatcherItem);
//            }
//            if (EntityFieldResolver == null) {
//                EntityFieldResolver = new FieldResolver(Entity);
//            }
//            if (DataWatcherMethodResolver == null) {
//                DataWatcherMethodResolver = new MethodResolver(DataWatcher);
//            }
//            if (DataWatcherItemMethodResolver == null) {
//                DataWatcherItemMethodResolver = new MethodResolver(DataWatcherItem);
//            }
//            if (EntityMethodResolver == null) {
//                EntityMethodResolver = new MethodResolver(Entity);
//            }
//            if (DataWatcherFieldResolver == null) {
//                DataWatcherFieldResolver = new FieldResolver(DataWatcher);
//            }
//
//            List list = new ArrayList();
//
//            //Existing values
//            Object dataWatcher = EntityMethodResolver.resolve("getDataWatcher").invoke(Minecraft.getHandle(entity));
//            Class dataWatcherItemsType;
//            if (isPaper) {
//                dataWatcherItemsType = Class.forName("it.unimi.dsi.fastutil.ints.Int2ObjectMap");
//            } else {
//                dataWatcherItemsType = Class.forName("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.Int2ObjectMap");
//            }
//            Map<Integer, Object> dataWatcherItems = (Map<Integer, Object>) DataWatcherFieldResolver.resolveByLastType(dataWatcherItemsType).get(dataWatcher);
//
//            Object dataWatcherObject = org.inventivetalent.reflection.minecraft.DataWatcher.V1_9.ValueType.ENTITY_SHARED_FLAGS.getType();
//            byte prev = (byte) (dataWatcherItems.isEmpty() ? 0 : DataWatcherItemMethodResolver.resolve("b").invoke(dataWatcherItems.get(0)));
//            byte b = (byte) (glowing ? (prev | 1 << 6) : (prev & ~(1 << 6)));//6 = glowing index
//            Object dataWatcherItem = DataWatcherItemConstructorResolver.resolveFirstConstructor().newInstance(dataWatcherObject, b);
//
//            //The glowing item
//            list.add(dataWatcherItem);
//
//            Object packetMetadata = PacketPlayOutMetadataResolver
//                    .resolve(new Class[]{int.class, DataWatcher, boolean.class})
//                    .newInstance(-entity.getEntityId(), dataWatcher, true);
//            List dataWatcherList = (List) PacketPlayOutMetadataFieldResolver.resolve("b").get(packetMetadata);
//            dataWatcherList.clear();
//            dataWatcherList.addAll(list);
//
//            sendPacket(packetMetadata, receiver);
//        } catch (ReflectiveOperationException e) {
//            throw new RuntimeException(e);
//        }
//    }


}
