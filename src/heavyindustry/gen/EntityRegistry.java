package heavyindustry.gen;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import heavyindustry.core.*;

/** Registered the {@linkplain UnitEntity unit type} this mod offers. */
public class EntityRegistry {
    private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap<>();
    private static final ObjectMap<String, Prov<? extends Entityc>> map = new ObjectMap<>();
    public static <T extends Entityc> void register(String name, Class<T> type, Prov<? extends T> prov) {
        map.put(name, prov);
        ids.put(type, EntityMapping.register(name, prov));
    }

    public static int getID(Class<? extends Entityc> type) {
        return ids.get(type, -1);
    }

    /** Registered entity. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
    public static void register() {
        register("LegsPayloadLegacyUnit", LegsPayloadLegacyUnit.class, LegsPayloadLegacyUnit::new);
        register("TankLegacyUnit", TankLegacyUnit.class, TankLegacyUnit::new);
        register("NoCoreDepositBuildingTetherLegsUnit", NoCoreDepositBuildingTetherLegsUnit.class, NoCoreDepositBuildingTetherLegsUnit::new);
    }
}