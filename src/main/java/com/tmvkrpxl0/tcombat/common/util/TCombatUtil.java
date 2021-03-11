package com.tmvkrpxl0.tcombat.common.util;

import com.google.common.primitives.Doubles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TCombatUtil {
    private static final HashMap<PlayerEntity, List<LivingEntity>> targets = new HashMap<>();

    public static double getEntityVectorAngle(@Nonnull Entity from, @Nonnull Entity to, @Nonnull Vector3d sourceToDestVelocity){
        Vector3d sourcePosition = from.getPositionVec();
        Vector3d targetPosition = to.getPositionVec();
        Vector3d difference = targetPosition.subtract(sourcePosition);
        return Math.toDegrees(angle(sourceToDestVelocity, difference));
    }

    public static double getEntityToEntityAngle(@Nonnull Entity from, @Nonnull Entity to){
        return getEntityVectorAngle(from, to, from.getMotion());
    }

    public static float angle(@Nonnull Vector3d a, Vector3d b) {
        double dot = Doubles.constrainToRange(a.dotProduct(b) / (a.length() * b.length()), -1.0D, 1.0D);
        return (float)Math.acos(dot);
    }

    @Nonnull
    public static List<LivingEntity> getTargets(@Nonnull PlayerEntity player) {
        return targets.containsKey(player)?targets.get(player).stream().filter(LivingEntity::isAlive).collect(Collectors.toList()):new LinkedList<>();
    }

    public static void setTargets(@Nonnull PlayerEntity player, @Nonnull List<LivingEntity> list){
        targets.put(player, list);
    }
}
