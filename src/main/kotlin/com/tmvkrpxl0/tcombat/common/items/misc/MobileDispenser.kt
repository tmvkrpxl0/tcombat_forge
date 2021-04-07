package com.tmvkrpxl0.tcombat.common.items.misc

import com.tmvkrpxl0.tcombat.common.capability.ItemInventoryCapabilityProvider
import com.tmvkrpxl0.tcombat.common.container.MobileDispenserGui
import net.minecraft.block.DispenserBlock
import net.minecraft.dispenser.IDispenseItemBehavior
import net.minecraft.dispenser.IPosition
import net.minecraft.dispenser.ProjectileDispenseBehavior
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.entity.projectile.PotionEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import javax.annotation.Nonnull

class MobileDispenser(properties: Properties) : Item(properties) {
    @Nonnull
    override fun onItemRightClick(
        worldIn: World,
        @Nonnull playerIn: PlayerEntity,
        @Nonnull handIn: Hand
    ): ActionResult<ItemStack> {
        if (!worldIn.isRemote) {
            val playerEntity = playerIn as ServerPlayerEntity
            val stack = playerIn.getHeldItem(handIn)
            if (playerEntity.isSneaking) {
                playerEntity.openContainer(MobileDispenserGui(stack))
            } else {
                val posVec = playerEntity.getEyePosition(1f).add(playerEntity.lookVec.mul(2.0, 2.0, 2.0))
                val pos = BlockPos(posVec)
                val cap = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                if (cap.resolve().isPresent) {
                    val handler = cap.resolve().get() as ItemStackHandler
                    var i = -1
                    var j = 1
                    for (k in 0..8) {
                        if (!handler.getStackInSlot(k).isEmpty && RNG.nextInt(j++) == 0) {
                            i = k
                        }
                    }
                    if (i < 0) {
                        worldIn.playEvent(1001, pos, 0)
                    } else {
                        val itemStack = handler.getStackInSlot(i)
                        val item = itemStack.item
                        val iDispenseItemBehavior = DISPENSE_REGISTRY[item]
                        if (iDispenseItemBehavior is ProjectileDispenseBehavior) {
                            try {
                                val projectileEntity = getProjectileEntity.invoke(
                                    iDispenseItemBehavior,
                                    worldIn,
                                    posVec,
                                    itemStack
                                ) as ProjectileEntity
                                val projectileUncertainty =
                                    getProjectileInaccuracy.invoke(iDispenseItemBehavior) as Float
                                val projectilePower = getProjectileVelocity.invoke(iDispenseItemBehavior) as Float
                                val lookAngle = playerEntity.lookVec
                                projectileEntity.shoot(
                                    lookAngle.x,
                                    (lookAngle.y.toFloat() + 0.1f).toDouble(),
                                    lookAngle.z,
                                    projectilePower,
                                    projectileUncertainty
                                )
                                projectileEntity.shooter = playerEntity
                                worldIn.addEntity(projectileEntity)
                                itemStack.shrink(1)
                                handler.setStackInSlot(i, itemStack)
                            } catch (e: IllegalAccessException) {
                                e.printStackTrace()
                            } catch (e: InvocationTargetException) {
                                e.printStackTrace()
                            }
                        } else {
                            if (item === Items.SPLASH_POTION || item === Items.LINGERING_POTION) {
                                val projectileEntity: ProjectileEntity =
                                    Util.make(PotionEntity(worldIn, posVec.x, posVec.y, posVec.z),
                                        { potion: PotionEntity -> potion.item = itemStack })
                                projectileEntity.shooter = playerEntity
                                val projectileUncertainty = (6.0 * 0.5).toFloat()
                                val projectilePower = (1.1 * 1.25).toFloat()
                                val lookAngle = playerEntity.lookVec
                                projectileEntity.shoot(
                                    lookAngle.x,
                                    (lookAngle.y.toFloat() + 0.1f).toDouble(),
                                    lookAngle.z,
                                    projectilePower,
                                    projectileUncertainty
                                )
                                worldIn.addEntity(projectileEntity)
                                itemStack.shrink(1)
                                handler.setStackInSlot(i, itemStack)
                            }
                        }
                    }
                }
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn)
    }

    override fun initCapabilities(stack: ItemStack, nbt: CompoundNBT?): ItemInventoryCapabilityProvider {
        return ItemInventoryCapabilityProvider()
    }

    companion object {
        private val RNG = Random()
        lateinit var DISPENSE_REGISTRY: Map<Item, IDispenseItemBehavior>
        lateinit var getProjectileEntity: Method
        lateinit var getProjectileInaccuracy: Method
        lateinit var getProjectileVelocity: Method
    }

    init {
        DISPENSE_REGISTRY = DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY
        getProjectileEntity = ObfuscationReflectionHelper.findMethod(
            ProjectileDispenseBehavior::class.java,
            "getProjectileEntity",
            World::class.java,
            IPosition::class.java,
            ItemStack::class.java
        )
        getProjectileInaccuracy = ObfuscationReflectionHelper.findMethod(
            ProjectileDispenseBehavior::class.java, "getProjectileInaccuracy"
        )
        getProjectileVelocity = ObfuscationReflectionHelper.findMethod(
            ProjectileDispenseBehavior::class.java, "getProjectileInaccuracy"
        )
    }
}