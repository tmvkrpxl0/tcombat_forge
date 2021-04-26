package com.tmvkrpxl0.tcombat.common.items.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.entities.projectile.ReflectiveArrowEntity
import net.minecraft.block.DispenserBlock
import net.minecraft.dispenser.IPosition
import net.minecraft.dispenser.ProjectileDispenseBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class ReflectiveArrowItem(properties:Properties): ArrowItem(properties) {
    override fun createArrow(worldIn: World, stack: ItemStack, shooter: LivingEntity): AbstractArrowEntity =
        ReflectiveArrowEntity(TCombatEntityTypes.REFLECTIVE_ARROW.get(), shooter, worldIn)

    init{
        DispenserBlock.registerBehavior(this, object: ProjectileDispenseBehavior(){
            override fun getProjectile(world: World, iPosition: IPosition, itemStack: ItemStack): ProjectileEntity {
                val reflect = ReflectiveArrowEntity(TCombatEntityTypes.REFLECTIVE_ARROW.get(), world)
                reflect.setPos(iPosition.x(), iPosition.y(), iPosition.z())
                reflect.setFrom(BlockPos(iPosition))
                return reflect
            }
        })
    }
}