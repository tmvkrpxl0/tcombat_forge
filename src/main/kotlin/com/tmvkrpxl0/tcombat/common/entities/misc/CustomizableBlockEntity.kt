package com.tmvkrpxl0.tcombat.common.entities.misc

import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.IronGolemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.NBTUtil
import net.minecraft.network.IPacket
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import javax.annotation.Nonnull

class CustomizableBlockEntity : Entity {
    private var isSolid = false
    private var player: PlayerEntity? = null
    var blockState: BlockState = Blocks.SAND.defaultState
        private set

    constructor(
        worldIn: World,
        x: Double,
        y: Double,
        z: Double,
        blockState: BlockState,
        player: PlayerEntity?,
        isSolid: Boolean
    ) : super(TCombatEntityTypes.CUSTOMIZABLE_BLOCK_ENTITY.get(), worldIn) {
        setPosition(x, y + ((1.0f - this.height) / 2.0f).toDouble(), z)
        this.player = player
        this.blockState = blockState
        this.isSolid = isSolid
        dataManager.set(OWNER_ID, this.player!!.entityId)
    }

    constructor(type: EntityType<out CustomizableBlockEntity>, world: World) : super(type, world)

    override fun tick() {
        super.tick()
        if (!world.isRemote) {
            if (player == null || !player!!.isAlive || !world.players.contains(player)) this.remove()
        }
        blockState = Blocks.DARK_OAK_SAPLING.defaultState
    }

    override fun registerData() {
        dataManager.register(OWNER_ID, 0)
    }

    override fun readAdditional(compound: CompoundNBT) {
        blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"))
        if (blockState.block.isAir(blockState, this.entityWorld, entityBlockPosition)) {
            blockState = Blocks.SAND.defaultState
        }
        isSolid = compound.getBoolean("Solid")
        player = world.getPlayerByUuid(compound.getUniqueId("Owner"))
    }

    override fun writeAdditional(compound: CompoundNBT) {
        compound.putBoolean("Solid", isSolid)
        compound.put("BlockState", NBTUtil.writeBlockState(blockState))
        compound.putUniqueId("Owner", player!!.uniqueID)
    }

    override fun func_241845_aY(): Boolean {
        return isSolid
    }

    @Nonnull
    override fun createSpawnPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    companion object {
        private val OWNER_ID: DataParameter<Int> =
            EntityDataManager.createKey(IronGolemEntity::class.java, DataSerializers.VARINT)
    }
}