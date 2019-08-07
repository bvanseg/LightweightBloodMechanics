package com.asx.lbm.common.items;

import java.util.ArrayList;

import com.asx.lbm.common.ItemHandler;
import com.asx.lbm.common.PotionHandler;
import com.asx.lbm.common.capabilities.IBleedableCapability.Bleedable;
import com.asx.lbm.common.capabilities.IBleedableCapability.Provider;
import com.asx.mdx.lib.world.entity.player.inventory.Inventories;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemGauze extends Item
{
    public ItemGauze()
    {
        super();
        this.setMaxStackSize(64);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 128;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        Bleedable bleedable = (Bleedable) playerIn.getCapability(Provider.CAPABILITY, null);

        if (bleedable.getBloodCount() < bleedable.getMaxBloodCount())
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }

        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
    {
        // Bleedable bleedable = (Bleedable) entityLiving.getCapability(Provider.CAPABILITY, null);

        if (entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entityLiving;
            
            for (PotionEffect effect : new ArrayList<PotionEffect>(player.getActivePotionEffects()))
            {
                if (effect.getPotion() == PotionHandler.HEAVY_BLEED)
                {
                    int duration = effect.getDuration();
                    player.getActivePotionEffects().remove(effect);
                    player.addPotionEffect(new PotionEffect(PotionHandler.LIGHT_BLEED, duration));
                    Inventories.consumeItem(player, ItemHandler.GAUZE);
                    return super.onItemUseFinish(stack, worldIn, player);
                }

                if (effect.getPotion() == PotionHandler.LIGHT_BLEED)
                {
                    int duration = effect.getDuration();
                    player.getActivePotionEffects().remove(effect);
                    Inventories.consumeItem(player, ItemHandler.GAUZE);
                    return super.onItemUseFinish(stack, worldIn, player);
                }
            }
        }

        return super.onItemUseFinish(stack, worldIn, entityLiving);
    }
}
