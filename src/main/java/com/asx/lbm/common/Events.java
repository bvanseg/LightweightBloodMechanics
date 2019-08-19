package com.asx.lbm.common;

import com.asx.lbm.LBM;
import com.asx.lbm.client.Resources;
import com.asx.lbm.client.Shaders;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.lbm.common.packets.client.PacketBleed;
import com.asx.mdx.lib.client.util.Draw;
import com.asx.mdx.lib.client.util.OpenGL;
import com.asx.mdx.lib.util.Game;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Events
{
    public static Events INSTANCE;

    private static long  lastBloodBarActivationTime = System.currentTimeMillis();
    private static int   bloodBarDisplayPeriod      = 1000 * 5;

    public Events()
    {
        INSTANCE = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void activateBloodBar()
    {
        lastBloodBarActivationTime = System.currentTimeMillis();
    }

    @SideOnly(Side.CLIENT)
    public static void drawVerticalProgressBar(int progress, int maxProgress, int posX, int posY, int width, int height, int color, int color2)
    {
        progress = progress > maxProgress ? maxProgress : progress;

        int percent = progress * 100 / maxProgress;
        int maxBarHeight = height - 2;
        int barHeight = percent * maxBarHeight / 100;

        Gui.drawRect(posX + 0, posY + 0, posX + width, posY + height, 0x77000000);
        Gui.drawRect(posX + 1, posY + 1, posX + width - 1, posY + height - 1, 0x77222222);
        Gui.drawRect(posX + 1, posY + 1 - barHeight + maxBarHeight, posX + width - 1, posY + 1 + maxBarHeight, color2);
        Gui.drawRect(posX + 1, posY + 1 - barHeight + maxBarHeight, posX + (width / 2), posY + 1 + maxBarHeight, color);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderGameOverlayEvent.Pre event)
    {
        Bleedable bleedable = (Bleedable) Game.minecraft().player.getCapability(Provider.CAPABILITY, null);

        // if (!Game.minecraft().player.capabilities.isCreativeMode && (System.currentTimeMillis() -
        // lastBloodBarActivationTime <= bloodBarDisplayPeriod ||
        // Game.minecraft().player.isPotionActive(PotionHandler.LIGHT_BLEED) ||
        // Game.minecraft().player.isPotionActive(PotionHandler.HEAVY_BLEED)))
        {
            if (bleedable.getMaxBloodCount() > 0 && bleedable.getBloodCount() > 0)
            {
                int xSkip = 0;
                int skipVal = 6;

                int width = 5;
                int height = 30;
                int posX = (int) (event.getResolution().getScaledWidth() / 2) + 93;
                int posY = event.getResolution().getScaledHeight() - height;
                drawVerticalProgressBar(bleedable.getBloodCount(), bleedable.getMaxBloodCount(), posX, posY, width, height, 0x77FF0000, 0x77880000);
                OpenGL.enableBlend();
                Draw.drawResource(Resources.BLOOD_DROP, posX - 1, posY - 9, 8, 8);

                // width = 5;
                // height = 39;
                // posX = (int) (event.getResolution().getScaledWidth() / 2) + 93 + (xSkip += skipVal);
                // posY = event.getResolution().getScaledHeight() - height;
                // drawVerticalProgressBar(bleedable.getHeartRate(), 130, posX, posY, width, height, 0x77AAFF00,
                // 0xFF448800);
            }

            OpenGL.color(1F, 1F, 1F, 1F);
            OpenGL.enableBlend();
            Draw.bindTexture(Gui.ICONS);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void render(RenderWorldLastEvent event)
    {
        if (Game.minecraft().world != null)
        {
            if (Game.minecraft().player.isPotionActive(PotionHandler.LIGHTHEADED))
            {
                Shaders.enableLightheaded(Game.minecraft().entityRenderer);
            }
            else if (Game.minecraft().entityRenderer.getShaderGroup() != null && Game.minecraft().entityRenderer.getShaderGroup().getShaderGroupName().equalsIgnoreCase("lbm:shaders/post/lightheaded.json"))
            {
                Shaders.disable();
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.WorldTickEvent event)
    {
        if (event.world != null)
        {
            for (int x = 0; x < event.world.loadedEntityList.size(); ++x)
            {
                Entity entity = (Entity) event.world.loadedEntityList.get(x);

                if (entity != null && entity instanceof EntityLivingBase)
                {
                    EntityLivingBase living = (EntityLivingBase) entity;

                    if (!living.isEntityUndead())
                    {
                        Bleedable bleedable = (Bleedable) living.getCapability(Provider.CAPABILITY, null);

                        bleedable.onTick(living, bleedable);
                    }
                }
            }

            // for (int x = 0; x < event.world.loadedEntityList.size(); ++x)
            // {
            // Entity entity = (Entity) event.world.loadedEntityList.get(x);
            //
            // if (EntityZombie.class.isInstance(entity) || EntitySlime.class.isInstance(entity))
            // {
            // entity.setDead();
            // entity = null;
            // }
            // }
        }
    }

    @SubscribeEvent
    public void onEntityHitByProjectile(net.minecraftforge.event.entity.ProjectileImpactEvent event)
    {
        ;
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent event)
    {
        // Bleedable bleedable = (Bleedable) event.getEntityLiving().getCapability(Provider.CAPABILITY,
        // null);
        // bleedable.setBloodCount(bleedable.getMaxBloodCount());
        // bleedable.syncClients(event.getEntityLiving());
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        Bleedable bleedable = (Bleedable) event.getEntityLiving().getCapability(Provider.CAPABILITY, null);

        float mult = event.getAmount() / 3;

        if (event.getSource().isExplosion())
        {
            mult = event.getAmount();
        }

        if (event.getAmount() >= 7 || event.getSource().isExplosion() || event.getSource().isProjectile())
        {
        	if (event.getEntityLiving().getRNG().nextInt(LBM.settings().getHeavyBleedChance()) == 0)
        	{
	            if (event.getEntityLiving().isPotionActive(PotionHandler.HEAVY_BLEED))
	            {
	                PotionEffect effect = event.getEntityLiving().getActivePotionEffect(PotionHandler.HEAVY_BLEED);
	                event.getEntityLiving().addPotionEffect(new PotionEffect(PotionHandler.HEAVY_BLEED, effect.getDuration() + (20 * 30)));
	            }
	            else
	            {
	                event.getEntityLiving().addPotionEffect(new PotionEffect(PotionHandler.HEAVY_BLEED, 20 * 60));
	            }
        	}
        }
        else if (event.getAmount() >= 4)
        {
        	if (event.getEntityLiving().getRNG().nextInt(LBM.settings().getLightBleedChance()) == 0)
        	{
	            if (event.getEntityLiving().isPotionActive(PotionHandler.LIGHT_BLEED))
	            {
	                PotionEffect effect = event.getEntityLiving().getActivePotionEffect(PotionHandler.LIGHT_BLEED);
	                event.getEntityLiving().addPotionEffect(new PotionEffect(PotionHandler.LIGHT_BLEED, effect.getDuration() + (20 * 30)));
	            }
	            else
	            {
	                event.getEntityLiving().addPotionEffect(new PotionEffect(PotionHandler.LIGHT_BLEED, 20 * 60));
	            }
        	}
        }

        if (event.getAmount() >= 2)
        {
            bleedable.setHeartRate(130 + event.getEntityLiving().getRNG().nextInt(20));
            bleedable.setBloodCount(bleedable.getBloodCount() - (int) (event.getAmount() * (LBM.settings().getImpactBloodLossMultiplier() * 2)));

            LBM.network().sendToAll(new PacketBleed(event.getEntityLiving(), 0.2F * mult, (int) Math.floor(LBM.settings().getBloodDetailLevel() * (event.getAmount() * LBM.settings().getImpactBloodLossMultiplier()))));
            
//            if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
//            {
//                BloodHandler.bleed(event.getEntityLiving(), 0.2F * mult, (int) Math.floor(LBM.settings().getBloodDetailLevel() * (event.getAmount() * LBM.settings().getImpactBloodLossMultiplier())));
//            }
        }
    }
}
