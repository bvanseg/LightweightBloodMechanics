package com.asx.lbm.common.potions;

import java.util.ArrayList;
import java.util.List;

import com.asx.lbm.client.Resources;
import com.asx.lbm.common.ItemHandler;
import com.asx.mdx.lib.client.util.Draw;
import com.asx.mdx.lib.client.util.OpenGL;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionHeavyBleed extends Potion
{
    public PotionHeavyBleed()
    {
        super(true, 0xFFAA0000);
        setPotionName("effect.heavybleed");
        setRegistryName("effectHeavyBleed");
        setIconIndex(0, 0);
    }
    
    @Override
    public void affectEntity(Entity source, Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health)
    {
        super.affectEntity(source, indirectSource, entityLivingBaseIn, amplifier, health);
    }
    
    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier)
    {
        super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    }
    
    @Override
    public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier)
    {   
        super.performEffect(entityLivingBaseIn, amplifier);
    }

    @Override
    public List<ItemStack> getCurativeItems()
    {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ItemHandler.GAUZE));
        return list;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasStatusIcon()
    {
        return false;
    }

    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
    {
        OpenGL.pushMatrix();
        OpenGL.enableBlend();
        Draw.bindTexture(Resources.BLOOD_DROP);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
        OpenGL.popMatrix();
    }

    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        OpenGL.pushMatrix();
        OpenGL.enableBlend();
        Draw.bindTexture(Resources.BLOOD_DROP);
        Gui.drawModalRectWithCustomSizedTexture(x + 8, y + 7, 0, 0, 18, 18, 18, 18);
        OpenGL.popMatrix();
    }
}
