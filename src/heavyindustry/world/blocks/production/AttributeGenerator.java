package heavyindustry.world.blocks.production;

import arc.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class AttributeGenerator extends AttributeCrafter {
    public float powerProduction = 1f;
    public AttributeGenerator(String name) {
        super(name);
        hasPower = true;
        consumesPower = false;
        outputsPower = true;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("power", (AttributeGeneratorBuild build) -> new Bar(
                () -> Core.bundle.format("bar.poweroutput", Strings.fixed(build.getPowerProduction() * 60f * build.timeScale(), 1)),
                () -> Pal.powerBar,
                () -> Mathf.num(build.efficiency > 0f)
        ));
    }

    public class AttributeGeneratorBuild extends AttributeCrafterBuild {
        @Override
        public float getPowerProduction() {
            return efficiency > 0f ? powerProduction * efficiencyMultiplier() : 0f;
        }
    }
}