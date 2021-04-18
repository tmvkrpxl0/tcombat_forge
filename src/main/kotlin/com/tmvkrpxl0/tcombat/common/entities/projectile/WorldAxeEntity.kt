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
import net.minecraft.item.Items
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
import net.minecraftforge.fml.server.ServerLifecycleHooks

class WorldAxeEntity : ProjectileEntity, IEntityAdditionalSpawnData {
    var inGround = false
    var inBlockState: BlockState? = null
    companion object {
        val BASE_AXE: DataParameter<ItemStack> = EntityDataManager.createKey(WorldAxeEntity::class.java, DataSerializers.ITEMSTACK)
    }

    constructor(entityType: EntityType<WorldAxeEntity>, world: World) : super(entityType, world)

    constructor(player: PlayerEntity, baseItem:ItemStack):this(TCombatEntityTypes.WORLD_AXE.get(), player.world){
        this.shooter = player
        this.setBaseAxe(baseItem)
        this.setPositionAndRotation(player.posX, player.posYEye, player.posZ, player.rotationYaw, player.rotationPitch)
        this.setNoGravity(false)
    }

    override fun registerData() {
        this.dataManager.register(BASE_AXE, ItemStack(TCombatItems.WORLD_AXE.get()))
    }

    override fun readAdditional(compound: CompoundNBT) {
        this.setBaseAxe(ItemStack.read(compound.getCompound("BaseAxe")))
        this.inGround = compound.getBoolean("inGround")
        super.writeAdditional(compound)
    }

    override fun writeAdditional(compound: CompoundNBT) {
        val c = CompoundNBT()
        compound.put("BaseAxe", this.getBaseAxe().write(c))
        compound.putBoolean("inGround", this.inGround)
        super.writeAdditional(compound)
    }

    fun setBaseAxe(baseItem: ItemStack) = this.dataManager.set(BASE_AXE, baseItem)

    fun getBaseAxe(): ItemStack = this.dataManager.get(BASE_AXE)

    override fun createSpawnPacket(): IPacket<*> = NetworkHooks.getEntitySpawningPacket(this)

    override fun remove() {
        val cap = this.getBaseAxe().getCapability(ItemEntityConnectionCapability.ITEM_ENTITY_CONNECTION_HANDLER)
        if(cap.resolve().isPresent){
            val entityHolder = cap.resolve().get() as EntityHolder
            entityHolder.setEntity(null)
        }
        super.remove()
    }

    override fun tick() {
        if(this.shooter == null || !this.shooter!!.isAlive || shouldRemove(this.shooter as PlayerEntity)){
            this.remove()
        }
        var vector3d = motion
        if(prevRotationPitch == 0.0f && prevRotationYaw == 0.0f) {
            val f = MathHelper.sqrt(horizontalMag(vector3d))
            rotationYaw = (MathHelper.atan2(vector3d.x, vector3d.z) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
            rotationPitch =
                (MathHelper.atan2(vector3d.y, f.toDouble()) * (180f / Math.PI.toFloat()).toDouble()).toFloat()
            prevRotationYaw = rotationYaw
            prevRotationPitch = rotationPitch
        }
        val blockpos = position
        val blockstate = world.getBlockState(blockpos)
        if(!blockstate.isAir(world, blockpos)) {
            val voxelshape = blockstate.getCollisionShapeUncached(world, blockpos)
            if(!voxelshape.isEmpty) {
                val vector3d1 = positionVec
                for(axisalignedbb in voxelshape.toBoundingBoxList()) {
                    if(axisalignedbb.offset(blockpos).contains(vector3d1)) {
                        inGround = true
                        break
                    }
                }
            }
        }
        if(this.isWet) {
            extinguish()
        }
        if(inGround) {
            if(inBlockState !== blockstate && this.shouldFall()) {
                this.fall()
            }
        } else {
            val vector3d2 = positionVec
            var vector3d3 = vector3d2.add(vector3d)
            var raytraceresult: RayTraceResult? = world.rayTraceBlocks(
                RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this)
            )
            if(raytraceresult!!.type != RayTraceResult.Type.MISS) {
                vector3d3 = raytraceresult.hitVec
            }
            while(!removed) {
                var entityraytraceresult = rayTraceEntities(vector3d2, vector3d3)
                if(entityraytraceresult != null) {
                    raytraceresult = entityraytraceresult
                }
                if(raytraceresult != null && raytraceresult.type == RayTraceResult.Type.ENTITY) {
                    val entity = (raytraceresult as EntityRayTraceResult).entity
                    val entity1 = this.shooter
                    if(entity is PlayerEntity && entity1 is PlayerEntity && !entity1.canAttackPlayer(entity as PlayerEntity)) {
                        raytraceresult = null
                        entityraytraceresult = null
                    }
                }
                if(raytraceresult != null && raytraceresult.type != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(
                        this, raytraceresult
                    )
                ) {
                    onImpact(raytraceresult)
                    isAirBorne = true
                }
                if(entityraytraceresult == null) {
                    break
                }
                raytraceresult = null
            }
            vector3d = motion
            val d3 = vector3d.x
            val d4 = vector3d.y
            val d0 = vector3d.z
            val d5 = this.posX + d3
            val d1 = this.posY + d4
            val d2 = this.posZ + d0
            val f1 = MathHelper.sqrt(horizontalMag(vector3d))
            rotationPitch = (MathHelper.atan2(d4, f1.toDouble()) * (180/ Math.PI)).toFloat()
            rotationPitch = func_234614_e_(prevRotationPitch, rotationPitch)
            var f2 = 0.99f
            if(this.isInWater) {
                f2 = getWaterDrag()
            }
            motion = vector3d.scale(f2.toDouble())
            if(!hasNoGravity()) {
                val vector3d4 = motion
                this.setMotion(vector3d4.x, vector3d4.y - 0.05, vector3d4.z)
            }
            setPosition(d5, d1, d2)
            doBlockCollisions()
        }
        super.tick()
    }

