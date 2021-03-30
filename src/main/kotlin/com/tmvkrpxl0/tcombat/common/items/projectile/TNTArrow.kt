package com.tmvkrpxl0.tcombat.common.items.projectile

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
import javax.annotation.Nonnull

class TNTArrow(properties: Properties) : ArrowItem(properties) {
    @Nonnull
    override fun createArrow(
        @Nonnull worldIn: World,
        @Nonnull stack: ItemStack,
        shooter: LivingEntity
    ): AbstractArrowEntity {
        val pos = shooter.positionVec
        val entity = TNTArrowEntity(worldIn, pos.x, pos.y, pos.z)
        entity.shooter = shooter
        return entity
    }

    init {
        DispenserBlock.registerDispenseBehavior(this, object : ProjectileDispenseBehavior() {
            @Nonnull
            override fun getProjectileEntity(
                @Nonnull worldIn: World,
                @Nonnull position: IPosition,
                @Nonnull stackIn: ItemStack
            ): ProjectileEntity {
                return TNTArrowEntity(worldIn, position.x, position.y, position.z)
            }
        })
    }
}