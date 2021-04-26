package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableFluidEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.ForgeHooksClient
import javax.annotation.Nonnull

class FluidEntityRenderer(renderManager: EntityRendererManager) : EntityRenderer<CustomizableFluidEntity>(renderManager) {
    override fun render(entityIn: CustomizableFluidEntity, entityYaw: Float, partialTicks: Float, @Nonnull matrixStackIn: MatrixStack, @Nonnull bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        val fluidState = entityIn.getFluidState()
        val blockPos = BlockPos(entityIn.x, entityIn.boundingBox.maxY, entityIn.z)
        if (!fluidState.isEmpty) {
            val world = entityIn.level
            val blockRendererDispatcher = Minecraft.getInstance().blockRenderer
            matrixStackIn.pushPose()
            matrixStackIn.translate((blockPos.x and 15).toDouble(), (blockPos.y and 15).toDouble(), (blockPos.z and 15).toDouble())
            for (type in RenderType.chunkBufferLayers()) {
                if (RenderTypeLookup.canRenderInLayer(fluidState, type)) {
                    ForgeHooksClient.setRenderLayer(type)
                    blockRendererDispatcher.renderLiquid(blockPos, world, bufferIn.getBuffer(type), fluidState)
                }
            }
            ForgeHooksClient.setRenderLayer(null)
            matrixStackIn.popPose()
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
        }
    }

    @Nonnull
    override fun getTextureLocation(@Nonnull entity: CustomizableFluidEntity): ResourceLocation {
        return AtlasTexture.LOCATION_BLOCKS
    }
}