    private fun shouldFall(): Boolean {
        return inGround && world.hasNoCollisions(AxisAlignedBB(positionVec, positionVec).grow(0.06))
    }

    private fun fall() {
        inGround = false
        val vector3d = motion
        motion = vector3d.mul(
            (rand.nextFloat() * 0.2f).toDouble(),
            (rand.nextFloat() * 0.2f).toDouble(),
            (rand.nextFloat() * 0.2f).toDouble()
        )
    }

    fun rayTraceEntities(startVec: Vector3d, endVec: Vector3d): EntityRayTraceResult? {
        return ProjectileHelper.rayTraceEntities(world, this, startVec, endVec, boundingBox.expand(motion).grow(1.0)) { entityIn: Entity ->
            func_230298_a_(entityIn)
        }
    }

    fun getWaterDrag(): Float {
        return 0.6f
    }

    override fun func_230299_a_(result: BlockRayTraceResult) {
        inBlockState = world.getBlockState(result.pos)
        super.func_230299_a_(result)
        val vector3d = result.hitVec.subtract(this.posX, this.posY, this.posZ)
        motion = vector3d
        val vector3d1 = vector3d.normalize().scale(0.05f.toDouble())
        setRawPosition(this.posX - vector3d1.x, this.posY - vector3d1.y, this.posZ - vector3d1.z)
        if(inBlockState!=null){
            this.playSound(inBlockState!!.soundType.breakSound, 1.0f, 0.8f)
            for(i in 1..10){
                this.world.addParticle(BlockParticleData(ParticleTypes.BLOCK, inBlockState!!),
                    this.posX, this.posY, this.posZ, 1.0, 1.0, 1.0)
            }
        }
        inGround = true
    }

    override fun writeSpawnData(buffer: PacketBuffer) {
        if(this.shooter!=null)buffer.writeUniqueId(this.shooter!!.uniqueID)
    }

    override fun readSpawnData(additionalData: PacketBuffer) {
        this.shooter = ServerLifecycleHooks.getCurrentServer().playerList.getPlayerByUUID(additionalData.readUniqueId())
    }

    fun shouldRemove(player: PlayerEntity): Boolean {
        val itemstack: ItemStack = player.heldItemMainhand
        val itemstack1: ItemStack = player.heldItemOffhand
        val flag = itemstack.item === TCombatItems.WORLD_AXE.get()
        val flag1 = itemstack1.item === TCombatItems.WORLD_AXE.get()
        return if(player.isAlive && (flag || flag1) && this.getDistanceSq(player) <= 100 * 100) {
            false
        } else {
            this.remove()
            true
        }
    }
}