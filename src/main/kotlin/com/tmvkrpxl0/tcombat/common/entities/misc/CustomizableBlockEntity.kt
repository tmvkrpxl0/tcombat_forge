package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.util.TCombatDataSerializers
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntitySize
import net.minecraft.entity.EntityType
import net.minecraft.entity.item.FallingBlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import java.util.*
import javax.annotation.Nonnull

class CustomizableBlockEntity : Entity, ICustomizableEntity{
    private lateinit var owner: UUID

    companion object {
        val BLOCK_STATE: DataParameter<BlockState> = EntityDataManager.defineId(CustomizableBlockEntity::class.java, TCombatDataSerializers.BLOCK_STATE)
        val IS_SOLID: DataParameter<Boolean> = EntityDataManager.defineId(CustomizableBlockEntity::class.java, DataSerializers.BOOLEAN)
    }

    constructor(type: EntityType<out CustomizableBlockEntity>, world: World) : super(type, world)

    constructor(x: Double, y: Double, z: Double, blockState: BlockState, player: PlayerEntity, isSolid: Boolean, uuid: UUID, width: Float, height: Float) : this(TCombatEntityTypes.CUSTOMIZABLE_BLOCK_ENTITY.get(), player.level) {
        if(!this.level.isClientSide){
            if(!TCombatUtil.isRequested(uuid))this.remove()
        }
        this.setPos(x, y, z)
        this.setBlockState(blockState)
        owner = player.uuid
        this.setSolid(isSolid)
        this.dimensions = EntitySize.scalable(width, height)
    }

    override fun defineSynchedData() {
        this.entityData.define(BLOCK_STATE, Blocks.AIR.defaultBlockState())
        this.entityData.define(IS_SOLID, false)
    }

    override fun getOwner(): PlayerEntity? = this.level.getPlayerByUUID(getOwnerId())

    override fun getOwnerId(): UUID = owner

    fun isSolid(): Boolean = this.entityData.get(IS_SOLID)

    fun setSolid(isSolid: Boolean) = this.entityData.set(IS_SOLID, isSolid)

    fun setBlockState(blockState: BlockState) = this.entityData.set(BLOCK_STATE, blockState)

    fun getBlockState() = this.entityData.get(BLOCK_STATE)!!

    override fun tick() {
        if (!this.level.isClientSide) {
            if (this.getOwner() == null) {
                if (!this.getOwner()!!.isAlive) this.remove()
                if (this.getOwner()!!.position().distanceToSqr(this.position()) > 150 * 150) this.toWorld()
            }
        }
        super.tick()
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        this.owner = compound.getUUID("Owner")
        this.setBlockState(NBTUtil.readBlockState(compound.getCompound("BlockState")))
        val b: BlockState = this.getBlockState()
        if (b.block.isAir(b, this.level, this.blockPosition())) {
            this.setBlockState(Blocks.SAND.defaultBlockState())
        }
        this.setSolid(compound.getBoolean("Solid"))
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        compound.putUUID("Owner", getOwnerId())
        compound.put("BlockState", NBTUtil.writeBlockState(this.getBlockState()))
        compound.putBoolean("Solid", isSolid())

    }

    override fun isPickable(): Boolean = isSolid()

    override fun canBeCollidedWith(): Boolean = isSolid()

    @Nonnull
    override fun getAddEntityPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)

    override fun toWorld() {
        val fallingBlockEntity = FallingBlockEntity(this.level, this.x, this.y, this.z, getBlockState())
        this.level.addFreshEntity(fallingBlockEntity)
        this.remove()
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        buffer.writeUUID(owner)
        buffer.writeFloat(this.dimensions.width)
        buffer.writeFloat(this.dimensions.height)
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        this.owner = additionalData.readUUID()
        this.dimensions = EntitySize.scalable(additionalData.readFloat(), additionalData.readFloat())
    }
}