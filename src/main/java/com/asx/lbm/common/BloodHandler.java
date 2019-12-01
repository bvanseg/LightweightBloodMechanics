package com.asx.lbm.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.asx.lbm.LBM;
import com.asx.lbm.api.IBleedable;
import com.asx.lbm.api.IBleedableException;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.lbm.common.packets.client.PacketBleedEffect;
import com.asx.mdx.core.mods.IInitEvent;
import com.asx.mdx.lib.client.entityfx.EntityBloodFX;
import com.asx.mdx.lib.util.Game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("all")
public class BloodHandler implements IInitEvent
{
    private static ArrayList<Class<? extends Entity>>   EXCEPTIONS           = new ArrayList<Class<? extends Entity>>();
    private static ArrayList<Class<? extends Entity>>   DEFAULT_EXCEPTIONS   = new ArrayList<Class<? extends Entity>>();
    private static Map<Class<? extends Entity>, Object> BLOOD_COLORS = new HashMap<Class<? extends Entity>, Object>();
    private static Map<Class<? extends Entity>, Object> DEFAULT_BLOOD_COLORS = new HashMap<Class<? extends Entity>, Object>();
    private static Map<Class<? extends Entity>, Object> BLOOD_PHOSPHOR = new HashMap<Class<? extends Entity>, Object>();
    private static Map<Class<? extends Entity>, Object> DEFAULT_BLOOD_PHOSPHOR = new HashMap<Class<? extends Entity>, Object>();

    static
    {
        DEFAULT_EXCEPTIONS.add(EntityIronGolem.class);
        DEFAULT_EXCEPTIONS.add(EntitySkeleton.class);
        DEFAULT_EXCEPTIONS.add(EntitySkeletonHorse.class);
        DEFAULT_EXCEPTIONS.add(EntityEndermite.class);
    }

    static
    {
        DEFAULT_BLOOD_COLORS.put(EntityCreeper.class, 0x66244503);
        DEFAULT_BLOOD_COLORS.put(EntitySlime.class, 0xAA75974B);
        DEFAULT_BLOOD_COLORS.put(EntityZombie.class, 0xAA7a2906);
        DEFAULT_BLOOD_COLORS.put(EntitySquid.class, 0xAA0000FF);
        DEFAULT_BLOOD_COLORS.put(EntityEnderman.class, 0xAA8800FF);
    }
    
    static
    {
        DEFAULT_BLOOD_PHOSPHOR.put(EntityEnderman.class, true);
    }

    public BloodHandler()
    {
        super();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        this.applyConfigExceptions();
        this.applyConfigExceptionPardons();
        this.applyConfigBloodColors();
        this.applyConfigBloodPhosphor();
    }

    public static Map<Class<? extends Entity>, ?> getDefaultEntityPhosphor()
    {
        return DEFAULT_BLOOD_PHOSPHOR;
    }

    private void applyConfigBloodPhosphor()
    {
        Map<Class<? extends Entity>, Object> mappings = LBM.settings().getEntityBloodPhosphor();

        for (Class<? extends Entity> clz : mappings.keySet())
        {
            Object value = mappings.get(clz);
            
            if (value != null)
            {
                boolean phosphor = Boolean.parseBoolean(value.toString());
                
                removeBloodPhosphorMapping(clz);
                addBloodPhosphorMapping(clz, phosphor);
            }
        }
    }

    public static Map<Class<? extends Entity>, Object> getDefaultBloodColors()
    {
        return DEFAULT_BLOOD_COLORS;
    }

    private void applyConfigBloodColors()
    {
        Map<Class<? extends Entity>, Object> mappings = LBM.settings().getEntityBloodColors();

        for (Class<? extends Entity> clz : mappings.keySet())
        {
            Object value = mappings.get(clz);
            
            if (value != null)
            {
                int color = Integer.parseInt(value.toString());
                
                removeBloodColorMapping(clz);
                addBloodColorMapping(clz, color);
            }
        }
    }

    private void applyConfigExceptions()
    {
        ArrayList<Class<? extends Entity>> configExceptions = LBM.settings().getExceptionsList();

        for (Class<? extends Entity> clz : configExceptions)
        {
            this.addException((Class<? extends EntityLivingBase>) clz);
        }
    }

    private void applyConfigExceptionPardons()
    {
        ArrayList<Class<? extends Entity>> pardons = LBM.settings().getExceptionPardonList();

        for (Class<? extends Entity> clz : pardons)
        {
            this.removeException((Class<? extends EntityLivingBase>) clz);
        }
    }

    public static ArrayList<Class<? extends Entity>> getDefaultExceptions()
    {
        return DEFAULT_EXCEPTIONS;
    }

