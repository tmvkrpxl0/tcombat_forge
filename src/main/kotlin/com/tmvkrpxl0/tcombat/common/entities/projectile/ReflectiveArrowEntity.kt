package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.entity.projectile.AbstractArrowEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.IPacket
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.network.play.server.SEntityVelocityPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.EntityRayTraceResult
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.fml.server.ServerLifecycleHooks
import java.lang.Double.max

class ReflectiveArrowEntity: AbstractArrowEntity {
    var totalDistanceSq = 0.0
    var lastDistanceSq = 0.0
    var target: LivingEntity? = null

    companion object{
        val FROM: DataParameter<BlockPos> = EntityDataManager.defineId(ReflectiveArrowEntity::class.java, DataSerializers.BLOCK_POS)
        val STOP: DataParameter<Boolean> = EntityDataManager.defineId(ReflectiveArrowEntity::class.java, DataSerializers.BOOLEAN)
    }

    constructor(entityType: EntityType<ReflectiveArrowEntity>, world: World) : super(entityType, world){
        this.isNoGravity = true
    }

    constructor(entityType: EntityType<ReflectiveArrowEntity>, shooter: LivingEntity, world: World): super(entityType, shooter, world){
        setFrom(shooter.blockPosition())
        this.isNoGravity = true
    }

    override fun defineSynchedData() {
        this.entityData.define(FROM, BlockPos.ZERO)
        this.entityData.define(STOP, false)
        super.defineSynchedData()
    }

    override fun setOwner(p_212361_1_: Entity?) {
        if(p_212361_1_!=null)setFrom(p_212361_1_.blockPosition())
        super.setOwner(p_212361_1_)
    }

    private fun getFrom(): BlockPos = this.entityData.get(FROM)

    fun setFrom(blockPos: BlockPos) = this.entityData.set(FROM, blockPos)

    fun isStop():Boolean = this.entityData.get(STOP)

    fun stop(){
        this.entityData.set(STOP, true)
        this.isNoGravity = false
    }

    override fun tick(){
        if(!this.isInWater){
            if(!this.inGround){
                if(this.totalDistanceSq <= 25 * 25){
                    this.deltaMovement = this.deltaMovement.scale(1 / 0.99)
                }
            }
        }
        if(!this.level.isClientSide){
            if(this.target!=null){
                if(!this.isStop()){
                    val vToTarget = this.target!!.position().subtract(this.position()).normalize()
                    val deltaLength = this.deltaMovement.length()
                    this.deltaMovement = vToTarget.scale(deltaLength)

                }
            }
            if(this.getFrom() == BlockPos.ZERO) {
                this.remove()
            }
        }
        this.totalDistanceSq = max(this.lastDistanceSq + this.getFrom().distSqr(this.blockPosition()), this.totalDistanceSq)
        if(this.totalDistanceSq>25 * 25)this.isNoGravity = false
        if(this.isInWater)this.stop()
        super.tick()
    }

    override fun onHitBlock(result: BlockRayTraceResult) {
        if(!this.level.isClientSide){
            if(!this.isInWater){
                if(this.totalDistanceSq <= 25 * 25){
                    val normal = result.direction.normal
                    val surface = Vector3d(normal.x.toDouble(), normal.y.toDouble(), normal.z.toDouble())
                    reflect(surface, result.blockPos)
                    return
                }
            }
        }
        super.onHitBlock(result)
    }

    override fun onHitEntity(result: EntityRayTraceResult) {
        if(!this.level.isClientSide){
            if(result.entity is LivingEntity && (result.entity as LivingEntity).useItem.isShield(result.entity as LivingEntity)){
                val look = result.entity.lookAngle
                reflect(Vector3d(look.x, 0.0, look.z), result.entity.blockPosition())
            }
        }
    }

    override fun getPickupItem(): ItemStack = ItemStack(TCombatItems.REFLECTIVE_ARROW.get())

    override fun getAddEntityPacket(): IPacket<*> {
        return NetworkHooks.getEntitySpawningPacket(this)
    }

    private fun reflect(surface: Vector3d, from: BlockPos){
        if(this.level.isClientSide)return
        val reflected = TCombatUtil.getReflect(this.deltaMovement, surface)
        this.deltaMovement = reflected
        this.lastDistanceSq = this.totalDistanceSq
        this.setFrom(from)
        val velocityPacket = SEntityVelocityPacket(this)
        val players = ServerLifecycleHooks.getCurrentServer().playerList
        this.markHurt()
        for(p in players.players){
            (p as ServerPlayerEntity).connection.send(velocityPacket)
        }
    }
}