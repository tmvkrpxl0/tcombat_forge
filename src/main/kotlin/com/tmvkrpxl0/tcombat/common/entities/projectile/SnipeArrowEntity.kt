package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.entity.projectile.ArrowEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.IPacket
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import java.lang.Double.max

class SnipeArrowEntity : ArrowEntity {

    constructor(entityType: EntityType<SnipeArrowEntity>, world: World) : super(entityType, world)
    constructor(worldIn: World, shooter: LivingEntity) : super(worldIn, shooter)

    private var originalLength:Vector3d? = null

    override fun getArrowStack(): ItemStack {
        return ItemStack(TCombatItems.TNT_ARROW.get())
    }

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun shoot(x: Double, y: Double, z: Double, velocity: Float, inaccuracy: Float) {
        originalLength = Vector3d(x,y,z)
        super.shoot(x, y, z, velocity*100, inaccuracy)
    }

    override fun onEntityHit(result: EntityRayTraceResult) {
        if(originalLength!=null){
            this.motion = originalLength!!
            this.markVelocityChanged()
        }
        super.onEntityHit(result)
    }
}
