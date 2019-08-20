package com.asx.lbm;

import com.asx.lbm.common.packets.client.PacketBleedEffect;
import com.asx.lbm.common.packets.client.PacketBleedableClientSync;
import com.asx.lbm.common.packets.server.PacketBleedableServerSync;
import com.asx.mdx.core.mods.IInitEvent;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler extends SimpleNetworkWrapper implements IInitEvent
{
    private int descriminator = 0;

    public NetworkHandler()
    {
        super(LBM.Properties.ID.toUpperCase());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        /** Send to the server **/
        this.registerMessage(Side.SERVER, PacketBleedableServerSync.class);
        
        /** Send to the client **/
        this.registerMessage(Side.CLIENT, PacketBleedEffect.class);
        this.registerMessage(Side.CLIENT, PacketBleedableClientSync.class);
    }

    /**
     * @param side - The side this packet will be sent to.
     * @param packet - The packet being registered.
     */
    @SuppressWarnings("unchecked")
    private <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Side side, Class<?> packet)
    {
        this.registerMessage((Class<? extends IMessageHandler<REQ, REPLY>>) packet, (Class<REQ>) packet, descriminator++, side);
    }
}
