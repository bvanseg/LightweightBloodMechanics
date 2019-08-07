package com.asx.lbm;

import com.asx.lbm.common.Events;
import com.asx.mdx.core.mods.IInitEvent;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModEventHandler implements IInitEvent
{
    @Override
    public void init(FMLInitializationEvent event)
    {
        new Events();
    }
}
