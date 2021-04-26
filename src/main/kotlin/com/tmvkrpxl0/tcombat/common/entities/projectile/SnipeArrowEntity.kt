package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.IPacket
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class SnipeArrowEntity : AbstractArrowEntity {

    constructor(entityType: EntityType<SnipeArrowEntity>, world: World) : super(entityType, world)
    constructor(entityType: EntityType<SnipeArrowEntity>, shooter: LivingEntity, world: World) : super(entityType, shooter, world)

    private var originalLength: Vector3d? = null

    override fun getPickupItem(): ItemStack {
        return ItemStack(TCombatItems.TNT_ARROW.get())
    }

    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun shoot(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) {
        originalLength = Vector3d(x, y, z)
        super.shoot(x, y, z, velocity * 100, inaccuracy)
    }

    override fun onHitEntity(result: EntityRayTraceResult) {
        if (originalLength != null) {
            this.deltaMovement = originalLength!!
            this.markHurt()
        }
        super.onHitEntity(result)
    }
}
