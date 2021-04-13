package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.fluid.Fluids
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import java.util.*

class CustomizableFluidEntity : Entity, ICustomizableEntity {
    companion object{
        val OWNER_UNIQUE_ID: DataParameter<UUID> = EntityDataManager.createKey(CustomizableFluidEntity::class.java, TCombatUtil.UNIQUE_ID)
        val FLUID_STATE: DataParameter<FluidState> = EntityDataManager.createKey(CustomizableFluidEntity::class.java, TCombatUtil.FLUID_STATE)
    }
    constructor(entityTypeIn: EntityType<*>, worldIn: World):super(entityTypeIn, worldIn)

    fun initialize(x: Double,
                   y: Double,
                   z: Double,
                   player: PlayerEntity,
                   fluidState: FluidState){
        this.setOwner(player)
        this.setPosition(x,y,z)
        this.setFluidState(fluidState)
    }

    override fun tick(){
        if(!this.world.isRemote){
            if(this.getOwner()==null || !this.getOwner()!!.isAlive)this.remove()
        }
        super.tick()
    }

    override fun getOwner() : PlayerEntity? = this.world.getPlayerByUuid(getOwnerId())

    override fun getOwnerId(): UUID = this.dataManager.get(OWNER_UNIQUE_ID)

    override fun setOwner(player: PlayerEntity) = setOwnerId(player.uniqueID)

    override fun setOwnerId(uuid: UUID) = this.dataManager.set(OWNER_UNIQUE_ID, uuid)
    
    fun getFluidState(): FluidState = this.dataManager.get(FLUID_STATE)
    
    fun setFluidState(fluidState: FluidState) = this.dataManager.set(FLUID_STATE, fluidState)

    override fun registerData(){
        this.dataManager.register(OWNER_UNIQUE_ID, UUID(0,0))
        this.dataManager.register(FLUID_STATE, Fluids.EMPTY.defaultState)
    }

    override fun readAdditional(compound: CompoundNBT) {
        setOwnerId(compound.getUniqueId("Owner"))
        setFluidState(TCombatUtil.readFluidState(compound.getCompound("FluidState")))
    }

    override fun writeAdditional(compound: CompoundNBT) {
        compound.putUniqueId("Owner", getOwnerId())
        compound.put("FluidState", TCombatUtil.writeFluidState(getFluidState()))
    }

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun toWorld() {
        val b = this.world.getBlockState(this.position)
        if (!b.isReplaceable(getFluidState().fluid)) {
            this.world.setBlockState(this.position, getFluidState().blockState)
        }
        this.remove()
    }

    override fun remove() {
        if(this.world.isRemote){
            this.world.playSound(this.getOwner(), this.position, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f)
            for (i in 0..5) {
                val d0 = rand.nextGaussian() * 0.01
                val d1 = rand.nextGaussian() * 0.01
                val d2 = rand.nextGaussian() * 0.01
                world.addParticle(ParticleTypes.POOF, getPosXRandom(0.2), this.posYRandom, getPosZRandom(0.2), d0, d1, d2)
            }
        }

        super.remove()
    }

    override fun applyEntityCollision(p_70108_1_: Entity) {
        super.applyEntityCollision(p_70108_1_)
    }

    fun onCollideWithCBFluid(other: CustomizableFluidEntity){}

    fun onCollideWithEntity(other: Entity){}
}