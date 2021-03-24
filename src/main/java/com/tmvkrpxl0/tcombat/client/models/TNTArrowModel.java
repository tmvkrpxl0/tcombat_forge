package com.tmvkrpxl0.tcombat.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

public class TNTArrowModel extends EntityModel<TNTArrowEntity> {
    private final ModelRenderer box;

    public TNTArrowModel() {
        this.box = new ModelRenderer(this, 0, 0);
        box.setTexSize(64, 64);
        texHeight = 64;
        texWidth = 64;

        this.box.addBox(-1, -1f,-8, 1, 1f, 8, 0,0,0);
        this.box.setPos(0.5f,1.5f,1);
        this.box.visible=true;
    }

    @Override
    public void setupAnim(@Nonnull TNTArrowEntity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
    }

    @Override
    public void renderToBuffer(MatrixStack p_225598_1_, @Nonnull IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        p_225598_1_.pushPose();
        box.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_+2, p_225598_6_, p_225598_7_, p_225598_8_);
        p_225598_1_.popPose();
    }
}
