package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
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
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks
import java.util.*
import javax.annotation.Nonnull


class CustomizableBlockEntity : Entity, ICustomizableEntity{
    private lateinit var owner: UUID

    companion object {
        val BLOCK_STATE: DataParameter<BlockState> = EntityDataManager.createKey(CustomizableBlockEntity::class.java, TCombatUtil.BLOCK_STATE)
        val IS_SOLID: DataParameter<Boolean> = EntityDataManager.createKey(CustomizableBlockEntity::class.java, DataSerializers.BOOLEAN)
    }

    constructor(type: EntityType<out CustomizableBlockEntity>, world: World) : super(type, world)

    constructor(x: Double, y: Double, z: Double, blockState: BlockState, player: PlayerEntity, isSolid: Boolean) : this(TCombatEntityTypes.CUSTOMIZABLE_BLOCK_ENTITY.get(), player.world) {
        this.setPosition(x, y, z)
        this.setBlockState(blockState)
        owner = player.uniqueID
        this.setSolid(isSolid)
    }

    override fun registerData() {
        this.dataManager.register(BLOCK_STATE, Blocks.AIR.defaultState)
        this.dataManager.register(IS_SOLID, false)
    }

    override fun getOwner(): PlayerEntity? = this.world.getPlayerByUuid(getOwnerId())

    override fun getOwnerId(): UUID = owner

    fun isSolid(): Boolean = this.getDataManager().get(IS_SOLID)

    fun setSolid(isSolid: Boolean) = this.getDataManager().set(IS_SOLID, isSolid)

    fun setBlockState(blockState: BlockState) = this.getDataManager().set(BLOCK_STATE, blockState)

    fun getBlockState() = this.getDataManager().get(BLOCK_STATE)!!

    override fun tick() {
        if (!this.world.isRemote) {
            if (this.getOwner() == null) {
                if (!this.getOwner()!!.isAlive) this.remove()
                if (this.getOwner()!!.position.distanceSq(this.position) > 150 * 150) this.toWorld()
            }
        }
        super.tick()
    }

    //Order:
    //PlayerEntity
    //BlockState
    //isSolid
    override fun readAdditional(compound: CompoundNBT) {
        this.owner = compound.getUniqueId("Owner")
        this.setBlockState(NBTUtil.readBlockState(compound.getCompound("BlockState")))
        val b: BlockState = this.getBlockState()
        if (b.block.isAir(b, this.entityWorld, this.entityBlockPosition)) {
            this.setBlockState(Blocks.SAND.defaultState)
        }
        this.setSolid(compound.getBoolean("Solid"))
    }

    override fun writeAdditional(compound: CompoundNBT) {
        compound.putUniqueId("Owner", getOwnerId())
        compound.put("BlockState", NBTUtil.writeBlockState(this.getBlockState()))
        compound.putBoolean("Solid", isSolid())

    }

    override fun func_241845_aY(): Boolean = isSolid()

    override fun canBeCollidedWith(): Boolean = isSolid()

    @Nonnull
    override fun createSpawnPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)

    override fun toWorld() {
        val fallingBlockEntity = FallingBlockEntity(this.world, this.posX, this.posY, this.posZ, getBlockState())
        world.addEntity(fallingBlockEntity)
        this.remove()
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        buffer.writeUniqueId(owner)
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        this.owner = additionalData.readUniqueId()
    }
}