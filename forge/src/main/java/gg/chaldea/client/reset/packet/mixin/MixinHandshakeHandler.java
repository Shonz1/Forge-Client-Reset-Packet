package gg.chaldea.client.reset.packet.mixin;

import com.google.common.collect.Maps;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.network.ConnectionData;
import net.minecraftforge.network.HandshakeMessages;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistry;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

@Mixin(HandshakeHandler.class)
public class MixinHandshakeHandler {

        @Shadow(remap = false)
        @Final
        static Marker FMLHSMARKER;
        @Shadow(remap = false)
        @Final
        private static Logger LOGGER;

        @Shadow(remap = false)
        private Set<ResourceLocation> registriesToReceive;
        @Shadow(remap = false)
        private Map<ResourceLocation, ForgeRegistry.Snapshot> registrySnapshots;

        /**
         * @author
         * @reason
         */
        @Overwrite(remap = false)
        void handleServerModListOnClient(HandshakeMessages.S2CModList serverModList, Supplier<NetworkEvent.Context> c) {
                LOGGER.debug(FMLHSMARKER, "Logging into server with mod list [{}]",
                                String.join(", ", serverModList.getModList()));
                c.get().setPacketHandled(true);
                NetworkConstants.handshakeChannel.reply(new HandshakeMessages.C2SModListReply(), c.get());

                LOGGER.debug(FMLHSMARKER, "Accepted server connection");
                // Set the modded marker on the channel, so we know we got packets
                c.get().getNetworkManager().channel().attr(NetworkConstants.FML_NETVERSION)
                                .set(NetworkConstants.NETVERSION);
                c.get().getNetworkManager().channel().attr(NetworkConstants.FML_CONNECTION_DATA)
                                .set(new ConnectionData(serverModList.getModList().stream()
                                        .collect(Collectors.toMap(String::toString, item -> Pair.of(item, item))),
                                                serverModList.getChannels()));

                this.registriesToReceive = new HashSet<>(serverModList.getRegistries());
                this.registrySnapshots = Maps.newHashMap();
                LOGGER.debug(REGISTRIES, "Expecting {} registries: {}", () -> this.registriesToReceive.size(),
                                () -> this.registriesToReceive);
        }

        /**
         * @author
         * @reason
         */
        @Overwrite(remap = false)
        void handleClientModListOnServer(HandshakeMessages.C2SModListReply clientModList,
                        Supplier<NetworkEvent.Context> c) {
                LOGGER.debug(FMLHSMARKER, "Received client connection with modlist [{}]",
                                String.join(", ", clientModList.getModList()));
                boolean accepted = !NetworkRegistry.validateServerChannels(clientModList.getChannels()).isEmpty();
                ((NetworkEvent.Context) c.get()).getNetworkManager().channel()
                                .attr(NetworkConstants.FML_CONNECTION_DATA)
                                .set(new ConnectionData(clientModList.getModList().stream()
                                        .collect(Collectors.toMap(String::toString, item -> Pair.of(item, item))), clientModList.getChannels()));
                ((NetworkEvent.Context) c.get()).setPacketHandled(true);
                if (!accepted) {
                        LOGGER.error(FMLHSMARKER, "Terminating connection with client, mismatched mod list");
                        c.get().getNetworkManager().send(new ClientboundLoginDisconnectPacket(
                                        new TextComponent("Connection closed - mismatched mod channel list")));
                        ((NetworkEvent.Context) c.get()).getNetworkManager()
                                        .disconnect(new TextComponent(
                                                        "Connection closed - mismatched mod channel list"));
                } else {
                        LOGGER.debug(FMLHSMARKER, "Accepted client connection mod list");
                }
        }
}
