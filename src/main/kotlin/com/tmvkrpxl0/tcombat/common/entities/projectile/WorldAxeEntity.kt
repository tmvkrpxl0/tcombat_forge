package com.tmvkrpxl0.tcombat.common.entities.projectile

import com.tmvkrpxl0.tcombat.common.capability.capabilities.ItemEntityConnectionCapability
import com.tmvkrpxl0.tcombat.common.capability.factories.EntityHolder
import com.tmvkrpxl0.tcombat.common.entities.TCombatEntityTypes
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.block.BlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
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
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.*
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks
import net.minecraftforge.fml.server.ServerLifecycleHooks

class WorldAxeEntity : ProjectileEntity, IEntityAdditionalSpawnData {
    var inGround = false
    var lastState: BlockState? = null
    companion object {
        val BASE_AXE: DataParameter<ItemStack> = EntityDataManager.defineId(WorldAxeEntity::class.java, DataSerializers.ITEM_STACK)
    }

    constructor(entityType: EntityType<WorldAxeEntity>, world: World) : super(entityType, world)

    constructor(player: PlayerEntity, baseItem:ItemStack):this(TCombatEntityTypes.WORLD_AXE.get(), player.level){
        this.owner = player
        this.setBaseAxe(baseItem)
        this.absMoveTo(player.x, player.eyeY, player.z, player.yRot, player.xRot)
        this.setNoGravity(false)
    }

    override fun defineSynchedData() {
        this.entityData.define(BASE_AXE, ItemStack(TCombatItems.WORLD_AXE.get()))
    }

    override fun readAdditionalSaveData(compound: CompoundNBT) {
        this.setBaseAxe(ItemStack.of(compound.getCompound("BaseAxe")))
        this.inGround = compound.getBoolean("inGround")
        super.readAdditionalSaveData(compound)
    }

    override fun addAdditionalSaveData(compound: CompoundNBT) {
        val c = CompoundNBT()
        compound.put("BaseAxe", this.getBaseAxe().save(c))
        compound.putBoolean("inGround", this.inGround)
        super.addAdditionalSaveData(compound)
    }

    fun setBaseAxe(baseItem: ItemStack) = this.entityData.set(BASE_AXE, baseItem)

    fun getBaseAxe(): ItemStack = this.entityData.get(BASE_AXE)

    override fun getAddEntityPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)

    override fun remove() {
        val cap = this.getBaseAxe().getCapability(ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER)
        if(cap.resolve().isPresent){
            val entityHolder = cap.resolve().get() as EntityHolder
            entityHolder.setEntity(null)
        }
        super.remove()
    }

    override fun tick() {
        if(this.owner == null || !this.owner!!.isAlive || shouldRemove(this.owner as PlayerEntity)){
            this.remove()
        }
        var vector3d = this.deltaMovement
        if(xRotO == 0.0f && yRotO == 0.0f) {
            val f = MathHelper.sqrt(getHorizontalDistanceSqr(vector3d))
            yRot = (MathHelper.atan2(vector3d.x, vector3d.z) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
            xRot =
                (MathHelper.atan2(vector3d.y, f.toDouble()) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
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
        } else {
            val vector3d2 = this.position()
            var vector3d3 = vector3d2.add(vector3d)
            var raytraceresult: RayTraceResult? = this.level.clip(
                RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)
            )
            if(raytraceresult!!.type != RayTraceResult.Type.MISS) {
                vector3d3 = raytraceresult.location
            }
            while(!removed) {
                var entityraytraceresult = findHitEntity(vector3d2, vector3d3)
                if(entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult
                }
                if(raytraceresult != null && raytraceresult.type == RayTraceResult.Type.ENTITY) {
                    val entity = (raytraceresult as EntityRayTraceResult).entity
                    val entity1 = this.owner
                    if(entity is PlayerEntity && entity1 is PlayerEntity && !entity1.canHarmPlayer(entity)) {
                        raytraceresult = null
                        entityraytraceresult = null
                    }
                }
                if(raytraceresult != null && raytraceresult.type != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onHit(raytraceresult)
                    this.hasImpulse = true
                }
                if(entityraytraceresult == null) {
                    break
                }
                raytraceresult = null
            }
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
            if(!isNoGravity()) {
                val vector3d4 = this.deltaMovement
                this.setDeltaMovement(vector3d4.x, vector3d4.y - 0.05, vector3d4.z)
            }
            this.setPos(d5, d1, d2)
           this.checkInsideBlocks()
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

    fun findHitEntity(startVec: Vector3d, endVec: Vector3d): EntityRayTraceResult? {
        return ProjectileHelper.getEntityHitResult(this.level, this, startVec, endVec, boundingBox.expandTowards(this.deltaMovement).inflate(1.0)) { entityIn: Entity ->
            canHitEntity(entityIn)
        }
    }

    fun getWaterInertia(): Float {
        return 0.6f
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
        if(this.owner!=null)buffer.writeUUID(this.owner!!.uuid)
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        this.owner = ServerLifecycleHooks.getCurrentServer().playerList.getPlayer(additionalData.readUUID())
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