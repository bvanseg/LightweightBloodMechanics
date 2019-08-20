package com.asx.lbm.common;

import com.asx.lbm.LBM;
import com.asx.lbm.client.Resources;
import com.asx.lbm.client.Shaders;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.lbm.common.packets.client.PacketBleedEffect;
import com.asx.mdx.lib.client.util.Draw;
import com.asx.mdx.lib.client.util.OpenGL;
import com.asx.mdx.lib.util.Game;

import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
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

        if (!Game.minecraft().player.capabilities.isCreativeMode && (System.currentTimeMillis() - lastBloodBarActivationTime <= bloodBarDisplayPeriod || Game.minecraft().player.isPotionActive(PotionHandler.LIGHT_BLEED) || Game.minecraft().player.isPotionActive(PotionHandler.HEAVY_BLEED)))
        {
            if (bleedable.getMaxBloodCount() > 0 && bleedable.getBloodCount() > 0)
            {
                int width = 5;
                int height = 30;
                int posX = (int) (event.getResolution().getScaledWidth() / 2) + 93;
                int posY = event.getResolution().getScaledHeight() - height;
                drawVerticalProgressBar(bleedable.getBloodCount(), bleedable.getMaxBloodCount(), posX, posY, width, height, 0x77FF0000, 0x77880000);
                OpenGL.enableBlend();
                Draw.drawResource(Resources.BLOOD_DROP, posX - 1, posY - 9, 8, 8);
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
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        float damage = event.getAmount();
        float multiplier = event.getSource().isExplosion() ? damage : damage / 3;
        EntityLivingBase living = event.getEntityLiving();
        Bleedable bleedable = (Bleedable) living.getCapability(Provider.CAPABILITY, null);
        
        if (living.isServerWorld())
        {
            bleedable.setHeartRate(130 + living.getRNG().nextInt(20));
            bleedable.setBloodCount(bleedable.getBloodCount() - (int) (damage * (LBM.settings().getImpactBloodLossMultiplier())));
            BloodHandler.handleInjury(event.getEntityLiving(), event.getAmount(), event.getSource().isExplosion(), event.getSource().isProjectile());
//            LBM.network().sendToAll(new PacketDamageEntity(event.getEntityLiving(), event.getAmount(), event.getSource()));
        }
        
        if (damage >= 2)
        {
            int detailLevel = LBM.settings().getBloodDetailLevel();
            double bloodLossMultiplier = LBM.settings().getImpactBloodLossMultiplier();
            LBM.network().sendToAll(new PacketBleedEffect(living, 0.2F * multiplier, (int) Math.floor(detailLevel * (damage * bloodLossMultiplier))));
            
//            if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
//            {
//                BloodHandler.bleedEffect(living, 0.2F * multiplier, (int) Math.floor(LBM.settings().getBloodDetailLevel() * (damage * LBM.settings().getImpactBloodLossMultiplier())));
//            }
        }
    }
}
