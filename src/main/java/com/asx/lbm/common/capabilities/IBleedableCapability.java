package com.asx.lbm.common.capabilities;

import java.util.concurrent.Callable;

import com.asx.lbm.LBM;
import com.asx.lbm.common.BloodHandler;
import com.asx.lbm.common.DamageSources;
import com.asx.lbm.common.Events;
import com.asx.lbm.common.PotionHandler;
import com.asx.lbm.common.packets.client.PacketBleed;
import com.asx.lbm.common.packets.client.PacketBleedableClientSync;
import com.asx.lbm.common.packets.server.PacketBleedableServerSync;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IBleedableCapability
{
    public int getHeartRate();

    public void setHeartRate(int rate);

    public int getMaxBloodCount();

    public int getBloodCount();

    public void setBloodCount(int count);

    public double getBodyMass();

    public void setBodyMass(double mass);

    public class Provider implements ICapabilitySerializable<NBTBase>
    {
        @CapabilityInject(IBleedableCapability.class)
        public static final Capability<IBleedableCapability> CAPABILITY = null;

        private IBleedableCapability                         instance   = CAPABILITY.getDefaultInstance();

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            return hasCapability(capability, facing) ? CAPABILITY.<T>cast(this.instance) : null;
        }

        @Override
        public NBTBase serializeNBT()
        {
            return CAPABILITY.getStorage().writeNBT(CAPABILITY, this.instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt)
        {
            CAPABILITY.getStorage().readNBT(CAPABILITY, this.instance, null, nbt);
        }
    }

    public default void heal(EntityLivingBase living)
    {
        living.setHealth(living.getMaxHealth());

        if (!living.world.isRemote)
        {
            living.curePotionEffects(new ItemStack(Items.MILK_BUCKET, 1));
            living.getActivePotionEffects().clear();
        }

        if (living instanceof EntityPlayer && living.world.isRemote)
        {
            EntityPlayer player = (EntityPlayer) living;
            player.getFoodStats().setFoodLevel(20);
        }
    }

    public static class Bleedable implements IBleedableCapability, IStorage<IBleedableCapability>
    {
        public static class Factory implements Callable<IBleedableCapability>
        {
            @Override
            public IBleedableCapability call() throws Exception
            {
                return new Bleedable();
            }
        }

        private EntityLivingBase living;
        private int              heartRate;
        private int              bloodCount;
        private double           bodyMass;

        public Bleedable()
        {
            super();
        }

        public void initialize(EntityLivingBase living)
        {
            this.living = living;
            this.bodyMass = (living.height * living.width) * 100;
            this.heartRate = 60;
            this.bloodCount = getMaxBloodCount();
        }

        public EntityLivingBase getLiving()
        {
            return living;
        }

        @Override
        public double getBodyMass()
        {
            return bodyMass;
        }

        public void setBodyMass(double bodyMass)
        {
            this.bodyMass = bodyMass;
        }

        @Override
        public int getMaxBloodCount()
        {
            return (int) Math.round(this.bodyMass * 2);
        }

        @Override
        public int getBloodCount()
        {
            return bloodCount;
        }

        @Override
        public void setBloodCount(int bloodCount)
        {
            if (bloodCount != this.bloodCount)
            {
                Events.activateBloodBar();
            }

            this.bloodCount = bloodCount;
        }

        /**
         * Gets the heart rate of this bleedable, which is measured in BPM.
         */
        @Override
        public int getHeartRate()
        {
            return this.heartRate;
        }

        @Override
        public void setHeartRate(int rate)
        {
            this.heartRate = rate;
        }

        public void onEntityHurt()
        {

        }

        public void onTick(EntityLivingBase living, IBleedableCapability bleedable)
        {
            World world = living.world;

            if (!world.isRemote && living.ticksExisted % 20 == 0)
            {
                this.syncClients(living);
            }

            int bpm = bleedable.getHeartRate();

            if (!world.isRemote)
            {
                int newRate = bpm;

                if (living.isSprinting())
                {
                    newRate = 130 + living.getRNG().nextInt(20);
                }
                else if (living.motionX + living.motionZ > 0)
                {
                    newRate = 70 + living.getRNG().nextInt(10);
                }
                else
                {
                    newRate = 60 + living.getRNG().nextInt(10);
                }

                if (newRate < bpm)
                {
                    newRate = bpm - 1;
                }

                bleedable.setHeartRate(newRate);
            }

            boolean doNotEffectEntity = false;

            if (living instanceof EntityPlayer)
            {
                EntityPlayer player = (EntityPlayer) living;

                if (player.capabilities.isCreativeMode)
                {
                    doNotEffectEntity = true;
                }
            }

            if (!doNotEffectEntity)
            {
                if (bleedable.getMaxBloodCount() > 0)
                {
                    if (100 * bleedable.getBloodCount() / bleedable.getMaxBloodCount() <= 40)
                    {
                        if (!living.isPotionActive(PotionHandler.LIGHTHEADED))
                        {
                            living.addPotionEffect(new PotionEffect(PotionHandler.LIGHTHEADED, 20 * 30));
                        }
                    }

                    if (bleedable.getBloodCount() <= 0)
                    {
                        living.attackEntityFrom(DamageSources.BLOOD_LOSS, living.getHealth());
                    }

                    if (bleedable.getBloodCount() * 100 / bleedable.getMaxBloodCount() <= 20)
                    {
                        if (!living.isPotionActive(MobEffects.BLINDNESS))
                        {
                            living.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 20 * 30));
                        }
                    }

                    if (living.isPotionActive(PotionHandler.HEAVY_BLEED))
                    {
                        int heavyBleedInterval = LBM.settings().getHeavyBleedInterval() + (living.getRNG().nextInt(3) * LBM.settings().getHeavyBleedInterval());

                        if (living.ticksExisted % heavyBleedInterval == 0)
                        {
                            bleedable.setBloodCount(bleedable.getBloodCount() - (int) (2 + (living.getRNG().nextInt(2)) * (LBM.settings().getHeavyBloodLossMultiplier() + bpm > 100 ? 1 : 0)));

                            if (!world.isRemote)
                            {
                                LBM.network().sendToAll(new PacketBleed(living, 0.1F + ((float) LBM.settings().getHeavyBleedSpread() + living.getRNG().nextFloat()), (int) Math.floor(LBM.settings().getBloodDetailLevel() / (bpm > 100 ? 1 : 3))));
                            }
                        }
                    }
                    else if (living.isPotionActive(PotionHandler.LIGHT_BLEED))
                    {
                        int lightBleedInterval = LBM.settings().getLightBleedInterval();

                        if (living.ticksExisted % lightBleedInterval == 0)
                        {
                            bleedable.setBloodCount(bleedable.getBloodCount() - LBM.settings().getLightBloodLossMultiplier());
                        }

                        if (living.ticksExisted % LBM.settings().getLightBleedInterval() + (living.getRNG().nextInt(2) * (LBM.settings().getLightBleedInterval())) == 0)
                        {
                            if (!world.isRemote)
                            {
                                LBM.network().sendToAll(new PacketBleed(living, (float) LBM.settings().getLightBleedSpread(), 1 + (living.getRNG().nextInt(2) - 1)));
                            }
                        }
                    }
                    else
                    {
                        if (living.getHealth() >= living.getMaxHealth())
                        {
                            if (living.ticksExisted % 20 * 4 == 0)
                            {
                                if (bleedable.getBloodCount() < bleedable.getMaxBloodCount())
                                {
                                    bleedable.setBloodCount(bleedable.getBloodCount() + 1);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public NBTBase writeNBT(Capability<IBleedableCapability> capability, IBleedableCapability instance, EnumFacing side)
        {
            NBTTagCompound tag = new NBTTagCompound();

            tag.setInteger("BPM", instance.getHeartRate());
            tag.setInteger("BloodCount", instance.getBloodCount());
            tag.setDouble("BodyMass", instance.getBodyMass());

            return tag;
        }

        @Override
        public void readNBT(Capability<IBleedableCapability> capability, IBleedableCapability instance, EnumFacing side, NBTBase nbt)
        {
            if (nbt instanceof NBTTagCompound)
            {
                NBTTagCompound tag = (NBTTagCompound) nbt;

                instance.setHeartRate(tag.getInteger("BPM"));
                instance.setBloodCount(tag.getInteger("BloodCount"));
                instance.setBodyMass(tag.getDouble("BodyMass"));
            }
        }

        public void syncServer(EntityLivingBase living2)
        {
            LBM.network().sendToServer(new PacketBleedableServerSync(living2.getEntityId(), (NBTTagCompound) Provider.CAPABILITY.getStorage().writeNBT(Provider.CAPABILITY, this, null)));
        }

        public void syncClients(EntityLivingBase living2)
        {
            LBM.network().sendToAll(new PacketBleedableClientSync(living2.getEntityId(), (NBTTagCompound) Provider.CAPABILITY.getStorage().writeNBT(Provider.CAPABILITY, this, null)));
        }
    }
}
