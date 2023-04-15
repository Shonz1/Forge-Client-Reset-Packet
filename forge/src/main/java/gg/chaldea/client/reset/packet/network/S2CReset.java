package gg.chaldea.client.reset.packet.network;

import net.minecraft.network.FriendlyByteBuf;

import static net.minecraftforge.network.HandshakeMessages.LoginIndexedMessage;

public class S2CReset extends LoginIndexedMessage {

    public S2CReset() {
        super();
    }

    public void encode(FriendlyByteBuf buffer) {

    }

    public static S2CReset decode(FriendlyByteBuf buffer) {
        return new S2CReset();
    }
}
