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

import javax.annotation.Nonnull;

// Made with Blockbench 3.8.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports

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
        super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
        model.renderToBuffer(p_225623_4_, p_225623_5_.getBuffer(model.renderType(BOX)), p_225623_6_, OverlayTexture.pack(0, false), 1,1,1,1);
    }


}
