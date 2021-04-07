package com.tmvkrpxl0.tcombat.client.listeners

import com.tmvkrpxl0.tcombat.TCombatMain
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = TCombatMain.MODID, bus = EventBusSubscriber.Bus.FORGE, value = [Dist.CLIENT])
object ClientForgeEventListener { /*private static final HashSet<LivingEntity> added = new HashSet<>();
    private static LivingEntity temp;

    @SubscribeEvent
    public static void renderPre(RenderLivingEvent.Pre<PigEntity, PigModel<PigEntity>> event) {
        renderPreTest(event);
    }

    private static void renderPreTest(RenderLivingEvent.Pre<PigEntity, PigModel<PigEntity>> event){
        temp = event.getEntity();
        if(!(temp instanceof PigEntity))return;
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        //GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        LivingRenderer<PigEntity, PigModel<PigEntity>> renderer = event.getRenderer();
        if(!added.contains(temp)){
            event.getRenderer().addLayer(new LayerRenderer<PigEntity, PigModel<PigEntity>>(renderer) {
                @Override
                public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, PigEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                    //layerRender(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, renderer);
                }
            });
        }
        //added.add(temp);
    }

    private static void layerRender(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, PigEntity entitylivingbaseIn, LivingRenderer<PigEntity, PigModel<PigEntity>> renderer){
        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderType type = RenderType.getEntityCutout(renderer.getEntityTexture(entitylivingbaseIn));
        IVertexBuilder builder = bufferIn.getBuffer(type);
        int i = LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0);
        //renderer.getEntityModel().render(matrixStackIn, builder, packedLightIn, i, 1,0,0,1F);
    }

    private static void renderPostTest(RenderLivingEvent.Post<PigEntity, PigModel<PigEntity>> event){//렌더링 바로 이후 이벤트
        if(event.getEntity()!=temp)return;
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @SubscribeEvent
    public static void renderPost(RenderLivingEvent.Post<PigEntity, PigModel<PigEntity>> event){
        //renderPostTest(event);
    }*/
}