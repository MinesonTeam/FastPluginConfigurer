package kz.hxncus.mc.fastpluginconfigurer.util;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

@UtilityClass
public class PotionUtil {
    public PotionEffect getPotionEffect(PotionMeta pm) {
        PotionData potionData = pm.getBasePotionData();
        PotionType potionType = potionData.getType();
        PotionEffectType potionEffectType = potionType.getEffectType();
        if (potionEffectType == null) {
            return null;
        }
        boolean extended = potionData.isExtended();
        boolean upgraded = potionData.isUpgraded();
        boolean irregular = isIrregular(potionEffectType);
        boolean negative = isNegative(potionEffectType);

        double duration = 1200;
        int amplifier = 0;
        if (!extended && !upgraded && !irregular) {
            duration *= negative ? 1.5 : 3;
        } else if (!extended && upgraded && !irregular) {
            duration = negative ? duration / 3 : duration * 1.5;
            amplifier = negative ? 3 : 1; // hard code slowness 4 in because its the only negative semi-irregular potion effect
        } else if (extended && !upgraded && !irregular) {
            duration *= negative ? 4 : 8;
        } else if (potionType.equals(PotionType.REGEN) || potionType.equals(PotionType.POISON)) {
            if (extended) {
                duration *= 1.5;
            } else {
                if (upgraded) {
                    duration = negative ? 432 : 440;
                    amplifier = 1;
                } else {
                    duration = 900;
                }
            }
        } else if (potionType.equals(PotionType.INSTANT_DAMAGE) || potionType.equals(PotionType.INSTANT_HEAL)) {
            duration = 1;
            amplifier = upgraded ? 1 : 0;
        } else if (potionType.equals(PotionType.LUCK)) {
            duration *= 5;
        } else if (potionType.equals(PotionType.TURTLE_MASTER)) {
            return null; // make sure in your method you do something about this. Since turtle master gives two potion effects, you have to handle this outside of this method.
        }
        return new PotionEffect(potionEffectType, (int) duration, amplifier);
    }


    public boolean isNegative(PotionEffectType pet) {
        for(PotionType type : getNegativePotions()) {
            if (type.getEffectType().equals(pet)) {
                return true;
            }
        }
        return false;
    }


    public PotionType[] getNegativePotions() {
        return new PotionType[]{PotionType.INSTANT_DAMAGE, PotionType.POISON, PotionType.SLOWNESS, PotionType.WEAKNESS, PotionType.SLOW_FALLING};
    }


    public boolean isIrregular(PotionEffectType pet) {
        for(PotionType type : getIrregularPotions()) {
            if (type.getEffectType().equals(pet)) {
                return true;
            }
        }
        return false;
    }

    public PotionType[] getIrregularPotions() {
        return new PotionType[]{PotionType.REGEN, PotionType.LUCK, PotionType.POISON, PotionType.TURTLE_MASTER, PotionType.INSTANT_DAMAGE, PotionType.INSTANT_HEAL};
    }

    public boolean isUnusable(PotionType type) {
        for(PotionType pt : getUnusable()) {
            if (pt.equals(type)) {
                return true;
            }
        }
        return false;
    }


    public PotionType[] getUnusable() {
        return new PotionType[]{PotionType.AWKWARD, PotionType.WATER, PotionType.THICK, PotionType.MUNDANE, PotionType.UNCRAFTABLE};
    }
}
