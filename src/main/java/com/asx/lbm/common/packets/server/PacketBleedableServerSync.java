package com.asx.lbm.common.packets.server;

import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBleedableServerSync implements IMessage, IMessageHandler<PacketBleedableServerSync, PacketBleedableServerSync>
{
    public NBTTagCompound tag;
    private int           entityId;

    public PacketBleedableServerSync()
    {
        ;
    }

    public PacketBleedableServerSync(int entityId, NBTTagCompound tag)
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
    public PacketBleedableServerSync onMessage(PacketBleedableServerSync packet, MessageContext ctx)
    {
        ctx.getServerHandler().player.getServerWorld().addScheduledTask(new Runnable() {
            @Override
            public void run()
            {
                Entity entity = ctx.getServerHandler().player.world.getEntityByID(packet.entityId);

                if (entity != null)
                {
                    Bleedable bleedable = (Bleedable) entity.getCapability(Provider.CAPABILITY, null);

                    if (bleedable != null)
                    {
                        Provider.CAPABILITY.getStorage().readNBT(Provider.CAPABILITY, bleedable, null, packet.tag);
                    }
                }
            }
        });

        return null;
    }
}
