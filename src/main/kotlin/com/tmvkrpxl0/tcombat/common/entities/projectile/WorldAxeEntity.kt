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
import net.minecraft.particles.RedstoneParticleData
import net.minecraft.util.math.*
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.vector.Vector3f
import net.minecraft.world.World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
import net.minecraftforge.fml.network.NetworkHooks

class WorldAxeEntity : ProjectileEntity, IEntityAdditionalSpawnData {
    companion object{
        val HOOKED: DataParameter<Int> = EntityDataManager.defineId(WorldAxeEntity::class.java, DataSerializers.INT)
        val ZROT:DataParameter<Float> = EntityDataManager.defineId(WorldAxeEntity::class.java, DataSerializers.FLOAT)
    }
    var inGround = false
    private var lastState: BlockState? = null
    lateinit var baseAxe: ItemStack
    private var hooked: LivingEntity? = null

    constructor(entityType: EntityType<WorldAxeEntity>, world: World) : super(entityType, world)

    constructor(player: PlayerEntity, baseItem:ItemStack):this(TCombatEntityTypes.WORLD_AXE.get(), player.level){
        this.owner = player
        this.baseAxe = baseItem
        val lookAngle = player.lookAngle
        this.absMoveTo(player.x + lookAngle.x,
            player.eyeY - (this.dimensions.height*2/3) + lookAngle.y,
            player.z + lookAngle.z,
            player.yRot,
            0F
        )
        this.yRot += 90
        this.isNoGravity = false
        this.setzRot(90 - player.xRot)
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
        this.entityData.define(ZROT, 0f)
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
        if(this.level.isClientSide){
            val particle = RedstoneParticleData(1f, 0f, 0f, 1f)
            val position = this.position()
            val lookAnglef = Vector3f(lookAngle.x.toFloat(), lookAngle.y.toFloat(), lookAngle.z.toFloat())
            for(i in 1 until 11){
                val offset = Vector3f.YP.copy()
                offset.transform(Vector3f.XP.rotationDegrees(xRot))
                offset.transform(Quaternion(lookAnglef, this.getzRot()-90, true))
                offset.mul((0.1 * i).toFloat())
                this.level.addParticle(particle, position.x + offset.x(), position.y + offset.y(), position.z + offset.z(),
                    0.0, 0.0, 0.0)
            }
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

    fun setzRot(zRot: Float){
        this.entityData.set(ZROT, zRot)
    }

    fun getzRot(): Float{
        return this.entityData.get(ZROT)
    }
}