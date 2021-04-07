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

class BlockEntityRenderer(renderManager: EntityRendererManager) :
    EntityRenderer<CustomizableBlockEntity>(renderManager) {
    init {
        shadowSize = 0.5f
    }
    override fun render(
        entityIn: CustomizableBlockEntity,
        entityYaw: Float,
        partialTicks: Float,
        @Nonnull matrixStackIn: MatrixStack,
        @Nonnull bufferIn: IRenderTypeBuffer,
        packedLightIn: Int
    ) {
        val blockstate = entityIn.blockState
        if (blockstate.renderType == BlockRenderType.MODEL) {
            val world = entityIn.entityWorld
            if (blockstate !== world.getBlockState(entityIn.position) && blockstate.renderType != BlockRenderType.INVISIBLE) {
                matrixStackIn.push()
                val blockpos = BlockPos(entityIn.posX, entityIn.boundingBox.maxY, entityIn.posZ)
                matrixStackIn.translate(-0.5, 0.0, -0.5)
                val blockRendererDispatcher = Minecraft.getInstance().blockRendererDispatcher
                blockRendererDispatcher.getModelForState(blockstate)
                for (type in RenderType.getBlockRenderTypes()) {
                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                        ForgeHooksClient.setRenderLayer(type)
                        blockRendererDispatcher.blockModelRenderer.renderModel(
                            world,
                            blockRendererDispatcher.getModelForState(blockstate),
                            blockstate,
                            blockpos,
                            matrixStackIn,
                            bufferIn.getBuffer(type),
                            false,
                            Random(),
                            2340965,
                            OverlayTexture.NO_OVERLAY
                        )
                    }
                }
                ForgeHooksClient.setRenderLayer(null)
                matrixStackIn.pop()
                super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn)
            }
        }
    }

    @Nonnull
    override fun getEntityTexture(@Nonnull entity: CustomizableBlockEntity): ResourceLocation {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE
    }
}