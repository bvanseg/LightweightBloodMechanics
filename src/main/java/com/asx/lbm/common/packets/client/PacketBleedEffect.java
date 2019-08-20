package com.asx.lbm.common.packets.client;

import com.asx.lbm.common.BloodHandler;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.mdx.lib.util.Game;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBleedEffect implements IMessage, IMessageHandler<PacketBleedEffect, PacketBleedEffect>
{
    private int   entityId;
    private float spread;
    private int   amount;

    public PacketBleedEffect()
    {
        ;
    }

    public PacketBleedEffect(EntityLivingBase living, float spread, int amount)
    {
        this.entityId = living.getEntityId();
        this.spread = spread;
        this.amount = amount;

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            BloodHandler.bleedEffect(living, spread, amount);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.spread = buf.readFloat();
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.spread);
        buf.writeInt(this.amount);
    }

    @Override
    public PacketBleedEffect onMessage(PacketBleedEffect packet, MessageContext ctx)
    {
        Game.minecraft().addScheduledTask(new Runnable() {
            @Override
            public void run()
            {
                if (Game.minecraft().player != null && Game.minecraft().player.world != null)
                {
                    Entity entity = Game.minecraft().player.world.getEntityByID(packet.entityId);

                    if (entity instanceof EntityLivingBase)
                    {
                        EntityLivingBase living = (EntityLivingBase) entity;
                        Bleedable bleedable = (Bleedable) living.getCapability(Provider.CAPABILITY, null);

                        if (bleedable != null)
                        {
                            BloodHandler.bleedEffect(living, packet.spread, packet.amount);
                        }
                    }
                }
            }
        });

        return null;
    }
}
