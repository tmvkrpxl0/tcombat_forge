package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.events.EntityEventListener
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.IPacket
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.network.NetworkHooks
import javax.annotation.Nonnull

class TNTArrowEntity : AbstractArrowEntity {
    private var explode = true

    constructor(worldIn: World, x: Double, y: Double, z: Double) : super(
        TCombatEntityTypes.TNT_ARROW.get(), x, y, z, worldIn
    )

    constructor(tntArrowType: EntityType<TNTArrowEntity>, worldIn: World) : super(tntArrowType, worldIn)

    constructor(entityType: EntityType<TNTArrowEntity>, worldIn: World, shooter: LivingEntity) : super(
        entityType, shooter, worldIn
    )


    override fun tick() {
        super.tick()
        if (this.isInWaterRainOrBubble) explode = false
        if (explode) {
            if (isInLava) {
                val flag = ForgeEventFactory.getMobGriefingEvent(this.level, this.owner)
                level.explode(
                    this, this.x, this.y, this.z, (if (this.isCritArrow) 4 else 2).toFloat(), flag && this.isOnFire, if (flag) Explosion.Mode.BREAK else Explosion.Mode.NONE
                )
                this.remove()
                return
            }
            val lookVec = this.lookAngle.normalize()
            level.addParticle(ParticleTypes.SMOKE, x - (lookVec.x/2), this.y - (lookVec.y/2), this.z - (lookVec.z/2), 0.0, 0.0, 0.0)
        }
    }

    override fun onHit(result: RayTraceResult) {
        super.onHit(result)
        if (this.owner is LivingEntity) EntityEventListener.explosionImmune.add(this.owner as LivingEntity)
        if (!level.isClientSide && explode && !this.isInWaterRainOrBubble) {
            val flag = ForgeEventFactory.getMobGriefingEvent(level, this.owner)
            level.explode(
                this, this.x, this.y, this.z, (if (this.isCritArrow) 4 else 2).toFloat(), flag && this.isOnFire, if (flag) Explosion.Mode.BREAK else Explosion.Mode.NONE
            )
            this.remove()
        }
    }

    @Nonnull
    override fun getPickupItem(): ItemStack {
        return ItemStack(TCombatItems.TNT_ARROW.get())
    }

    @Nonnull
    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }
}