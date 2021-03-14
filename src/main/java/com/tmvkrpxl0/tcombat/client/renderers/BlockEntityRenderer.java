package com.tmvkrpxl0.tcombat.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tmvkrpxl0.tcombat.common.entities.CustomizableBlockEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockEntityRenderer  extends EntityRenderer<CustomizableBlockEntity> {

    public BlockEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.5F;
    }

    @Override
    public void render(CustomizableBlockEntity entityIn, float entityYaw, float partialTicks, @Nonnull MatrixStack matrixStackIn, @Nonnull IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockState blockstate = entityIn.getBlockState();
        if (blockstate.getRenderType() == BlockRenderType.MODEL) {
            World world = entityIn.getEntityWorld();
            if (blockstate != world.getBlockState(entityIn.getPosition()) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                GL11.glEnable(GL11.GL_STENCIL_TEST); // Turn on da test
                GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT); // Flush old data

                GL11.glStencilMask(0xFF); // Writing = ON
                GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF); // Always "add" to frame
                GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); // Replace on success
                //Anything rendered here will be cut if goes beyond frame defined before.
                matrixStackIn.push();
                BlockPos blockpos = new BlockPos(entityIn.getPosX(), entityIn.getBoundingBox().maxY, entityIn.getPosZ());
                matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
                for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.getBlockRenderTypes()) {
                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                        net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
                        blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(blockstate), blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), 2340965, OverlayTexture.NO_OVERLAY);
                    }
                }
                net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
                matrixStackIn.pop();
                super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
                GL11.glDisable(GL11.GL_STENCIL_TEST);
            }
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getEntityTexture(@Nonnull CustomizableBlockEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
