package heavyindustry.content;

import arc.graphics.*;
import heavyindustry.entities.effect.*;
import mindustry.content.*;
import mindustry.type.*;


/**
 * Defines the {@linkplain Liquid liquid} this mod offers.
 * @author Wisadell
 */
public final class HILiquids {
    public static Liquid
            brine,methane,nanofluid,nitratedOil;
    public static void load() {
        brine = new Liquid("brine", Color.valueOf("b8c89f")) {{
            coolant = false;
            viscosity = 0.8f;
            explosiveness = 0.1f;
        }};
        methane = new Liquid("methane", Color.valueOf("fbd367")) {{
            gasColor = barColor = lightColor = color;
            gas = true;
            flammability = 1f;
            explosiveness = 1f;
        }};
        nanofluid = new Liquid("nanofluid", Color.valueOf("7fd489")) {{
            heatCapacity = 1.5f;
            viscosity = 0.8f;
            temperature = 0.3f;
            lightColor = Color.valueOf("7fd489").a(0.3f);
            particleSpacing = 10;
            particleEffect = WrapperEffect.wrap(HIFx.glowParticle, color);
            effect = StatusEffects.electrified;
            coolant = true;
        }};
        nitratedOil = new Liquid("nitrated-oil", Color.valueOf("3c3e45")) {{
            temperature = 0.5f;
            viscosity = 0.8f;
            flammability = 0.8f;
            explosiveness = 3.2f;
            effect = StatusEffects.tarred;
            canStayOn.add(Liquids.water);
            coolant = false;
        }};
    }
}
