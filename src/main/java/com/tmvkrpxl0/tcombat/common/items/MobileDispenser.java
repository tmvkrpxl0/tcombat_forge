package com.tmvkrpxl0.tcombat.common.items;

import com.tmvkrpxl0.tcombat.common.capability.ItemInventoryCapabilityProvider;
import com.tmvkrpxl0.tcombat.common.container.MobileDispenserGui;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MobileDispenser extends Item {
    private static final Random RNG = new Random();
    public static Map<Item, IDispenseItemBehavior> DISPENSE_BEHAVIOR_REGISTRY;
    private static Method getProjectileEntity;
    private static Method getProjectileVelocity;
    private static Method getProjectileInaccuracy;

    public MobileDispenser(Item.Properties properties){
        super(properties);
        try{
            Field f = DispenserBlock.class.getDeclaredField("DISPENSE_BEHAVIOR_REGISTRY");
            f.setAccessible(true);
            DISPENSE_BEHAVIOR_REGISTRY = (Map<Item, IDispenseItemBehavior>) f.get(null);
            getProjectileEntity = ProjectileDispenseBehavior.class.getDeclaredMethod("getProjectileEntity", World.class, IPosition.class, ItemStack.class);
            getProjectileEntity.setAccessible(true);
            getProjectileVelocity = ProjectileDispenseBehavior.class.getDeclaredMethod("getProjectileVelocity");
            getProjectileVelocity.setAccessible(true);
            getProjectileInaccuracy = ProjectileDispenseBehavior.class.getDeclaredMethod("getProjectileInaccuracy");
            getProjectileInaccuracy.setAccessible(true);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, @Nonnull PlayerEntity playerIn, @Nonnull Hand handIn) {
        if(!worldIn.isRemote){
            ServerPlayerEntity playerEntity = (ServerPlayerEntity) playerIn;
            ItemStack stack = playerIn.getHeldItem(handIn);
            if(playerEntity.isSneaking()){
                playerEntity.openContainer(new MobileDispenserGui(stack));
            }else{
                Vector3d posVec = playerEntity.getEyePosition(1F).add(playerEntity.getLookVec().mul(2,2,2));
                BlockPos pos = new BlockPos(posVec);
                LazyOptional<IItemHandler> cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if(cap.resolve().isPresent()){
                    ItemStackHandler handler  = (ItemStackHandler) cap.resolve().get();
                    List<ItemStack> stacks = new ArrayList<>(9);
                    for(int i = 0;i<9;i++){
                        stacks.add(handler.getStackInSlot(i));
                    }
                    int i = -1;
                    int j = 1;
                    for(int k = 0; k <stacks.size(); ++k) {
                        if (!stacks.get(k).isEmpty() && RNG.nextInt(j++) == 0) {
                            IDispenseItemBehavior tempBehaviour = DISPENSE_BEHAVIOR_REGISTRY.get(stacks.get(k).getItem());
                            if(tempBehaviour instanceof DefaultDispenseItemBehavior){
                                i = k;
                            }
                        }
                    }
                    if (i < 0) {
                        worldIn.playEvent(1001, pos, 0);
                    } else {
                        ItemStack itemstack = handler.getStackInSlot(i);
                        IDispenseItemBehavior idispenseitembehavior = DISPENSE_BEHAVIOR_REGISTRY.get(itemstack.getItem());
                        if (idispenseitembehavior != IDispenseItemBehavior.NOOP) {
                            if(idispenseitembehavior instanceof DefaultDispenseItemBehavior){
                                handler.setStackInSlot(i, dispenseProjectile(playerEntity, posVec, playerEntity.getLookVec(), itemstack, idispenseitembehavior));
                                worldIn.playEvent(1000, pos, 0);
                                worldIn.playEvent(2000, pos, Direction.getFacingDirections(playerIn)[0].getIndex());
                            }
                        }

                    }
                }

            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    @Nullable
    @Override
    public ICapabilitySerializable<? extends INBT> initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new ItemInventoryCapabilityProvider();
    }


    protected ItemStack dispenseProjectile(PlayerEntity player, IPosition position, Vector3d direction, ItemStack stack, IDispenseItemBehavior reference){
        World world = player.world;
        if(reference instanceof ProjectileDispenseBehavior){
            try{
                ProjectileDispenseBehavior behavior = (ProjectileDispenseBehavior) reference;
                ProjectileEntity projectileentity = (ProjectileEntity) getProjectileEntity.invoke(behavior, world, position, stack);
                projectileentity.setShooter(player);
                projectileentity.shoot(direction.getX(), ((float)direction.getY() + 0.1F), direction.getZ(),
                        (float)getProjectileVelocity.invoke(behavior), (float)getProjectileInaccuracy.invoke(behavior));
                world.addEntity(projectileentity);
                stack.shrink(1);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return stack;
        }else if (reference instanceof DefaultDispenseItemBehavior) {
            ItemStack itemstack = stack.split(1);
            dispenseItem(player, itemstack, 6, direction, position);
            return stack;
        }
            return stack;
    }

    public static void dispenseItem(PlayerEntity player, ItemStack stack, int speed, Vector3d direction, IPosition position) {
        World worldIn = player.world;
        double d0 = position.getX();
        double d1 = position.getY();
        double d2 = position.getZ();
        d1 = d1 - 0.15625D;
        ItemEntity itementity = new ItemEntity(worldIn, d0, d1, d2, stack);
        double d3 = worldIn.rand.nextDouble() * 0.1D + 0.2D;
        itementity.setMotion(worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + direction.getX() * d3,
                worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + (double)0.2F,
                worldIn.rand.nextGaussian() * (double)0.0075F * (double)speed + direction.getZ() * d3);
        worldIn.addEntity(itementity);
    }
}
