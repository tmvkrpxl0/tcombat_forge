package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.TameableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.IPacket
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import javax.annotation.Nonnull

class CustomizableBlockEntity : Entity, ICustomizableEntity {
    private var isSolid = false
    lateinit var player: PlayerEntity //This should never be null
    lateinit var blockState: BlockState private set

    constructor(type: EntityType<out CustomizableBlockEntity>, world: World) : super(type, world){

    }
    fun initialize(x: Double,
                   y: Double,
                   z: Double,
                   blockState: BlockState,
                   player: PlayerEntity,
                   isSolid: Boolean){
        this.setPosition(x,y,z)
        this.blockState = blockState
        this.player = player
        this.isSolid = isSolid
        serializeNBT()
    }

    override fun registerData() {

    }

    override fun getOwner() : PlayerEntity{
        return this.player
    }

    override fun tick() {
        if (!this.world.isRemote) {
            if (!this.player.isAlive) this.remove()
        }
        super.tick()
    }

    //Order:
    //PlayerEntity
    //BlockState
    //isSolid
    override fun readAdditional(compound: CompoundNBT) {
        this.player = this.world.getPlayerByUuid(compound.getUniqueId("Owner"))!!
        this.blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"))
        if (blockState.block.isAir(this.blockState, this.entityWorld, this.entityBlockPosition)) {
            this.blockState = Blocks.SAND.defaultState
        }
        this.isSolid = compound.getBoolean("Solid")
    }

    override fun writeAdditional(compound: CompoundNBT) {
        this.world.isRemote
        compound.putUniqueId("Owner", this.player.uniqueID)
        compound.put("BlockState", NBTUtil.writeBlockState(this.blockState))
        compound.putBoolean("Solid", this.isSolid)

    }

    override fun func_241845_aY(): Boolean {
        return this.isSolid
    }

    @Nonnull
    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    override fun toWorld(){
        val b = this.world.getBlockState(this.position)
        if(!b.isOpaqueCube(this.world, this.position)){
            this.world.setBlockState(this.position, this.blockState)
        }
        this.remove()
    }
}