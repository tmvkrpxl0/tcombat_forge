package com.tmvkrpxl0.tcombat.common.items.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity
import net.minecraft.block.DispenserBlock
import net.minecraft.dispenser.IPosition
import net.minecraft.dispenser.ProjectileDispenseBehavior
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.item.ArrowItem
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class TNTArrowItem(properties: Properties) : ArrowItem(properties) {

    override fun createArrow(worldIn: World, stack: ItemStack, shooter: LivingEntity): AbstractArrowEntity = TNTArrowEntity(TCombatEntityTypes.TNT_ARROW.get(), worldIn, shooter)

    init {
        DispenserBlock.registerBehavior(this, object : ProjectileDispenseBehavior() {
            override fun getProjectile(worldIn: World, position: IPosition, stackIn: ItemStack): ProjectileEntity = TNTArrowEntity(worldIn, position.x(), position.y(), position.z())
        })
    }
}