package com.tmvkrpxl0.tcombat.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.data.ForgeItemTagsProvider;

// Made with Blockbench 3.8.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

public class TNTArrowRenderer extends ArrowRenderer<TNTArrowEntity> {
    public static final ResourceLocation ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation TNT_BOX = new ResourceLocation("tcombat", "textures/entity/projectiles/tnt_arrow.png");
    public TNTArrowRenderer(EntityRendererManager p_i46193_1_) {
        super(p_i46193_1_);
    }

    @Override
    public void render(TNTArrowEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        renderTest(p_225623_1_, p_225623_2_, p_225623_3_, matrixStack, p_225623_5_, p_225623_6_);
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, matrixStack, p_225623_5_, p_225623_6_);
    }

    private void renderTest(TNTArrowEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer p_225623_5_, int p_225623_6_){
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(p_225623_3_, p_225623_1_.yRotO, p_225623_1_.yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.xRot)));
        matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
        matrixStack.translate(-4.0D, 0.0D, 0.0D);
        IVertexBuilder ivertexbuilder = p_225623_5_.getBuffer(RenderType.entityCutout(TNT_BOX));
        MatrixStack.Entry entry = matrixStack.last();
        Matrix4f matrix4f = entry.pose();
        Matrix3f matrix3f = entry.normal();
        //x length, y length, z length, x pixel position, y pixel position,
        //I don't understand what those lines mean, help
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -7, -1, -1, 0.0F, 0.4375F, 1, 0, 0, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -7, -1, 1, 0.1875F, 0.0F, 1, 0, 0, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -7, 1, 1, 0.1875F, 0.3125F, -1, 0, 0, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -7, 1, -1, 0.0F, 0.3125F, -1, 0, 0, p_225623_6_);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -8, -1, 1, 0.0F, 0.5F, 1, 1, 1, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, 8, -1, 1, 0.5F, 0.0F, 1, 1, 1, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, 8, 1, 1, 0.5F, 0.0F, 1, 1, 1, p_225623_6_);
        this.vertex(matrix4f, matrix3f, ivertexbuilder, -8, 1, 1, 0.0F, 0.5F, 1, 1, 1, p_225623_6_);


        matrixStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TNTArrowEntity p_110775_1_) {
        return ARROW_LOCATION;
    }
}