    public static ArrayList<Class<? extends Entity>> getExceptions()
    {
        return EXCEPTIONS;
    }

    public static void addBloodPhosphorMapping(Class<? extends Entity> clz, boolean phosphor)
    {
        if (BLOOD_PHOSPHOR.get(clz) == null)
        {
            BLOOD_PHOSPHOR.put(clz, phosphor);
        }
    }

    public static void removeBloodPhosphorMapping(Class<? extends Entity> clz)
    {
        if (BLOOD_PHOSPHOR.get(clz) != null)
        {
            BLOOD_PHOSPHOR.remove(clz);
            
            if (BLOOD_PHOSPHOR.get(clz) != null)
            {
                removeBloodPhosphorMapping(clz);
            }
        }
    }

    public static void addBloodColorMapping(Class<? extends Entity> clz, int color)
    {
        if (BLOOD_COLORS.get(clz) == null)
        {
            BLOOD_COLORS.put(clz, color);
        }
    }

    public static void removeBloodColorMapping(Class<? extends Entity> clz)
    {
        if (BLOOD_COLORS.get(clz) != null)
        {
            BLOOD_COLORS.remove(clz);
            
            if (BLOOD_COLORS.get(clz) != null)
            {
                removeBloodColorMapping(clz);
            }
        }
    }

    public static void addException(Class<? extends EntityLivingBase> exception)
    {
        if (EXCEPTIONS.contains(exception))
        {
            return;
        }

        EXCEPTIONS.add(exception);
    }

    public static void removeException(Class<? extends EntityLivingBase> exception)
    {
        if (EXCEPTIONS.contains(exception))
        {
            EXCEPTIONS.remove(exception);
        }
    }

    public static boolean shouldBleed(EntityLivingBase living)
    {
        if (!LBM.settings().isBloodEnabled())
        {
            return false;
        }

        if (living instanceof IBleedableException)
            return false;

        if (EXCEPTIONS.contains(living.getClass()))
            return false;

        for (Class<? extends Entity> exception : EXCEPTIONS)
        {
            if (exception.isInstance(living))
            {
                return false;
            }
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void bleedEffect(EntityLivingBase living, float spread, int amount)
    {
        boolean bleed = shouldBleed(living);

        if (bleed)
        {
            int color = 0xAA610000;
            boolean glow = false;

            if (living instanceof IBleedable)
            {
                IBleedable effects = (IBleedable) living;

                color = effects.getBloodColor();
                glow = effects.doesBloodGlow();
            }
            
            for (Class<? extends Entity> eClz : BLOOD_COLORS.keySet())
            {
                if (eClz.isInstance(living))
                {
                    color = Integer.valueOf(BLOOD_COLORS.get(eClz).toString());
                }
            }
            
            for (Class<? extends Entity> eClz : BLOOD_PHOSPHOR.keySet())
            {
                if (eClz.isInstance(living))
                {
                    glow = Boolean.valueOf(BLOOD_PHOSPHOR.get(eClz).toString());
                }
            }

            for (int i = amount; i > 0; i--)
            {
                double pX = living.posX + (living.getRNG().nextDouble() * spread) - (living.getRNG().nextDouble() * spread);
                double pY = living.posY + (living.getRNG().nextDouble() * spread) - (living.getRNG().nextDouble() * spread);
                double pZ = living.posZ + (living.getRNG().nextDouble() * spread) - (living.getRNG().nextDouble() * spread);

                Game.minecraft().effectRenderer.addEffect(new EntityBloodFX(living.world, pX, pY, pZ, color, 30 * 20, glow));
            }
        }
    }
    
    public static void applyBleedWithChance(EntityLivingBase living, Potion potion, int chance, boolean override)
    {
        if (living.getRNG().nextInt(chance) == 0 || override)
        {
            applyBleed(living, potion);
        }
    }
    
    public static void applyBleed(EntityLivingBase living, Potion potion)
    {
        PotionEffect active = living.getActivePotionEffect(potion);
        int duration = active != null ? active.getDuration() + (20 * 30) : 20 * 60;
        living.addPotionEffect(new PotionEffect(potion, duration));
    }
    
    public static void handleInjury(EntityLivingBase living, float damage, boolean explosion, boolean projectile)
    {
        if (damage >= 7 || explosion)
        {
            applyBleedWithChance(living, PotionHandler.HEAVY_BLEED, LBM.settings().getHeavyBleedChance(), explosion);
        }
        else if (damage >= 4 || projectile)
        {
            applyBleedWithChance(living, PotionHandler.LIGHT_BLEED, LBM.settings().getLightBleedChance(), projectile);
        }
    }
}
