package com.tmvkrpxl0.tcombat.common.listeners;

import com.tmvkrpxl0.tcombat.common.enchants.TCombatEnchants;
import com.tmvkrpxl0.tcombat.common.skills.AbstractSkill;
import com.tmvkrpxl0.tcombat.common.skills.Skills;
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.tmvkrpxl0.tcombat.TCombatMain.LOGGER;
import static com.tmvkrpxl0.tcombat.TCombatMain.MODID;

@Mod.EventBusSubscriber(modid=MODID,bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT,Dist.DEDICATED_SERVER})
public class CommonEventListener {
    private static final Map<LivingEntity, ArrowCounter> multishotTracker = new HashMap<>();
    private static final Set<AbstractArrowEntity> instaArrows = new HashSet<>();
    private static final Map<AbstractArrowEntity, Double> snipeArrows = new HashMap<>();
    @SubscribeEvent
    public static void onRegistryCreation(RegistryEvent.NewRegistry event){
        RegistryBuilder<AbstractSkill> builder = new RegistryBuilder<>();
        builder.setName(new ResourceLocation(MODID, "skill_registry"));
        builder.setType(AbstractSkill.class);
        IForgeRegistry<AbstractSkill> registry = builder.create();
        registry.registerAll(Skills.ARROW_SENSE, Skills.REFLECT_ARROW);
        LOGGER.info("Registry Created!");
    }

    @SubscribeEvent
    public static void worldTickEvent(TickEvent.ServerTickEvent event){
        if(event.side == LogicalSide.SERVER){
            multishotTracker.clear();
            instaArrows.removeIf(e -> e.isOnGround() || !e.isAlive());
            snipeArrows.keySet().removeIf(abstractArrowEntity -> !abstractArrowEntity.isAlive() || abstractArrowEntity.isOnGround());
        }
    }

    public static class ArrowCounter{
        private final AbstractArrowEntity[] arrows = new AbstractArrowEntity[3];
        private int count = 0;
        public ArrowCounter() {
        }
        public void count(AbstractArrowEntity entity){
            arrows[count] = entity;
            count++;
        }

        public int getCount(){
            return this.count;
        }

        public AbstractArrowEntity[] getArrows() {
            return arrows;
        }
    }



    @SubscribeEvent
    public static void onProjectileHit(ProjectileImpactEvent.Arrow event){
        if(instaArrows.contains(event.getArrow())){
            RayTraceResult r = event.getRayTraceResult();
            if(r instanceof EntityRayTraceResult){
                EntityRayTraceResult result = (EntityRayTraceResult) r;
                result.getEntity().setInvulnerable(false);
                result.getEntity().invulnerableTime = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event){
        if(event.getSource() instanceof IndirectEntityDamageSource){
            IndirectEntityDamageSource source = (IndirectEntityDamageSource) event.getSource();
            if(source.getMsgId().equals("arrow")){
                if(snipeArrows.containsKey(source.getDirectEntity())){
                    AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity) source.getDirectEntity();
                    event.setAmount((float) (abstractArrowEntity.getBaseDamage() * snipeArrows.remove(abstractArrowEntity)));
                }
            }
        }
    }

    @SubscribeEvent
    //public static void onUse(PlayerInteractEvent.LeftClickBlock event){
    public static void onUse(PlayerInteractEvent event){
        if(!(event instanceof PlayerInteractEvent.LeftClickBlock))return;
        ItemStack stack = event.getItemStack();
        if(stack.getItem() instanceof BucketItem){
            World world = event.getEntityLiving().level;
            BlockPos blockPos = event.getPos().relative(event.getFace());
            BlockState blockState = world.getBlockState(blockPos);
            BucketItem bucketItem = (BucketItem) stack.getItem();
            Fluid fluid = bucketItem.getFluid();
            if(fluid instanceof FlowingFluid){
                fluid = ((FlowingFluid) fluid).getFlowing();
                if(blockState.canBeReplaced(fluid) || blockState.isAir(world, blockPos)){
                    TCombatUtil.emptyBucket(bucketItem, event.getPlayer(), world, blockPos, null, fluid);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event){
        if(!event.getEntity().level.isClientSide() && event.getEntity() instanceof AbstractArrowEntity){
            AbstractArrowEntity abstractArrowEntity = (AbstractArrowEntity) event.getEntity();
            if(abstractArrowEntity.getOwner() instanceof LivingEntity){
                LivingEntity shooter = (LivingEntity) abstractArrowEntity.getOwner();
                ItemStack stack = shooter.getMainHandItem().getItem() == Items.CROSSBOW ? shooter.getMainHandItem() : shooter.getOffhandItem();
                if(stack.getItem()==Items.CROSSBOW){
                    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);

                    if(enchants.containsKey(TCombatEnchants.FOCUS.get())){
                        instaArrows.add(abstractArrowEntity);
                        if(enchants.containsKey(Enchantments.MULTISHOT)){
                            if (!multishotTracker.containsKey(shooter)) {
                                multishotTracker.put(shooter, new ArrowCounter());
                            }
                            ArrowCounter counter = multishotTracker.get(shooter);
                            if(counter.getCount()==3)return;
                            counter.count(abstractArrowEntity);
                            if(counter.getCount()==3){
                                AbstractArrowEntity[] entities = counter.getArrows();
                                Vector3d vector3d1 = shooter.getUpVector(1.0F);
                                Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 0, true);
                                Vector3d vector3d = shooter.getViewVector(1.0F);
                                Vector3f vector3f = new Vector3f(vector3d);
                                vector3f.transform(quaternion);
                                Vector3d originalVector = entities[0].getDeltaMovement();
                                for(int i = 1;i<3;i++){
                                    entities[i].setDeltaMovement(vector3f.x()*originalVector.length(), vector3f.y()*originalVector.length(), vector3f.z()*originalVector.length());
                                    entities[i].hurtMarked = true;
                                }
                            }
                        }else{
                            Vector3d vector3d1 = shooter.getUpVector(1.0F);
                            Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 0, true);
                            Vector3d vector3d = shooter.getViewVector(1.0F);
                            Vector3f vector3f = new Vector3f(vector3d);
                            vector3f.transform(quaternion);
                            Vector3d originalVector = abstractArrowEntity.getDeltaMovement();
                            abstractArrowEntity.setDeltaMovement(vector3f.x()*originalVector.length(), vector3f.y()*originalVector.length(), vector3f.z()*originalVector.length());
                            abstractArrowEntity.hurtMarked = true;
                        }
                    }

                    if(enchants.containsKey(TCombatEnchants.SNIPE.get())){
                        Vector3d vector3d1 = shooter.getUpVector(1.0F);
                        Quaternion quaternion = new Quaternion(new Vector3f(vector3d1), 0, true);
                        Vector3d vector3d = shooter.getViewVector(1.0F);
                        Vector3f vector3f = new Vector3f(vector3d);
                        vector3f.transform(quaternion);
                        Vector3d originalVector = abstractArrowEntity.getDeltaMovement();
                        snipeArrows.put(abstractArrowEntity, originalVector.length());
                        double length = originalVector.length() * 10;
                        abstractArrowEntity.setDeltaMovement(vector3f.x()*length, vector3f.y()*length, vector3f.z()*length);
                        abstractArrowEntity.hurtMarked = true;
                    }

                    if(enchants.containsKey(TCombatEnchants.CROSSBOW_FLAME.get())){
                        abstractArrowEntity.setSecondsOnFire(100);
                    }
                }
            }
        }
    }
}
