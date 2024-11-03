package heavyindustry.gen;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;

public final class HIRegister {
    private static final ObjectIntMap<Class<? extends Entityc>> ids = new ObjectIntMap<>();

    private static final ObjectMap<String, Prov<? extends Entityc>> map = new ObjectMap<>();

    private HIRegister(){
        throw new AssertionError("You are attempting to instantiated a class that should not be instantiated: HIRegister.");
    }

    /** Register unit's name. */
    public static <T extends Entityc> void put(String name, Class<T> type, Prov<? extends T> prov){
        map.put(name, prov);
        ids.put(type, EntityMapping.register(name, prov));
    }

    /** Find available class id and register it. */
    public static int getID(Class<? extends Entityc> type){
        return ids.get(type, -1);
    }

    /** Incase you used up all 256 class ids. use the same code for ~250 units you idiot. */
    public static void load(){
        put("LegsPayloadUnit", LegsPayloadUnit.class, LegsPayloadUnit::new);
        put("NoCoreDepositBuildingTetherLegsUnit", NoCoreDepositBuildingTetherLegsUnit.class, NoCoreDepositBuildingTetherLegsUnit::new);
        put("UltFire", UltFire.class, UltFire::new);
        put("EnergyUnit", EnergyUnit.class, EnergyUnit::new);
    }
}
