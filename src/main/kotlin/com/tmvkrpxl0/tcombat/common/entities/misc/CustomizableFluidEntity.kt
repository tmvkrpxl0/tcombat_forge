package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.FluidState
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.SoundEvents
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class CustomizableFluidEntity : Entity, ICustomizableEntity {
    private lateinit var player: PlayerEntity //This should never be null. if it is null somehow, it's error
    lateinit var fluidState:FluidState
    constructor(entityTypeIn: EntityType<*>, worldIn: World):super(entityTypeIn, worldIn)

    constructor(
        world: World,
        x: Double,
        y: Double,
        z: Double,
        player: PlayerEntity,
        fluidState: FluidState
    ):this(TCombatEntityTypes.CUSTOMIZABLE_FLUID_ENTITY.get(), world){
        this.player = player
        this.setPosition(x,y,z)
        this.fluidState = fluidState
    }

    override fun tick(){
        if(!this.world.isRemote){
            if(!this.player.isAlive)this.remove()
        }
        super.tick()
    }

    override fun getOwner() : PlayerEntity{
        return player
    }

    override fun registerData() {

    }

    override fun readAdditional(compound: CompoundNBT) {
        player = world.getPlayerByUuid(compound.getUniqueId("Owner"))!!
        fluidState = TCombatUtil.readFluidState(compound.getCompound("FluidState"))
    }

    override fun writeAdditional(compound: CompoundNBT) {
        compound.putUniqueId("Owner", player.uniqueID)
        compound.put("FluidState", TCombatUtil.writeFluidState(fluidState))
    }

    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun toWorld() {
        val b = this.world.getBlockState(this.position)
        if (!b.isReplaceable(this.fluidState.fluid)) {
            this.world.setBlockState(this.position, this.fluidState.blockState)
        }
        this.remove()
    }

    override fun remove() {
        this.world.playSound(this.player, this.position, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1f, 1f)
        super.remove()
    }
}