package com.asx.lbm.common.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class PotionLightheaded extends Potion
{
    public PotionLightheaded()
    {
        super(true, 0xFFAA0000);
        setPotionName("effect.confusion");
        setRegistryName("effectConfusion");
        setIconIndex(4, 2);
    }

    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier)
    {
        super.performEffect(entityLivingBaseIn, amplifier);
    }
}
