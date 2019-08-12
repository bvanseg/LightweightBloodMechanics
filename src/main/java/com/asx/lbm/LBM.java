package com.asx.lbm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.asx.lbm.LBM.Properties;
import com.asx.lbm.common.BloodHandler;
import com.asx.lbm.common.CapabilityHandler;
import com.asx.lbm.common.ItemHandler;
import com.asx.lbm.common.PotionHandler;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Properties.ID, name = Properties.NAME, version = Properties.VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:mdxlib;")
public class LBM
{
    public static final Logger LOGGER = LogManager.getLogger("LBM");

    public static class Properties
    {
        public static final String ID      = "lbm";
        public static final String NAME    = "Lightweight Blood Mechanics";
        public static final String VERSION = "1.0";
    }

    private static Settings settings;
    private static NetworkHandler network;
    private static ModEventHandler events;
    private static CapabilityHandler capabilities;
    private static PotionHandler potions;
    private static BloodHandler blood;
    private static ItemHandler items;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER.info("Lightweight Blood Mechanics Copyright (C) 2019 ASX");
        LOGGER.info("Pre initialization...");
        settings().pre(event);
        capabilities().pre(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        LOGGER.info("Initialization...");
        network().init(event);
        events().init(event);
        blood().init(event);
        potions().init(event);
    }

    @EventHandler
    public void post(FMLPostInitializationEvent event)
    {
        LOGGER.info("Post Initialization...");
    }

    public static Logger logger()
    {
        return LOGGER;
    }

    public static NetworkHandler network()
    {
        return network == null ? network = new NetworkHandler() : network;
    }

    public static ModEventHandler events()
    {
        return events == null ? events = new ModEventHandler() : events;
    }
    
    public static ItemHandler items()
    {
        return items == null ? items = new ItemHandler() : items;
    }

    public static CapabilityHandler capabilities()
    {
        return capabilities == null ? capabilities = new CapabilityHandler() : capabilities;
    }

    public static BloodHandler blood()
    {
        return blood == null ? blood = new BloodHandler() : blood;
    }

    public static PotionHandler potions()
    {
        return potions == null ? potions = new PotionHandler() : potions;
    }

    public static Settings settings()
    {
        return settings == null ? settings = new Settings() : settings;
    }
}
