package com.asx.lbm.common;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.asx.lbm.LBM;
import com.asx.lbm.common.potions.PotionHeavyBleed;
import com.asx.lbm.common.potions.PotionLightBleed;
import com.asx.lbm.common.potions.PotionLightheaded;
import com.asx.mdx.core.mods.IInitEvent;

import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class PotionHandler implements IInitEvent
{
    public static final Potion LIGHTHEADED = new PotionLightheaded();
    public static final Potion LIGHT_BLEED = new PotionLightBleed();
    public static final Potion HEAVY_BLEED = new PotionHeavyBleed();

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        List<Field> fields = (Arrays.asList(PotionHandler.class.getFields()));

        try
        {
            for (Field field : fields)
            {
                Object obj = field.get(LBM.potions());

                if (Potion.class.isInstance(obj))
                {
                    Potion potion = (Potion) obj;
                    event.getRegistry().register(potion);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
