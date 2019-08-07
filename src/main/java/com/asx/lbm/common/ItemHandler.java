package com.asx.lbm.common;

import com.asx.lbm.LBM;
import com.asx.lbm.common.items.ItemGauze;
import com.asx.mdx.lib.client.Renderers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@ObjectHolder(LBM.Properties.ID)
public class ItemHandler
{
    public static final Item GAUZE = new ItemGauze().setRegistryName("gauze").setCreativeTab(CreativeTabs.COMBAT);

    @Mod.EventBusSubscriber(modid = LBM.Properties.ID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            for (java.lang.reflect.Field field : ItemHandler.class.getDeclaredFields())
            {
                try
                {
                    Object obj = field.get(LBM.items());

                    if (obj instanceof Item)
                    {
                        Item item = (Item) obj;
                        item.setTranslationKey(item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath());
                        event.getRegistry().register(item);
                        registerIcon(item);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private static Item registerIcon(Item item)
    {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            Renderers.registerIcon(item);
        }

        return item;
    }
}