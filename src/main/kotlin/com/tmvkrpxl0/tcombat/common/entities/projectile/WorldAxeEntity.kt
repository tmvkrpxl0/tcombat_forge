package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.capability.capabilities.WorldAxeCapability
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.block.BlockState
import net.minecraft.client.entity.player.ClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.projectile.ProjectileEntity
import net.minecraft.entity.projectile.ProjectileHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundNBT
import net.minecraft.network.IPacket
import net.minecraft.network.PacketBuffer
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.particles.BlockParticleData
import net.minecraft.particles.ParticleTypes
import net.minecraft.util.math.*
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks

class WorldAxeEntity : ProjectileEntity, IEntityAdditionalSpawnData {
    companion object{
        val HOOKED: DataParameter<Int> = EntityDataManager.defineId(WorldAxeEntity::class.java, DataSerializers.INT)
    }
    var inGround = false
    private var lastState: BlockState? = null
    lateinit var baseAxe: ItemStack
    private var hooked: LivingEntity? = null

    constructor(entityType: EntityType<WorldAxeEntity>, world: World) : super(entityType, world)

    constructor(player: PlayerEntity, baseItem:ItemStack):this(TCombatEntityTypes.WORLD_AXE.get(), player.level){
        this.owner = player
        this.baseAxe = baseItem
        this.absMoveTo(player.x, player.eyeY, player.z, player.yRot, player.xRot)
        this.isNoGravity = false
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        this.baseAxe = ItemStack.of(compound.getCompound("BaseAxe"))
        this.inGround = compound.getBoolean("inGround")
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        val c = CompoundNBT()
        compound.put("BaseAxe", this.baseAxe.save(c))
        compound.putBoolean("inGround", this.inGround)
        super.addAdditionalSaveData(compound)
    }

    override fun getAddEntityPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)

    override fun defineSynchedData() {
        this.entityData.define(HOOKED, 0)
    }

    override fun remove() {
        val cap = this.baseAxe.getCapability(WorldAxeCapability.itemEntityConnectionHandler)
        if(cap.resolve().isPresent){
            val entityHolder = cap.resolve().get()
            entityHolder.setEntity(null)
        }
        super.remove()
    }

    override fun tick() {
        if(this.owner == null || !this.owner!!.isAlive || this.shouldRemove(this.owner as PlayerEntity)){
            this.remove()
        }
        if(this.hooked?.isAlive != true){
            this.hooked = null
            this.entityData.set(HOOKED, 0)
        }
        var vector3d = this.deltaMovement
        if(xRotO == 0.0f && yRotO == 0.0f) {
            val f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))
            yRot = (MathHelper.atan2(vector3d.x, vector3d.z) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
            xRot = (MathHelper.atan2(vector3d.y, f.toDouble()) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
            yRotO = yRot
            xRotO = xRot
        }
        val blockpos = this.blockPosition()
        val blockstate = this.level.getBlockState(blockpos)
        if(!blockstate.isAir(this.level, blockpos)) {
            val voxelshape = blockstate.getCollisionShape(this.level, blockpos)
            if(!voxelshape.isEmpty) {
                val vector3d1 = this.position()
                for(axisalignedbb in voxelshape.toAabbs()) {
                    if(axisalignedbb.move(blockpos).contains(vector3d1)) {
                        inGround = true
                        break
                    }
                }
            }
        }
        if(this.isInWaterOrRain) {
            this.clearFire()
        }
        if(inGround) {
            if(lastState !== blockstate && this.shouldFall()) {
                this.fall()
            }
        } else if(this.hooked == null){
            vector3d = this.deltaMovement
            val d3 = vector3d.x
            val d4 = vector3d.y
            val d0 = vector3d.z
            val d5 = this.x + d3
            val d1 = this.y + d4
            val d2 = this.z + d0
            val f1 = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))
            xRot = (MathHelper.atan2(d4, f1.toDouble()) * (180/ Math.PI)).toFloat()
            xRot = lerpRotation(xRotO, xRot)
            var f2 = 0.99f
            if(this.isInWater) {
                f2 = getWaterInertia()
            }
            this.deltaMovement = vector3d.scale(f2.toDouble())
            if(!isNoGravity) {
                val vector3d4 = this.deltaMovement
                this.setDeltaMovement(vector3d4.x, vector3d4.y - 0.05, vector3d4.z)
            }
            this.setPos(d5, d1, d2)
           this.checkInsideBlocks()
        }else{
            val h = this.hooked!!
            this.setPos(h.x, h.getY(0.8), h.z)
        }
        super.tick()
    }

    private fun shouldFall(): Boolean {
        return inGround && this.level.noCollision(AxisAlignedBB(this.position(), this.position()).inflate(0.06))
    }

    private fun fall() {
        inGround = false
        val vector3d = this.deltaMovement
        this.deltaMovement = vector3d.multiply(
            (random.nextFloat() * 0.2f).toDouble(),
            (random.nextFloat() * 0.2f).toDouble(),
            (random.nextFloat() * 0.2f).toDouble()
        )
    }

    fun getWaterInertia(): Float {
        return 0.6f
    }

    override fun onHitEntity(result: EntityRayTraceResult) {
        super.onHitEntity(result)
        if (!this.level.isClientSide) {
            if(result.entity is LivingEntity){
                this.hooked = result.entity as LivingEntity
                this.setHookedEntity()
            }
        }
    }

    private fun setHookedEntity() {
        this.entityData.set(HOOKED, this.hooked!!.id)
    }

    override fun onHitBlock(result: BlockRayTraceResult) {
        lastState = this.level.getBlockState(result.blockPos)
        super.onHitBlock(result)
        val vector3d = result.location.subtract(this.x, this.y, this.z)
        this.deltaMovement = vector3d
        val vector3d1 = vector3d.normalize().scale(0.05f.toDouble())
        setPosRaw(this.x - vector3d1.x, this.y - vector3d1.y, this.z - vector3d1.z)
        if(lastState!=null){
            this.playSound(lastState!!.soundType.breakSound, 1.0f, 0.8f)
            for(i in 1..10){
                this.level.addParticle(BlockParticleData(ParticleTypes.BLOCK, lastState!!),
                    this.x, this.y, this.z, 1.0, 1.0, 1.0)
            }
        }
        inGround = true
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        buffer.writeVarInt(this.owner!!.id)
        buffer.writeItem(this.baseAxe)
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        val player = this.level.getEntity(additionalData.readVarInt()) as ClientPlayerEntity
        this.owner = player
        var item = player.mainHandItem
        val compare = additionalData.readItem()
        if(!item.sameItem(compare))item = player.offhandItem
        this.baseAxe = item
        val cap = item.getCapability(WorldAxeCapability.itemEntityConnectionHandler)
        cap.resolve().get().setEntity(this)
    }

    fun shouldRemove(player: PlayerEntity): Boolean {
        val itemstack: ItemStack = player.mainHandItem
        val itemstack1: ItemStack = player.offhandItem
        val flag = itemstack.item === TCombatItems.WORLD_AXE.get()
        val flag1 = itemstack1.item === TCombatItems.WORLD_AXE.get()
        return if(player.isAlive && (flag || flag1) && this.distanceToSqr(player) <= 100 * 100) {
            false
        } else {
            this.remove()
            true
        }
    }
}