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
    private static final AxisAlignedBB sizeBig = AxisAlignedBB.ofSize(30, 30, 30);
    private static final AxisAlignedBB sizeSmall = AxisAlignedBB.ofSize(0.2,0.2,0.2);
    @Override
    public boolean execute(ServerPlayerEntity player) {
        if(!player.getCommandSenderWorld().isClientSide){
            AxisAlignedBB axisAlignedBB = sizeBig.move(player.position());
            List<Entity> list = player.level.getEntities(player, axisAlignedBB,
                    o -> o instanceof ProjectileEntity && o.distanceToSqr(player) < (15*15) && player.canSee(o) &&
                            TCombatUtil.getEntityToEntityAngle(o, player) < 30 && (!(o instanceof ArrowEntity) || o.isOnGround()));
            TCombatMain.LOGGER.info("GOT THE LIST!");
            if(list.isEmpty())return false;
            Vector3d lookVec = player.getViewVector(1F);
            Vector3d eyeVector = player.getEyePosition(1F);
            for(Entity e : list){
                ProjectileEntity projectile = (ProjectileEntity) e;
                Vector3d entityVector = projectile.position();
                Vector3d difference = entityVector.subtract(eyeVector);
                boolean flyToLeft = -lookVec.x() * difference.z() + lookVec.z() * difference.x() > 0;
                double radian = Math.toRadians(flyToLeft ? 90 : -90);
                Vector3d velocity = projectile.getDeltaMovement();
                double x = velocity.x();
                double z = velocity.z();
                double sin = Math.sin(radian);
                double cos = Math.cos(radian);
                projectile.setDeltaMovement(cos * x - sin * z, projectile.getDeltaMovement().y, sin * x + cos * z);
                projectile.hurtMarked = true;
                e.level.addParticle(ParticleTypes.SWEEP_ATTACK, entityVector.x, entityVector.y, entityVector.z, 0, 0, 0);
                player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.2F, 1);
                for(Entity temp : e.level.getEntities(e, sizeSmall.move(entityVector))){
                    temp.hurt(DamageSource.playerAttack(player), 1);
                }

            }
            TCombatMain.LOGGER.info("REFLECTED!!!");
        }
        return true;
    }
}
