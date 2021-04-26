package com.tmvkrpxl0.tcombat.client.renderers

import com.mojang.blaze3d.matrix.MatrixStack
import com.tmvkrpxl0.tcombat.common.entities.misc.CustomizableBlockEntity
import net.minecraft.block.BlockRenderType
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererManager
import net.minecraft.client.renderer.texture.AtlasTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraftforge.client.ForgeHooksClient
import java.util.*
import javax.annotation.Nonnull

class BlockEntityRenderer(renderManager: EntityRendererManager) : EntityRenderer<CustomizableBlockEntity>(renderManager) {
    init {
        this.shadowRadius = 0.5f
    }

    override fun render(entityIn: CustomizableBlockEntity, entityYaw: Float, partialTicks: Float, @Nonnull matrixStackIn: MatrixStack, @Nonnull bufferIn: IRenderTypeBuffer, packedLightIn: Int) {
        val blockState = entityIn.getBlockState()
        if (blockState.renderShape == BlockRenderType.MODEL) {
            val world = entityIn.level
            matrixStackIn.pushPose()
            val blockPos = BlockPos(entityIn.x, entityIn.boundingBox.maxY, entityIn.z)
            matrixStackIn.translate(-0.5, 0.0, -0.5)
            val blockRendererDispatcher = Minecraft.getInstance().blockRenderer
            for (type in RenderType.chunkBufferLayers()) {
                if (RenderTypeLookup.canRenderInLayer(blockState, type)) {
                    ForgeHooksClient.setRenderLayer(type)
                    blockRendererDispatcher.modelRenderer.tesselateBlock(
                        world, blockRendererDispatcher.getBlockModel(blockState), blockState, blockPos, matrixStackIn, bufferIn.getBuffer(type), false, Random(), 2340965, OverlayTexture.NO_OVERLAY
                    )
                }
            }
            ForgeHooksClient.setRenderLayer(null)
            matrixStackIn.popPose()
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
        }
    }

    @Nonnull
    override fun getTextureLocation(@Nonnull entity: CustomizableBlockEntity): ResourceLocation {
        return AtlasTexture.LOCATION_BLOCKS
    }
}