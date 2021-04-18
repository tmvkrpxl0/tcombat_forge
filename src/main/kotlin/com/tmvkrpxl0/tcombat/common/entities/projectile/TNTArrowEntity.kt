package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.events.EntityEventListener
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

    constructor(tntArrowType: EntityType<TNTArrowEntity>, world: World) : super(tntArrowType, world)

    constructor(entityType: EntityType<TNTArrowEntity>, worldIn: World, shooter: LivingEntity) : super(
        entityType, shooter, worldIn
    )


    override fun tick() {
        super.tick()
        if (isWet) explode = false
        if (explode) {
            if (isInLava) {
                val flag = ForgeEventFactory.getMobGriefingEvent(world, this.shooter)
                world.createExplosion(
                    this, this.posX, this.posY, this.posZ, (if (isCritical) 4 else 2).toFloat(), flag && isBurning, if (flag) Explosion.Mode.BREAK else Explosion.Mode.NONE
                )
                this.remove()
                return
            }
            val lookVec = this.lookVec.normalize()
            world.addParticle(ParticleTypes.SMOKE, posX - (lookVec.x/2), posY - (lookVec.y/2), posZ - (lookVec.z/2), 0.0, 0.0, 0.0)
        }
    }

    override fun onImpact(result: RayTraceResult) {
        super.onImpact(result)
        if (this.shooter is LivingEntity) EntityEventListener.explosionImmune.add(this.shooter as LivingEntity)
        if (!world.isRemote() && explode && !isWet) {
            val flag = ForgeEventFactory.getMobGriefingEvent(world, this.shooter)
            world.createExplosion(
                this, this.posX, this.posY, this.posZ, (if (isCritical) 4 else 2).toFloat(), flag && isBurning, if (flag) Explosion.Mode.BREAK else Explosion.Mode.NONE
            )
            this.remove()
        }
    }

    @Nonnull
    override fun getArrowStack(): ItemStack {
        return ItemStack(TCombatItems.TNT_ARROW.get())
    }

    @Nonnull
    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }
}