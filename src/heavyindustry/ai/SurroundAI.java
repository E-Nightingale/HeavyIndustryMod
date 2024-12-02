package heavyindustry.ai;

import arc.math.*;
import arc.math.geom.*;
import heavyindustry.math.*;
import mindustry.ai.types.*;

import static mindustry.Vars.*;

public class SurroundAI extends FlyingAI {
    public static final Vec2 tmp = new Vec2();

    public boolean inbound = false;

    @Override
    public void updateMovement() {
        unloadPayloads();

        if (target != null && unit.hasWeapons()) {
            float dst = unit.dst(target);

            float ang;

            if (dst > unit.type.range * 0.975f) {
                ang = unit.angleTo(target) + Mathm.asinDeg(unit.type.range * 0.95f / dst);
                inbound = false;
            } else if (dst > unit.type.range * 0.725f) {
                if (!inbound) ang = angleVertical();
                else {
                    ang = unit.rotation;
                    if (dst > unit.type.range * 0.925f) inbound = false;
                }
            } else if (dst > unit.type.range * 0.35f || Angles.within(unit.rotation - 180, unit.angleTo(target), 15)) {
                ang = unit.rotation;
                inbound = true;
            } else {
                ang = angleDelta(115f);
                inbound = true;
            }

            unit.lookAt(ang);
            unit.moveAt(tmp.trns(ang, unit.speed()));
        }

        if (target == null && state.rules.waves && unit.team == state.rules.defaultTeam) {
            moveTo(getClosestSpawner(), state.rules.dropZoneRadius + 130f);
        }
    }

    public float angleDelta(float ang) {
        return unit.angleTo(target) + /*Mathf.sign(unit.team.id % 2 == 0) **/ ang;
    }

    public float angleVertical() {
        return angleDelta(90f);
    }
}
