package gg.chaldea.client.reset.packet.mixin;

import io.netty.channel.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Connection.class)
public class MixinConnection {

    @Shadow
    private Channel channel;
    @Shadow
    private Component disconnectedReason;

    // TODO: Rework stack on negotiation screen
//    /**
//     * @author
//     * @reason
//     */
//    @Overwrite
//    @OnlyIn(Dist.CLIENT)
//    public void disconnect(Component p_150718_1_) {
//        if (this.channel.isOpen()) {
//            this.channel.close().awaitUninterruptibly();
//            this.disconnectedReason = p_150718_1_;
//
//            if (this.disconnectedReason == null) {
//                this.disconnectedReason = new TextComponent("Unknown error");
//            }
//
//            if (Minecraft.getInstance().screen instanceof GenericDirtMessageScreen) {
//                Component title = ((GenericDirtMessageScreen) Minecraft.getInstance().screen).getTitle();
//
//                if (title instanceof TranslatableComponent &&
//                        ((TranslatableComponent) title).getKey().equals("connect.negotiating")) {
//                    Minecraft.getInstance()
//                            .setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()),
//                                    new TextComponent(""), this.disconnectedReason));
//                }
//            }
//        }
//    }
}
