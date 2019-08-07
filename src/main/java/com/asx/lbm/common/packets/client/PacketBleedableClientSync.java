package com.asx.lbm.common.packets.client;

import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.mdx.lib.util.Game;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBleedableClientSync implements IMessage, IMessageHandler<PacketBleedableClientSync, PacketBleedableClientSync>
{
    public NBTTagCompound tag;
    private int           entityId;

    public PacketBleedableClientSync()
    {
        ;
    }

    public PacketBleedableClientSync(int entityId, NBTTagCompound tag)
    {
        this.entityId = entityId;
        this.tag = tag;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public PacketBleedableClientSync onMessage(PacketBleedableClientSync packet, MessageContext ctx)
    {
        Game.minecraft().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                if (Game.minecraft().player != null && Game.minecraft().player.world != null)
                {
                    Entity entity = Game.minecraft().player.world.getEntityByID(packet.entityId);

                    if (entity != null)
                    {
                        Bleedable bleedable = (Bleedable) entity.getCapability(Provider.CAPABILITY, null);

                        if (bleedable != null)
                        {
                            Provider.CAPABILITY.getStorage().readNBT(Provider.CAPABILITY, bleedable, null, packet.tag);
                        }
                    }
                }
            }
        });

        return null;
    }
}
