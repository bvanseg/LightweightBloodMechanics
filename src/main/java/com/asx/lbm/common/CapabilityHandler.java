package com.asx.lbm.common;

import com.asx.lbm.LBM;
import com.asx.lbm.common.capabilities.IBleedableCapability;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.mdx.core.mods.IPreInitEvent;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler implements IPreInitEvent
{
    public static final ResourceLocation BLEEDABLE = new ResourceLocation(LBM.Properties.ID, "bleedable");

    @Override
    public void pre(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(IBleedableCapability.class, new IBleedableCapability.Bleedable(), new IBleedableCapability.Bleedable.Factory());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityLivingBase)
        {
            event.addCapability(BLEEDABLE, new IBleedableCapability.Provider());
        }
    }

    @SubscribeEvent
    public void onEntityTrackEvent(PlayerEvent.StartTracking event)
    {
        this.syncEntity(event.getTarget());
    }

    @SubscribeEvent
    public void onEntitySpawnInWorld(EntityJoinWorldEvent event)
    {
        if (event.getEntity() != null && !event.getEntity().world.isRemote)
        {
            if (event.getEntity() instanceof EntityLivingBase)
            {
                Bleedable bleedable = (Bleedable) event.getEntity().getCapability(Provider.CAPABILITY, null);
                bleedable.initialize((EntityLivingBase) event.getEntity());
            }

            this.syncEntity(event.getEntity());
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event)
    {
        ;
    }

    public void syncEntity(Entity target)
    {
        WorldServer worldServer = (WorldServer) target.world;

        if (worldServer != null)
        {
            EntityTracker tracker = worldServer.getEntityTracker();

            if (tracker != null && target != null)
            {
                if (target instanceof EntityLivingBase)
                {
                    Bleedable organism = (Bleedable) target.getCapability(IBleedableCapability.Provider.CAPABILITY, null);

                    if (organism != null)
                    {
                        if (target instanceof EntityPlayer)
                        {
                            EntityPlayer player = (EntityPlayer) target;
                            organism.syncClients(player);
                        }
                    }
                }
            }
        }
    }
}
