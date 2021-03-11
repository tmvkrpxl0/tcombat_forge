package com.tmvkrpxl0.tcombat.common.skills;

import com.tmvkrpxl0.tcombat.TCombatMain;
import com.tmvkrpxl0.tcombat.common.util.TCombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class ReflectArrow extends AbstractActiveSkill{
    private static final Field inGroundField = ObfuscationReflectionHelper.findField(AbstractArrowEntity.class, "inGround");
    private static final AxisAlignedBB sizeBig = AxisAlignedBB.withSizeAtOrigin(30, 30, 30);
    private static final AxisAlignedBB sizeSmall = AxisAlignedBB.withSizeAtOrigin(0.2,0.2,0.2);
    @Override
    public boolean execute(ServerPlayerEntity player) {
        if(!player.getEntityWorld().isRemote){
            AxisAlignedBB axisAlignedBB = sizeBig.offset(player.getPositionVec());
            List<Entity> list = player.world.getEntitiesInAABBexcluding(player, axisAlignedBB,
                    o -> {
                        try {
                            return o instanceof ProjectileEntity && o.getDistanceSq(player) < (15*15) && player.canEntityBeSeen(o) &&
                                    TCombatUtil.getEntityToEntityAngle(o, player) < 30 && (!(o instanceof ArrowEntity) || !inGroundField.getBoolean(o));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return false;
                        }
                    });
            TCombatMain.LOGGER.info("GOT THE LIST!");
            if(list.isEmpty())return false;
            Vector3d lookVec = player.getLook(1F);
            Vector3d eyeVector = player.getEyePosition(1F);
            for(Entity e : list){
                ProjectileEntity projectile = (ProjectileEntity) e;
                Vector3d entityVector = projectile.getPositionVec();
                Vector3d difference = entityVector.subtract(eyeVector);
                boolean flyToLeft = -lookVec.getX() * difference.getZ() + lookVec.getZ() * difference.getX() > 0;
                double radian = Math.toRadians(flyToLeft ? 90 : -90);
                Vector3d velocity = projectile.getMotion();
                double x = velocity.getX();
                double z = velocity.getZ();
                double sin = Math.sin(radian);
                double cos = Math.cos(radian);
                projectile.setMotion(cos * x - sin * z, projectile.getMotion().y, sin * x + cos * z);
                projectile.velocityChanged = true;
                e.world.addParticle(ParticleTypes.SWEEP_ATTACK, entityVector.x, entityVector.y, entityVector.z, 0, 0, 0);
                player.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.2F, 1);
                for(Entity temp : e.world.getEntitiesWithinAABBExcludingEntity(e, sizeSmall.offset(entityVector))){
                    temp.attackEntityFrom(DamageSource.causePlayerDamage(player), 1);
                }

            }
            TCombatMain.LOGGER.info("REFLECTED!!!");
        }
        return true;
    }
}
