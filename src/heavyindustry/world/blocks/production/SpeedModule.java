package heavyindustry.world.blocks.production;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;

import static arc.Core.*;

/**
 * Module for improving drill a bit of speed.
 */
public class SpeedModule extends DrillModule {
    public TextureRegion[] arrow = new TextureRegion[3];

    public SpeedModule(String name) {
        super(name);
        boostSpeed = 1f;
        powerMul = 1f;
        powerExtra = 100f;

        stackable = true;
    }

    @Override
    public void load() {
        super.load();
        for (int i = 0; i < 3; i++) {
            arrow[i] = atlas.find(name + "-arrow-" + i);
        }
    }

    public class SpeedModuleBuild extends DrillModuleBuild {
        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.effect);
            for (int i = 0; i < 3; i++) {
                float scl = (Mathf.sinDeg(-Time.time * 3 + 120 * i) * 1.2f + (Mathf.sinDeg(-Time.time * 3 + 120 * i + 120)) * 0.6f) * smoothWarmup;
                Draw.alpha(scl);
                Draw.rect(arrow[i], x, y, rotdeg());
            }
            Draw.reset();
        }
    }
}
