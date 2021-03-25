package com.tmvkrpxl0.tcombat.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tmvkrpxl0.tcombat.TCombatMain;
import com.tmvkrpxl0.tcombat.client.models.TNTArrowModel;
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class TNTArrowRenderer extends ArrowRenderer<TNTArrowEntity> {
    private final TNTArrowModel model;
    private static final ResourceLocation BOX = new ResourceLocation(TCombatMain.MODID, "textures/entity/projectiles/tnt_arrow.png");
    public TNTArrowRenderer(EntityRendererManager p_i46193_1_) {
        super(p_i46193_1_);
        model = new TNTArrowModel();
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull TNTArrowEntity p_110775_1_) {
        return TippedArrowRenderer.NORMAL_ARROW_LOCATION;
    }

    @Override
    public void render(@Nonnull TNTArrowEntity p_225623_1_, float p_225623_2_, float p_225623_3_, @Nonnull MatrixStack p_225623_4_, @Nonnull IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        p_225623_4_.pushPose();
        p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(p_225623_3_, p_225623_1_.yRotO, p_225623_1_.yRot) - 90F));
        p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.xRot)));
        model.renderToBuffer(p_225623_4_, p_225623_5_.getBuffer(model.renderType(BOX)), p_225623_6_, OverlayTexture.pack(0, false), 1,1,1,1);
        p_225623_4_.popPose();
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
    }
}
