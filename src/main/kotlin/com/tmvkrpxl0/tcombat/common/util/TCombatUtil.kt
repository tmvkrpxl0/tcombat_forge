package com.tmvkrpxl0.tcombat.common.util

import com.google.common.primitives.Doubles
import com.tmvkrpxl0.tcombat.common.capability.capabilities.TargetCapability
import com.tmvkrpxl0.tcombat.common.capability.factories.ITargetHolder
import com.tmvkrpxl0.tcombat.common.network.packets.CBSizeRequestPacket
import com.tmvkrpxl0.tcombat.common.network.packets.TCombatPacketHandler
import net.minecraft.block.BlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.vector.Vector2f
import net.minecraft.util.math.vector.Vector3d
import net.minecraftforge.client.model.pipeline.LightUtil
import net.minecraftforge.fml.common.thread.SidedThreadGroups
import net.minecraftforge.fml.network.PacketDistributor
import org.apache.logging.log4j.util.TriConsumer
import java.util.*
import java.util.function.BiConsumer
import javax.annotation.Nonnull
import kotlin.math.absoluteValue
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min

object TCombatUtil {
    val AABB_100: AxisAlignedBB = AxisAlignedBB.ofSize(100.0, 100.0, 100.0)
    private val cbRequests = HashMap<UUID, TriConsumer<UUID, Float, Float>>()
    fun getEntityVectorAngle(@Nonnull from: Entity, @Nonnull to: Entity, @Nonnull sourceToDestVelocity: Vector3d): Double {
        val sourcePosition = from.position()
        val targetPosition = to.position()
        val difference = targetPosition.subtract(sourcePosition)
        return Math.toDegrees(angle(sourceToDestVelocity, difference).toDouble())
    }

    fun getEntityToEntityAngle(@Nonnull from: Entity, @Nonnull to: Entity): Double {
        return getEntityVectorAngle(from, to, from.deltaMovement)
    }

    fun angle(@Nonnull a: Vector3d, b: Vector3d): Float {
        val dot = Doubles.constrainToRange(a.dot(b) / (a.length() * b.length()), -1.0, 1.0)
        return acos(dot).toFloat()
    }

    @Nonnull
    fun getTargets(@Nonnull player: PlayerEntity): List<LivingEntity> = this.getTargetHolder(player).getTargets()

    fun setTargets(@Nonnull player: PlayerEntity, @Nonnull list: List<LivingEntity>) = this.getTargetHolder(player).setTargets(list)

    fun setTargets(@Nonnull player: PlayerEntity){
            val targets = LinkedList<LivingEntity>()
            val degree = 60
            val playerDirection: Vector3d = player.lookAngle
            val playerEyeVector: Vector3d = player.getEyePosition(1f)
            var size = 0
            for(e in player.level.getEntities(player, AxisAlignedBB.ofSize(100.0, 100.0, 100.0).move(player.position()))) {
                if(e !is LivingEntity) continue
                if(!player.canSee(e)) continue
                val entityVector: Vector3d = e.position()
                val difference: Vector3d = entityVector.subtract(playerEyeVector)
                val angleDifference =
                    Math.toDegrees(angle(playerDirection, difference).toDouble())
                if(angleDifference <= degree) {
                    targets.add(e)
                    size++
                    if(size == 100) break
                }
            }
            setTargets(player, targets)
    }

    fun getReflect(@Nonnull v1: Vector3d, @Nonnull surface: Vector3d): Vector3d {
        val normal = surface.normalize()
        val dot2 = v1.dot(normal) * 2
        return v1.subtract(normal.scale(dot2))
    }

    //v1.x*v2.x + v1.y*v2.y + v1.z*v2.z = 0
    //v1.z*v2.z = - v1.x*v2.x - v1.y*v2.y
    //v2.z = (- v1.x*v2.x - v1.y*v2.y) / v1.z
    fun getPerpendicularRandom(@Nonnull v1: Vector3d, @Nonnull random: Random): Vector3d{
        val v2x = random.nextDouble() * v1.x
        val v2y = random.nextDouble() * v1.y
        val v2z = (-v2x -v2y) / v1.z
        return Vector3d(v2x, v2y, v2z).normalize()
    }

    fun getPickMode(@Nonnull player: PlayerEntity): TargetCapability.PickMode = this.getTargetHolder(player).getPickMode()

    fun setPickMode(@Nonnull player: PlayerEntity, @Nonnull mode: TargetCapability.PickMode) = this.getTargetHolder(player).setPickMode(mode)

