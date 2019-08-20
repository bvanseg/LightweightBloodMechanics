package com.asx.lbm;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.asx.lbm.common.BloodHandler;
import com.asx.mdx.config.ConfigSetting;
import com.asx.mdx.config.ConfigSettingBoolean;
import com.asx.mdx.config.ConfigSettingDouble;
import com.asx.mdx.config.ConfigSettingEntitySettingList;
import com.asx.mdx.config.ConfigSettingEntityTypeList;
import com.asx.mdx.config.ConfigSettingInteger;
import com.asx.mdx.config.IFlexibleConfiguration;
import com.asx.mdx.core.mods.IPreInitEvent;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Settings implements IPreInitEvent, IFlexibleConfiguration
{
    public static final Settings           instance          = new Settings();
    
    private final ArrayList<ConfigSetting> allSettings       = new ArrayList<ConfigSetting>();
    
    private Configuration                  configuration;
    private final String                   CATEGORY_OTHER    = "general";
    private ConfigSetting                  bloodEnabled;
    private ConfigSetting                  phosphorBlurring;
    private ConfigSetting                  bloodDetailLevel;
    private ConfigSetting                  lightBleedChance;
    private ConfigSetting                  lightBleedInterval;
    private ConfigSetting                  lightBleedMultiplier;
    private ConfigSetting                  heavyBleedChance;
    private ConfigSetting                  heavyBleedInterval;
    private ConfigSetting                  heavyBleedMultiplier;
    private ConfigSetting                  lightBleedSpread;
    private ConfigSetting                  heavyBleedSpread;
    private ConfigSetting                  impactBloodLossMultiplier;
    private ConfigSetting                  exceptionsList;
    private ConfigSetting                  exceptionPardonList;
    private ConfigSetting                  entityBloodColors;
    private ConfigSetting                  entityBloodPhosphor;
    
    @Override
    public ArrayList<ConfigSetting> allSettings()
    {
        return allSettings;
    }
    
    @Override
    public void saveSettings()
    {
        this.configuration.save();
    }

    @EventHandler
    public void pre(FMLPreInitializationEvent evt)
    {
        configuration = new Configuration(new File(evt.getModConfigurationDirectory(), "lbm.config"));
        try
        {
            configuration.load();

            phosphorBlurring = new ConfigSettingBoolean(this, configuration.get(CATEGORY_OTHER, "phosphor_blurring", true, "If disabled, the dragging blur shader effect will not be active."));
            bloodEnabled = new ConfigSettingBoolean(this, configuration.get(CATEGORY_OTHER, "blood_enabled", true, "If disabled, no blood will be dropped."));
            bloodDetailLevel = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "blood_detail", 16, "Default: 16. Blood multiplier level. Higher levels = more blood."));
            impactBloodLossMultiplier = new ConfigSettingDouble(this, configuration.get(CATEGORY_OTHER, "impact_blood_loss_multiplier", 2, "Default: 2. Impact blood loss multiplier. Lower = less loss"));
            lightBleedChance = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "light_bleed_chance", 3, "Default: 3. Light bleed chance. 1 out of X chance of bleeding."));
            lightBleedInterval = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "light_bleed_interval", 40, "Default: 40. Light bleed effect interval. (20 = 1 second, 40 = 2 seconds, etc) Lower = more often"));
            lightBleedMultiplier = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "light_bleed_multiplier", 1, "Default: 1. Light bleed effect multiplier. Higher = more loss"));
            heavyBleedChance = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "heavy_bleed_chance", 2, "Default: 2. Heavy bleed chance. 1 out of X chance of bleeding."));
            heavyBleedInterval = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "heavy_bleed_interval", 15, "Default: 15. Heavy bleed effect interval. (20 = 1 second, 40 = 2 seconds, etc) Lower = more often"));
            heavyBleedMultiplier = new ConfigSettingInteger(this, configuration.get(CATEGORY_OTHER, "heavy_bleed_multiplier", 1, "Default: 1. Heavy bleed effect multiplier. Higher = more loss"));
            lightBleedSpread = new ConfigSettingDouble(this, configuration.get(CATEGORY_OTHER, "light_bleed_spread", 0.6, "Default: 0.6. Light bleed effect interval. Lower = more often"));
            heavyBleedSpread = new ConfigSettingDouble(this, configuration.get(CATEGORY_OTHER, "heavy_bleed_spread", 0.5, "Default: 0.5. Heavy bleed effect interval. Higher = more spread"));
            exceptionsList = new ConfigSettingEntityTypeList(this, configuration.get(CATEGORY_OTHER, "bleeding_exceptions", ConfigSettingEntityTypeList.entityRegistryListForConfig(BloodHandler.getDefaultExceptions()), "Exception list for entities that should not bleed.")).setRequiresRestart();
            exceptionPardonList = new ConfigSettingEntityTypeList(this, configuration.get(CATEGORY_OTHER, "bleeding_exception_pardons", ConfigSettingEntityTypeList.entityRegistryListForConfig(new ArrayList<Class <? extends Entity>>()), "Pardon list for entities that should bleed but are internally configured not to bleed. Overrides the main exception list")).setRequiresRestart();
            entityBloodColors = new ConfigSettingEntitySettingList<Integer>(this, configuration.get(CATEGORY_OTHER, "entity_blood_colors", ConfigSettingEntitySettingList.entitySettingListForConfig(BloodHandler.getDefaultBloodColors()), "Blood color list for each entity type.")).setRequiresRestart();
            entityBloodPhosphor = new ConfigSettingEntitySettingList<Boolean>(this, configuration.get(CATEGORY_OTHER, "entity_blood_phoshphor", ConfigSettingEntitySettingList.entitySettingListForConfig(BloodHandler.getDefaultEntityPhosphor()), "Blood phosphorescent lighting effect list for each entity type.")).setRequiresRestart();
        }
        finally
        {
            configuration.save();
        }
    }

    public Configuration getConfig()
    {
        return configuration;
    }

    public boolean isBloodEnabled()
    {
        return (Boolean) this.bloodEnabled.value();
    }
    
    public boolean isPhosphorBlurringEnabled()
    {
        return (Boolean) phosphorBlurring.value();
    }
    
    public int getBloodDetailLevel()
    {
        return (Integer) bloodDetailLevel.value();
    }

    public int getHeavyBleedInterval()
    {
        return (Integer) heavyBleedInterval.value();
    }

    public int getLightBleedInterval()
    {
        return (Integer) lightBleedInterval.value();
    }
    
    public double getHeavyBleedSpread()
    {
        return (Double) heavyBleedSpread.value();
    }
    
    public double getLightBleedSpread()
    {
        return (Double) lightBleedSpread.value();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Class<? extends Entity>> getExceptionsList()
    {
        return (ArrayList<Class<? extends Entity>>) exceptionsList.value();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<Class<? extends Entity>> getExceptionPardonList()
    {
        return (ArrayList<Class<? extends Entity>>) exceptionPardonList.value();
    }

    @SuppressWarnings("unchecked")
    public Map<Class<? extends Entity>, Object> getEntityBloodColors()
    {
        return (Map<Class<? extends Entity>, Object>) entityBloodColors.value();
    }

    @SuppressWarnings("unchecked")
    public Map<Class<? extends Entity>, Object> getEntityBloodPhosphor()
    {
        return (Map<Class<? extends Entity>, Object>) entityBloodPhosphor.value();
    }

    public double getImpactBloodLossMultiplier()
    {
        return (Double) impactBloodLossMultiplier.value();
    }

    public int getLightBloodLossMultiplier()
    {
        return (Integer) lightBleedMultiplier.value();
    }

    public int getHeavyBloodLossMultiplier()
    {
        return (Integer) heavyBleedMultiplier.value();
    }

    public int getHeavyBleedChance()
    {
        return (Integer) heavyBleedChance.value();
    }

    public int getLightBleedChance()
    {
        return (Integer) lightBleedChance.value();
    }
}
