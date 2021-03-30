package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.listeners.CommonEventListener
import com.tmvkrpxl0.tcombat.common.network.packets.RocketJumpPacket
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.IPacket
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.fml.network.PacketDistributor
import javax.annotation.Nonnull

class TNTArrowEntity : AbstractArrowEntity {
    private var explode = true

    constructor(worldIn: World, x: Double, y: Double, z: Double) : super(
        TCombatEntityTypes.TNT_ARROW.get(),
        x,
        y,
        z,
        worldIn
    )

    constructor(
        tntArrowType: EntityType<TNTArrowEntity>,
        world: World
    ) : super(tntArrowType, world)

    override fun tick() {
        super.tick()
        if (isInWater) explode = false
        if (explode) {
            if (isInLava) {
                if (this.shooter is LivingEntity) CommonEventListener.explosionImmune.add(this.shooter as LivingEntity)
                if (!world.isRemote) {
                    explosion(posX, posY, posZ)
                }
                this.remove()
                return
            }
            world.addParticle(ParticleTypes.SMOKE, posX, posY, posZ, 0.0, 0.0, 0.0)
        }
    }

    private fun explosion(x: Double, y: Double, z: Double) {
        world.createExplosion(this, x, y, z, (if (isCritical) 4 else 2).toFloat(), isBurning, Explosion.Mode.BREAK)
        val packet = RocketJumpPacket(
            TCombatUtil.getWorldId(world), if (this.shooter is LivingEntity) (this.shooter as LivingEntity).entityId else -1,
            posX, posY, posZ, if (isCritical) 4 else 2, isBurning, Explosion.Mode.BREAK.ordinal
        )
        TCombatPacketHandler.Companion.INSTANCE.send<RocketJumpPacket>(
            PacketDistributor.DIMENSION.with { world.dimensionKey },
            packet
        )
    }

    override fun onEntityHit(@Nonnull result: EntityRayTraceResult) {
        super.onEntityHit(result)
        if (world.isRemote()) return
        val location = result.hitVec
        if (explode && !isInWater) {
            if (this.shooter is LivingEntity) CommonEventListener.explosionImmune.add(this.shooter as LivingEntity)
            if (!world.isRemote) {
                explosion(location.x, location.y, location.z)
            }
            this.remove()
        }
    }

    override fun func_230299_a_(@Nonnull result: BlockRayTraceResult) {
        super.func_230299_a_(result)
        val pos = result.pos
        if (explode && !isInWater) {
            if (this.shooter is LivingEntity) CommonEventListener.explosionImmune.add(this.shooter as LivingEntity)
            if (!world.isRemote) {
                explosion(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
            }
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