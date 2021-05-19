package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.util.TCombatDataSerializers
import com.tmvkrpxl0.tcombat.common.util.VanilaCopy
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import java.util.*

class CustomizableFluidEntity : Entity, ICustomizableEntity {
    lateinit var owner: UUID

    companion object {
        val FLUID_STATE: DataParameter<FluidState> = EntityDataManager.defineId(CustomizableFluidEntity::class.java, TCombatDataSerializers.FLUID_STATE)
    }

    constructor(type: EntityType<*>, world: World) : super(type, world)

    constructor(x: Double, y: Double, z: Double, player: PlayerEntity, fluidState: FluidState) : this(TCombatEntityTypes.CUSTOMIZABLE_FLUID_ENTITY.get(), player.level) {
        owner = player.uuid
        this.setPos(x, y, z)
        this.setFluidState(fluidState)

    }

    override fun tick() {
        if (!this.level.isClientSide) {
            if (this.getOwner() == null || !this.getOwner()!!.isAlive) this.remove()
        }
        super.tick()
    }

    override fun getOwner(): PlayerEntity? = this.level.getPlayerByUUID(getOwnerId())

    override fun getOwnerId(): UUID = this.owner

    fun getFluidState(): FluidState = this.entityData.get(FLUID_STATE)

    fun setFluidState(fluidState: FluidState) = this.entityData.set(FLUID_STATE, fluidState)

    override fun defineSynchedData() {
        this.entityData.define(FLUID_STATE, Fluids.EMPTY.defaultFluidState())
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        this.owner = compound.getUUID("Owner")
        this.setFluidState(VanilaCopy.readFluidState(compound.getCompound("FluidState")))
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        compound.putUUID("Owner", getOwnerId())
        compound.put("FluidState", VanilaCopy.writeFluidState(getFluidState()))
    }

    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun toWorld() {
        val b = this.level.getBlockState(this.blockPosition())
        if (!b.canBeReplaced(getFluidState().type)) {
            this.level.setBlockAndUpdate(this.blockPosition(), getFluidState().createLegacyBlock())
        }
        this.remove()
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        buffer.writeUUID(this.getOwnerId())
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        this.owner = additionalData.readUUID()
    }

    override fun remove() {
        if (this.level.isClientSide) {
            this.level.playSound(this.getOwner(), this.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f)
            for (i in 0..5) {
                val d0 = random.nextGaussian() * 0.01
                val d1 = random.nextGaussian() * 0.01
                val d2 = random.nextGaussian() * 0.01
                this.level.addParticle(ParticleTypes.POOF, getRandomX(0.2), this.randomY, this.getRandomZ(0.2), d0, d1, d2)
            }
        }

        super.remove()
    }

    override fun push(p_70108_1_: Entity) {
        super.push(p_70108_1_)
    }

    fun onCollideWithCBFluid(other: CustomizableFluidEntity) {}

    fun onCollideWithEntity(other: Entity) {}
}