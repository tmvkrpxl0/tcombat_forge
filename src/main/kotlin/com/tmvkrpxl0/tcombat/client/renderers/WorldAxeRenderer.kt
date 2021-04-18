package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import com.tmvkrpxl0.tcombat.common.entities.projectile.WorldAxeEntity
import com.tmvkrpxl0.tcombat.common.items.TCombatItems
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
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
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.vector.Vector3f

class WorldAxeRenderer(rendererManager: EntityRendererManager) : EntityRenderer<WorldAxeEntity>(rendererManager) {

    override fun getEntityTexture(entity: WorldAxeEntity): ResourceLocation = AtlasTexture.LOCATION_BLOCKS_TEXTURE

    override fun render(worldAxeEntity: WorldAxeEntity, yaw: Float, partialTicks: Float, matrixStack: MatrixStack, bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        matrixStack.push()
        val world = worldAxeEntity.world
        val itemStack = worldAxeEntity.getBaseAxe()
        val itemRenderer = Minecraft.getInstance().itemRenderer

        ///////////////////////Render axe start
        matrixStack.push()
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180 - worldAxeEntity.rotationYaw + 90))
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(worldAxeEntity.rotationPitch - 20))
        matrixStack.scale(1.5f, 1.5f, 1.5f)
        val iBakedModel = itemRenderer.getItemModelWithOverrides(itemStack, world, null)
        itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.GROUND, false, matrixStack, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, iBakedModel)
        matrixStack.pop()
        ///////////////////////Render axe stop

        //////////////////////Render line start
        val playerentity = worldAxeEntity.shooter as PlayerEntity
        matrixStack.push()
        val lookVec = worldAxeEntity.lookVec
        matrixStack.translate(lookVec.x, lookVec.y, lookVec.z)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.rotate(renderManager.cameraOrientation)
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0f))
        matrixStack.pop()
        var i = if(playerentity.primaryHand == HandSide.RIGHT) 1 else -1
        val itemstack: ItemStack = playerentity.heldItemMainhand
        if(itemstack.item !== TCombatItems.WORLD_AXE.get()) {
            i = -i
        }
        val f: Float = playerentity.getSwingProgress(partialTicks)
        val f1 = MathHelper.sin(MathHelper.sqrt(f) * Math.PI.toFloat())
        val f2 = MathHelper.lerp(
            partialTicks,
            playerentity.prevRenderYawOffset,
            playerentity.renderYawOffset
        ) * (Math.PI.toFloat() / 180f)
        val d0 = MathHelper.sin(f2).toDouble()
        val d1 = MathHelper.cos(f2).toDouble()
        val d2 = i.toDouble() * 0.35
        val d4: Double
        val d5: Double
        val d6: Double
        val f3: Float
        if((renderManager.options == null || renderManager.options.pointOfView.func_243192_a()) && playerentity === Minecraft.getInstance().player) {
            var d7 = renderManager.options.fov
            d7 /= 100.0
            var vector3d = Vector3d(i.toDouble() * -0.36 * d7, -0.045 * d7, 0.4)
            vector3d = vector3d.rotatePitch(
                -MathHelper.lerp(
                    partialTicks,
                    playerentity.prevRotationPitch,
                    playerentity.rotationPitch
                ) * (Math.PI.toFloat() / 180f)
            )
            vector3d = vector3d.rotateYaw(
                -MathHelper.lerp(
                    partialTicks,
                    playerentity.prevRotationYaw,
                    playerentity.rotationYaw
                ) * (Math.PI.toFloat() / 180f)
            )
            vector3d = vector3d.rotateYaw(f1 * 0.5f)
            vector3d = vector3d.rotatePitch(-f1 * 0.7f)
            d4 = MathHelper.lerp(partialTicks.toDouble(), playerentity.prevPosX, playerentity.getPosX()) + vector3d.x
            d5 = MathHelper.lerp(partialTicks.toDouble(), playerentity.prevPosY, playerentity.getPosY()) + vector3d.y
            d6 = MathHelper.lerp(partialTicks.toDouble(), playerentity.prevPosZ, playerentity.getPosZ()) + vector3d.z
            f3 = playerentity.getEyeHeight()
        } else {
            d4 = MathHelper.lerp(
                partialTicks.toDouble(),
                playerentity.prevPosX,
                playerentity.posX
            ) - d1 * d2 - d0 * 0.8
            d5 =
                playerentity.prevPosY + playerentity.eyeHeight.toDouble() + (playerentity.posY - playerentity.prevPosY) * partialTicks.toDouble() - 0.45
            d6 = MathHelper.lerp(
                partialTicks.toDouble(),
                playerentity.prevPosZ,
                playerentity.posZ
            ) - d0 * d2 + d1 * 0.8
            f3 = if(playerentity.isCrouching) -0.1875f else 0.0f
        }
        val d9 = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.prevPosX, worldAxeEntity.posX)
        val d10 = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.prevPosY, worldAxeEntity.posY) + 0.25
        val d8 = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.prevPosZ, worldAxeEntity.posZ)
        val f4 = (d4 - d9).toFloat()
        val f5 = (d5 - d10).toFloat() + f3
        val f6 = (d6 - d8).toFloat()
        val ivertexbuilder1 = bufferIn.getBuffer(RenderType.getLines())
        val matrix4f1: Matrix4f = matrixStack.last.matrix
        val j = 16

        for(k in 0..15) {
            func_229104_a_(f4, f5, f6, ivertexbuilder1, matrix4f1, func_229105_a_(k, 16))
            func_229104_a_(f4, f5, f6, ivertexbuilder1, matrix4f1, func_229105_a_(k + 1, 16))
        }

        matrixStack.pop()
        ////////////////////Render line stop
        super.render(worldAxeEntity, yaw, partialTicks, matrixStack, bufferIn, packedLightIn)
    }

    private fun func_229105_a_(p_229105_0_: Int, p_229105_1_: Int): Float {
        return p_229105_0_.toFloat() / p_229105_1_.toFloat()
    }

    private fun func_229104_a_(
        p_229104_0_: Float,
        p_229104_1_: Float,
        p_229104_2_: Float,
        p_229104_3_: IVertexBuilder,
        p_229104_4_: Matrix4f,
        p_229104_5_: Float
    ) {
        p_229104_3_.pos(
            p_229104_4_,
            p_229104_0_ * p_229104_5_,
            p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5f + 0.25f,
            p_229104_2_ * p_229104_5_
        ).color(0, 0, 0, 255).endVertex()
    }

}