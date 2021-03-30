package com.tmvkrpxl0.tcombat.client.models

import com.mojang.blaze3d.matrix.MatrixStack
import com.mojang.blaze3d.vertex.IVertexBuilder
import com.tmvkrpxl0.tcombat.common.entities.projectile.TNTArrowEntity
import net.minecraft.client.renderer.entity.model.EntityModel
import net.minecraft.client.renderer.model.ModelRenderer
import javax.annotation.Nonnull

class TNTArrowModel : EntityModel<TNTArrowEntity>() {
    private val box: ModelRenderer = ModelRenderer(this, 0, 0)
    override fun setRotationAngles(
        @Nonnull entityIn: TNTArrowEntity,
        limbSwing: Float,
        limbSwingAmount: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
    }

    override fun render(
        @Nonnull matrixStackIn: MatrixStack,
        @Nonnull bufferIn: IVertexBuilder,
        packedLightIn: Int,
        packedOverlayIn: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        box.setRotationPoint(1f, 1.5f, 0.5f)
        box.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red + 2, green, blue, alpha)
    }

    init {
        box.setTextureSize(64, 64)
        textureHeight = 64
        textureWidth = 64
        box.addBox(-8f, -1f, -1f, 8f, 1f, 1f, 0f, 0f, 0f)
        box.showModel = true
    }
}