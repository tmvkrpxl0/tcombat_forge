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
import net.minecraft.util.HandSide
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.vector.Matrix4f
import net.minecraft.util.math.vector.Vector3d
import net.minecraft.util.math.vector.Vector3f

class WorldAxeRenderer(private val renderManager: EntityRendererManager) :
    EntityRenderer<WorldAxeEntity>(renderManager) {
    override fun getTextureLocation(entity: WorldAxeEntity): ResourceLocation = AtlasTexture.LOCATION_BLOCKS

    override fun render(
        worldAxeEntity: WorldAxeEntity,
        yaw: Float,
        partialTicks: Float,
        matrixStack: MatrixStack,
        bufferIn: IRenderTypeBuffer,
        packedLightIn: Int
    ) {
        matrixStack.pushPose()
        val world = worldAxeEntity.level
        val itemStack = worldAxeEntity.getBaseAxe()
        val itemRenderer = Minecraft.getInstance().itemRenderer ///////////////////////Render axe start
        matrixStack.pushPose()
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180 - worldAxeEntity.yRot + 90))
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(worldAxeEntity.xRot - 20))
        matrixStack.scale(1.5f, 1.5f, 1.5f)
        val iBakedModel = itemRenderer.getModel(itemStack, world, null)
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
        //////////////////////Render line start
        val playerentity = worldAxeEntity.owner as PlayerEntity
        matrixStack.pushPose()
        val lookVec = worldAxeEntity.lookAngle
        matrixStack.translate(lookVec.x, lookVec.y, lookVec.z)
        matrixStack.scale(0.5f, 0.5f, 0.5f)
        matrixStack.mulPose(renderManager.cameraOrientation())
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0f))
        matrixStack.popPose()
        var lvt_12_1_ = if(playerentity.mainArm == HandSide.RIGHT) 1 else -1
        val itemstack: ItemStack = playerentity.mainHandItem
        if(itemstack.item !== TCombatItems.WORLD_AXE.get()) {
            lvt_12_1_ = -lvt_12_1_
        }
        val lvt_14_1_: Float = playerentity.getAttackAnim(partialTicks)
        val lvt_15_1_ = MathHelper.sin(MathHelper.sqrt(lvt_14_1_) * 3.1415927f)
        val lvt_16_1_ =
            (MathHelper.lerp(partialTicks, playerentity.yBodyRotO, playerentity.yBodyRot) * 0.017453292f).toFloat()
        val lvt_17_1_ = MathHelper.sin(lvt_16_1_).toDouble()
        val lvt_19_1_ = MathHelper.cos(lvt_16_1_).toDouble()
        val lvt_21_1_ = lvt_12_1_.toDouble() * 0.35
        val lvt_25_2_: Double
        val lvt_27_2_: Double
        val lvt_29_2_: Double
        val lvt_31_2_: Float
        var lvt_32_2_: Double
        if((entityRenderDispatcher.options == null || entityRenderDispatcher.options.cameraType.isFirstPerson) && playerentity === Minecraft.getInstance().player) {
            lvt_32_2_ = entityRenderDispatcher.options.fov
            lvt_32_2_ /= 100.0
            var lvt_34_1_ = Vector3d(lvt_12_1_.toDouble() * -0.36 * lvt_32_2_, -0.045 * lvt_32_2_, 0.4)
            lvt_34_1_ = lvt_34_1_.xRot(
                (-MathHelper.lerp(
                    partialTicks, playerentity.xRotO, playerentity.xRot
                ) * 0.017453292f)
            )
            lvt_34_1_ = lvt_34_1_.yRot(
                (-MathHelper.lerp(
                    partialTicks, playerentity.yRotO, playerentity.yRot
                ) * 0.017453292f)
            )
            lvt_34_1_ = lvt_34_1_.yRot(lvt_15_1_ * 0.5f)
            lvt_34_1_ = lvt_34_1_.xRot(-lvt_15_1_ * 0.7f)
            lvt_25_2_ = MathHelper.lerp(partialTicks.toDouble(), playerentity.xo, playerentity.getX()) + lvt_34_1_.x
            lvt_27_2_ = MathHelper.lerp(partialTicks.toDouble(), playerentity.yo, playerentity.getY()) + lvt_34_1_.y
            lvt_29_2_ = MathHelper.lerp(partialTicks.toDouble(), playerentity.zo, playerentity.getZ()) + lvt_34_1_.z
            lvt_31_2_ = playerentity.getEyeHeight()
        } else {
            lvt_25_2_ = MathHelper.lerp(
                partialTicks.toDouble(), playerentity.xo, playerentity.x
            ) - lvt_19_1_ * lvt_21_1_ - lvt_17_1_ * 0.8
            lvt_27_2_ =
                playerentity.yo + playerentity.eyeHeight.toDouble() + (playerentity.y - playerentity.yo) * partialTicks.toDouble() - 0.45
            lvt_29_2_ = MathHelper.lerp(
                partialTicks.toDouble(), playerentity.zo, playerentity.z
            ) - lvt_17_1_ * lvt_21_1_ + lvt_19_1_ * 0.8
            lvt_31_2_ = if(playerentity.isCrouching) -0.1875f else 0.0f
        }

        lvt_32_2_ = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.xo, worldAxeEntity.x)
        val lvt_34_2_ = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.yo, worldAxeEntity.y) + 0.25
        val lvt_36_1_ = MathHelper.lerp(partialTicks.toDouble(), worldAxeEntity.zo, worldAxeEntity.z)
        val lvt_38_1_ = (lvt_25_2_ - lvt_32_2_).toFloat()
        val lvt_39_1_: Float = (lvt_27_2_ - lvt_34_2_).toFloat() + lvt_31_2_
        val lvt_40_1_ = (lvt_29_2_ - lvt_36_1_).toFloat()
        val lvt_41_1_: IVertexBuilder = bufferIn.getBuffer(RenderType.lines())
        val lvt_42_1_: Matrix4f = matrixStack.last().pose()
        val lvt_43_1_: Int = 1

        for(lvt_44_1_ in 0..15) {
            stringVertex(
                lvt_38_1_, lvt_39_1_, lvt_40_1_, lvt_41_1_, lvt_42_1_, fraction(lvt_44_1_, 16)
            )
            stringVertex(
                lvt_38_1_, lvt_39_1_, lvt_40_1_, lvt_41_1_, lvt_42_1_, fraction(lvt_44_1_ + 1, 16)
            )
        }

        matrixStack.popPose() ////////////////////Render line stop
        super.render(worldAxeEntity, yaw, partialTicks, matrixStack, bufferIn, packedLightIn)
    }

    private fun fraction(p_229105_0_: Int, p_229105_1_: Int): Float {
        return p_229105_0_.toFloat() / p_229105_1_.toFloat()
    }

    private fun stringVertex(
        p_229104_0_: Float,
        p_229104_1_: Float,
        p_229104_2_: Float,
        p_229104_3_: IVertexBuilder,
        p_229104_4_: Matrix4f,
        p_229104_5_: Float
    ) {
        p_229104_3_.vertex(
            p_229104_4_,
            p_229104_0_ * p_229104_5_,
            p_229104_1_ * (p_229104_5_ * p_229104_5_ + p_229104_5_) * 0.5f + 0.25f,
            p_229104_2_ * p_229104_5_
        ).color(0, 0, 0, 255).endVertex()
    }

}