    @Nonnull
    private fun getTargetHolder(@Nonnull player: PlayerEntity): ITargetHolder {
        val cap = player.getCapability(TargetCapability.TARGET_HANDLER)
        if(cap.resolve().isPresent) {
            return cap.resolve().get()
        }
        throw IllegalStateException("Player must have Target Capability!")
    }

    @Nonnull
    fun pickTarget(@Nonnull player: PlayerEntity): LivingEntity?{
        when(this.getPickMode(player)){
            TargetCapability.PickMode.CLOSE->{
                val entities = player.level.getEntities(player, AABB_100) { entity: Entity -> entity is LivingEntity && entity.isAlive && entity.distanceToSqr(player) < 100 * 100 }
                var distance = Double.MAX_VALUE
                var close: LivingEntity? = null
                for(e in entities){
                    if(e.distanceToSqr(player) < distance){
                        distance = e.distanceToSqr(player)
                        close = e as LivingEntity
                    }
                }
                return close
            }
            TargetCapability.PickMode.LOOK->{
                val entities = player.level.getEntities(player, AABB_100) { entity: Entity -> entity is LivingEntity && entity.isAlive && entity.distanceToSqr(player) < 100 * 100 }
                var angle = 360.0
                var looking: LivingEntity? = null
                for(e in entities){
                    val temp = this.getEntityToEntityAngle(player, e)
                    if(temp < angle){
                        angle = temp
                        looking = e as LivingEntity
                    }
                }
                return looking
            }
            TargetCapability.PickMode.RANDOM->{
                val entities = player.level.getEntities(player, AABB_100) { entity: Entity -> entity is LivingEntity && entity.isAlive && entity.distanceToSqr(player) < 100 * 100 }
                val random = player.random.nextInt().absoluteValue
                return entities[random % entities.size] as LivingEntity
            }
        }
    }

    @Nonnull
    fun getFocus(@Nonnull player: PlayerEntity): LivingEntity? = this.getTargetHolder(player).getFocused()

    @Nonnull
    fun setFocus(@Nonnull player: PlayerEntity, focused: LivingEntity?) = this.getTargetHolder(player).setFocused(focused)

    fun updateTargets(@Nonnull player: PlayerEntity) = this.getTargetHolder(player).update()

    fun requestCB(player: ServerPlayerEntity, blockState: BlockState, consumer: TriConsumer<UUID, Float, Float>){
        val uuid = UUID.randomUUID()
        cbRequests[uuid] = consumer
        val request = CBSizeRequestPacket(blockState, uuid)
        TCombatPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with { player }, request)
    }

    fun isRequested(uuid: UUID): Boolean = cbRequests.containsKey(uuid)

    fun getBlockSize(blockState: BlockState): Vector2f{
        val blockRenderer =  Minecraft.getInstance().blockRenderer
        val model = blockRenderer.getBlockModel(blockState)
        var xMin = 1.0f
        var xMax = 0.0f
        var yMin = 1.0f
        var yMax = 0.0f
        var zMin = 1.0f
        var zMax = 0.0f
        for(d in CBSizeRequestPacket.sides){
            val quads = model.getQuads(blockState, d, CBSizeRequestPacket.random)
            val unpacked = Array(4) { Array(6) { FloatArray(4) } }
            for(q in quads){
                val vertices = q.vertices
                for(v in 0 until 4){
                    LightUtil.unpack(vertices, unpacked[v][0], DefaultVertexFormats.BLOCK, v, 0)
                    for(i in 0 until 4){
                        val vertex = unpacked[v][0]
                        xMin = min(xMin, vertex[0])
                        xMax = max(xMax, vertex[0])
                        yMin = min(yMin, vertex[1])
                        yMax = max(yMax, vertex[1])
                        zMin = min(zMin, vertex[2])
                        zMax = max(zMax, vertex[2])
                    }

                }
            }
        }
        xMin = max(0.0f, xMin)
        xMax = min(1.0f, xMax)
        yMin = max(0.0f, yMin)
        yMax = min(1.0f, yMax)
        zMin = max(0.0f, zMin)
        zMax = min(1.0f, zMax)
        return Vector2f(xMax - xMin, yMax - yMin)
    }

    fun handleRequest(uuid: UUID, x: Float, y: Float){
        require(Thread.currentThread().threadGroup == SidedThreadGroups.SERVER) { "Server only method!" }
        require(cbRequests.containsKey(uuid)) { "Cannot find Request! uuid: $uuid" }
        val handler = cbRequests[uuid]!!
        handler.accept(uuid, x, y)
        cbRequests.remove(uuid)
    }
}