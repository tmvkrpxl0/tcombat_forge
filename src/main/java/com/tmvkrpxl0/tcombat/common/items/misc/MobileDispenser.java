package com.tmvkrpxl0.tcombat.common.items.misc;

import com.tmvkrpxl0.tcombat.common.capability.ItemInventoryCapabilityProvider;
import com.tmvkrpxl0.tcombat.common.container.MobileDispenserGui;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Random;

public class MobileDispenser extends Item {
    private static final Random RNG = new Random();
    public static Map<Item, IDispenseItemBehavior> DISPENSE_REGISTRY;
    private static Method getProjectile;
    private static Method getProjectilePower;
    private static Method getProjectileUncertainty;

    public MobileDispenser(Item.Properties properties){
        super(properties);
        try{
            Field f = DispenserBlock.class.getDeclaredField("DISPENSER_REGISTRY");
            f.setAccessible(true);
            DISPENSE_REGISTRY = (Map<Item, IDispenseItemBehavior>) f.get(null);
            getProjectile = ProjectileDispenseBehavior.class.getDeclaredMethod("getProjectile", World.class, IPosition.class, ItemStack.class);
            getProjectile.setAccessible(true);
            getProjectilePower = ProjectileDispenseBehavior.class.getDeclaredMethod("getPower");
            getProjectilePower.setAccessible(true);
            getProjectileUncertainty = ProjectileDispenseBehavior.class.getDeclaredMethod("getUncertainty");
            getProjectileUncertainty.setAccessible(true);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        if (!worldIn.isClientSide) {
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) playerIn;
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (playerEntity.isShiftKeyDown()) {
                playerEntity.openMenu(new MobileDispenserGui(stack));
            } else {
                Vector3d posVec = playerEntity.getEyePosition(1F).add(playerEntity.getLookAngle().multiply(2, 2, 2));
                BlockPos pos = new BlockPos(posVec);
                LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if (cap.resolve().isPresent()) {
                    ItemStackHandler handler = (ItemStackHandler) cap.resolve().get();
                    int i = -1;
                    int j = 1;
                    for (int k = 0; k < 9; ++k) {
                        if (!handler.getStackInSlot(k).isEmpty() && RNG.nextInt(j++) == 0) {
                            i = k;
                        }
                    }
                    if (i < 0) {
                        worldIn.levelEvent(1001, pos, 0);
                    } else {
                        ItemStack itemstack = handler.getStackInSlot(i);
                        Item item = itemstack.getItem();
                        IDispenseItemBehavior iDispenseItemBehavior = DISPENSE_REGISTRY.get(item);
                        if (iDispenseItemBehavior instanceof ProjectileDispenseBehavior) {
                            ProjectileDispenseBehavior projectileDispenseBehavior = (ProjectileDispenseBehavior) iDispenseItemBehavior;
                            try {
                                ProjectileEntity projectileEntity = (ProjectileEntity) getProjectile.invoke(projectileDispenseBehavior, worldIn, posVec, itemstack);
                                float projectileUncertainty = (float) getProjectileUncertainty.invoke(projectileDispenseBehavior);
                                float projectilePower = (float) getProjectilePower.invoke(projectileDispenseBehavior);
                                Vector3d lookAngle = playerEntity.getLookAngle();
                                projectileEntity.shoot(lookAngle.x, (float) lookAngle.y + 0.1F, lookAngle.z, projectilePower, projectileUncertainty);
                                projectileEntity.setOwner(playerEntity);
                                worldIn.addFreshEntity(projectileEntity);
                                itemstack.shrink(1);
                                handler.setStackInSlot(i, itemstack);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) {
                                ProjectileEntity projectileEntity = Util.make(new PotionEntity(worldIn, posVec.x, posVec.y, posVec.z),
                                        (p_218411_1_) -> p_218411_1_.setItem(itemstack));
                                float projectileUncertainty = (float) (6.0 * 0.5);
                                float projectilePower = (float) (1.1 * 1.25);
                                Vector3d lookAngle = playerEntity.getLookAngle();
                                projectileEntity.shoot(lookAngle.x, (float) lookAngle.y + 0.1F, lookAngle.z, projectilePower, projectileUncertainty);
                                projectileEntity.setOwner(playerEntity);
                                worldIn.addFreshEntity(projectileEntity);
                                itemstack.shrink(1);
                                handler.setStackInSlot(i, itemstack);
                            }
                        }
                    }
                }
            }
        }
            return super.use(worldIn, playerIn, handIn);
    }

    @Nullable
    @Override
    public ICapabilitySerializable<? extends INBT> initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemInventoryCapabilityProvider();
    }
}
