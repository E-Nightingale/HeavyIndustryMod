package heavyindustry.gen;

import arc.util.*;
import mindustry.gen.*;

public class BuildingTetherPayloadLegsUnit extends PayloadLegsUnit implements BuildingTetherc {
    public @Nullable Building building;

    @Override
    public int classId() {
        return EntityRegister.getId(BuildingTetherPayloadLegsUnit.class);
    }

    @Override
    public void update() {
        super.update();
        if (building == null || !building.isValid() || building.team != team) {
            Call.unitDespawn(self());
        }
    }

    @Override
    public Building building() {
        return building;
    }

    @Override
    public void building(Building build) {
        building = build;
    }
}