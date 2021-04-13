package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.TCombatMain
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
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import java.util.*
import javax.annotation.Nonnull


class CustomizableBlockEntity(type: EntityType<out CustomizableBlockEntity>, world: World) : Entity(type, world), ICustomizableEntity {
    companion object{
        val OWNER_UNIQUE_ID:DataParameter<UUID> = EntityDataManager.createKey(CustomizableBlockEntity::class.java, TCombatUtil.UNIQUE_ID)
        val BLOCK_STATE:DataParameter<BlockState> = EntityDataManager.createKey(CustomizableBlockEntity::class.java, TCombatUtil.BLOCK_STATE)
        val IS_SOLID:DataParameter<Boolean> = EntityDataManager.createKey(CustomizableBlockEntity::class.java, DataSerializers.BOOLEAN)
    }

    fun initialize(x: Double,
                   y: Double,
                   z: Double,
                   blockState: BlockState,
                   uniqueId: UUID,
                   isSolid: Boolean){
        this.setPosition(x,y,z)
        this.setBlockState(blockState)
        this.setOwnerId(uniqueId)
        this.setSolid(isSolid)
        this.serializeNBT()
    }

    override fun registerData() {
        this.dataManager.register(OWNER_UNIQUE_ID, UUID(0L, 0L))
        this.dataManager.register(BLOCK_STATE, Blocks.AIR.defaultState)
        this.dataManager.register(IS_SOLID, false)
    }

    override fun getOwner() : PlayerEntity? = this.world.getPlayerByUuid(getOwnerId())

    override fun getOwnerId(): UUID = this.getDataManager().get(OWNER_UNIQUE_ID)

    override fun setOwnerId(uuid:UUID) = this.getDataManager().set(OWNER_UNIQUE_ID, uuid)

    override fun setOwner(player: PlayerEntity) = setOwnerId(player.uniqueID)
    
    fun isSolid():Boolean = this.getDataManager().get(IS_SOLID)
    
    fun setSolid(isSolid: Boolean) = this.getDataManager().set(IS_SOLID, isSolid)

    fun setBlockState(blockState: BlockState){
        TCombatMain.LOGGER.info("Serializer: $BLOCK_STATE blockState: $blockState")
        this.getDataManager().set(BLOCK_STATE, blockState)
    }

    fun getBlockState() = this.getDataManager().get(BLOCK_STATE)

    override fun tick() {
        if (!this.world.isRemote) {
            if (this.getOwner()==null){
                if(!this.getOwner()!!.isAlive) this.remove()
                if(this.getOwner()!!.position.distanceSq(this.position) > 150*150)this.toWorld()
            }
        }
        super.tick()
    }

    //Order:
    //PlayerEntity
    //BlockState
    //isSolid
    override fun readAdditional(compound: CompoundNBT) {
        this.setOwnerId(compound.getUniqueId("Owner"))
        this.setBlockState(NBTUtil.readBlockState(compound.getCompound("BlockState")))
        val b:BlockState = this.getBlockState()
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

    override fun func_241845_aY(): Boolean {
        return isSolid()
    }

    override fun canBeCollidedWith(): Boolean = isSolid()

    @Nonnull
    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun toWorld(){
        val fallingBlockEntity = FallingBlockEntity(this.world, this.posX, this.posY, this.posZ, getBlockState())
        world.addEntity(fallingBlockEntity)
        this.remove()
    }
}