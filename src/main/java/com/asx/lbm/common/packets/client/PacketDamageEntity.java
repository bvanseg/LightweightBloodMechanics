package com.asx.lbm.common.packets.client;

import com.asx.lbm.common.BloodHandler;
import com.asx.mdx.lib.util.Game;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDamageEntity implements IMessage, IMessageHandler<PacketDamageEntity, PacketDamageEntity>
{
    private int entityId;
    private float amount;
    private boolean isExplosion;
    private boolean isProjectile;

    public PacketDamageEntity()
    {
        ;
    }

    public PacketDamageEntity(EntityLivingBase living, float amount, DamageSource source)
    {
        this.entityId = living.getEntityId();
        this.amount = amount;
        this.isExplosion = source.isExplosion();
        this.isProjectile = source.isProjectile();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityId = buf.readInt();
        this.amount = buf.readFloat();
        this.isExplosion = buf.readBoolean();
        this.isProjectile = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.entityId);
        buf.writeFloat(this.amount);
        buf.writeBoolean(this.isExplosion);
        buf.writeBoolean(this.isProjectile);
    }

    @Override
    public PacketDamageEntity onMessage(PacketDamageEntity packet, MessageContext ctx)
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
                        BloodHandler.handleInjury(living, packet.amount, packet.isExplosion, packet.isProjectile);
                    }
                }
            }
        });

        return null;
    }
}
