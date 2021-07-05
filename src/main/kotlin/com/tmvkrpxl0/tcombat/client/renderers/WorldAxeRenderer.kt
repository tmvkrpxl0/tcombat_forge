package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import com.tmvkrpxl0.tcombat.TCombatMain
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.model.IBakedModel
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.HandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.math.vector.Quaternion
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.vector.Vector3f

class WorldAxeRenderer(private val renderManager: EntityRendererManager) :
    EntityRenderer<WorldAxeEntity>(renderManager) {
    companion object{
        private val AXE = ResourceLocation(TCombatMain.MODID, "item/world_axe_axe")
    }
    override fun getTextureLocation(entity: WorldAxeEntity): ResourceLocation = AtlasTexture.LOCATION_BLOCKS

    override fun render(
        worldAxeEntity: WorldAxeEntity,
        yaw: Float,
        partialTicks: Float,
        matrixStack: MatrixStack,
        bufferIn: IRenderTypeBuffer,
        packedLightIn: Int
    ) {
        val itemStack = worldAxeEntity.baseAxe
        val owner = worldAxeEntity.owner!! as PlayerEntity
        val itemRenderer = Minecraft.getInstance().itemRenderer ///////////////////////Render axe start
        matrixStack.pushPose()
        matrixStack.pushPose()
        val lookAngle = worldAxeEntity.lookAngle
        val lookAnglef = Vector3f(lookAngle.x.toFloat(), lookAngle.y.toFloat(), lookAngle.z.toFloat())
        val q = Quaternion(lookAnglef, worldAxeEntity.getzRot(), true)
        matrixStack.translate(0.0, 0.4, 0.0)
        matrixStack.translate(lookAngle.x * 0.2, lookAngle.y * 0.2, lookAngle.z * 0.2)
        matrixStack.mulPose(q)
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90-worldAxeEntity.yRot))
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-45F))
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-worldAxeEntity.xRot))
        matrixStack.scale(1.5f, 1.5f, 1.5f)
        val iBakedModel: IBakedModel = Minecraft.getInstance().modelManager.getModel(AXE)
        itemRenderer.render(
            itemStack,
            ItemCameraTransforms.TransformType.GROUND,
            false,
            matrixStack,
            bufferIn,
            packedLightIn,
            OverlayTexture.NO_OVERLAY,
            iBakedModel
        )
        matrixStack.popPose() ///////////////////////Render axe stop

        var i = if (owner.mainArm == HandSide.RIGHT) 1 else -1
        val mainHand: ItemStack = owner.mainHandItem
        if (mainHand.item != TCombatItems.WORLD_AXE.get()) {
            i = -i
        }

        val animaDuration: Float = owner.getAttackAnim(partialTicks)
        val animaRadian = MathHelper.sin(MathHelper.sqrt(animaDuration) * Math.PI.toFloat())
        val bodyRadian =
            MathHelper.lerp(partialTicks, owner.yBodyRotO, owner.yBodyRot) * (Math.PI.toFloat() / 180f)
        val animaX = MathHelper.sin(bodyRadian).toDouble()
        val animaY = MathHelper.cos(bodyRadian).toDouble()
        val animaZ = i.toDouble() * 0.35
        val rodX: Double
        val rodY: Double
        val rodZ: Double
        val yOffset: Float
        if (entityRenderDispatcher.options.cameraType.isFirstPerson && owner === Minecraft.getInstance().player) {
            var fov = entityRenderDispatcher.options.fov
            fov /= 100.0
            var vector3d = Vector3d(i.toDouble() * -0.5 * fov, 0.02 * fov, 0.325)
            vector3d = vector3d.xRot(-MathHelper.lerp(partialTicks, owner.xRotO, owner.xRot) * (Math.PI.toFloat() / 180f))
            vector3d = vector3d.yRot(-MathHelper.lerp(partialTicks, owner.yRotO, owner.yRot) * (Math.PI.toFloat() / 180f))
            vector3d = vector3d.yRot(animaRadian * 0.5f)
            vector3d = vector3d.xRot(-animaRadian * 0.7f)
            rodX = MathHelper.lerp(partialTicks.toDouble(), owner.xo, owner.getX()) + vector3d.x
            rodY = MathHelper.lerp(partialTicks.toDouble(), owner.yo, owner.getY()) + vector3d.y
            rodZ = MathHelper.lerp(partialTicks.toDouble(), owner.zo, owner.getZ()) + vector3d.z
            yOffset = owner.getEyeHeight()
        } else {
            rodX = MathHelper.lerp(partialTicks.toDouble(), owner.xo, owner.x) - animaY * animaZ - animaX * 0.8
            rodY = owner.yo + owner.eyeHeight.toDouble() + (owner.y - owner.yo) * partialTicks.toDouble() - 0.45
            rodZ = MathHelper.lerp(partialTicks.toDouble(), owner.zo, owner.z) - animaX * animaZ + animaY * 0.8
            yOffset = if (owner.isCrouching) -0.1875f else 0.0f
        }

        val entityX = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.xo, worldAxeEntity.x)
        val entityY = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.yo, worldAxeEntity.y) + 0.25
        val entityZ = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.zo, worldAxeEntity.z)
        val diffX = (rodX - entityX).toFloat()
        val diffY = (rodY - entityY).toFloat() + yOffset
        val diffZ = (rodZ - entityZ).toFloat()
        val lineBuffer: IVertexBuilder = bufferIn.getBuffer(RenderType.lines())
        val linePose = matrixStack.last().pose()
        val j = 16

        for (k in 0 until j) {
            stringVertex(diffX, diffY, diffZ, lineBuffer, linePose, fraction(k, 16))
            stringVertex(diffX, diffY, diffZ, lineBuffer, linePose, fraction(k + 1, 16))
        }
        matrixStack.popPose()
        super.render(worldAxeEntity, yaw, partialTicks, matrixStack, bufferIn, packedLightIn)
    }

    private fun fraction(up: Int, down: Int): Float {
        return up.toFloat() / down.toFloat()
    }

    private fun stringVertex(
        x: Float,
        y: Float,
        z: Float,
        iVertexBuilder: IVertexBuilder,
        pose: Matrix4f,
        fraction: Float
    ) {
        iVertexBuilder
            .vertex(pose, x * fraction, y * (fraction * fraction + fraction) * 0.5f + 0.25f, z * fraction)
            .color(0, 0, 0, 255).endVertex()
    }

}