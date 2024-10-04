package heavyindustry.world.blocks.heat;

import arc.math.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.*;
import mindustry.world.meta.*;

public class FuelHeater extends GenericCrafter {
    public float heatOutput = 5f, warmupRate = 0.15f;

    public @Nullable ConsumeItemFilter filterItem;
    public @Nullable ConsumeLiquidFilter filterLiquid;

    public FuelHeater(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("heat", (FuelHeaterBuild e) -> new Bar("bar.heat", Pal.lightOrange, () -> e.heat / heatOutput));
    }

    @Override
    public void init() {
        filterItem = findConsumer(c -> c instanceof ConsumeItemFilter);
        filterLiquid = findConsumer(c -> c instanceof ConsumeLiquidFilter);
        super.init();
    }

    public class FuelHeaterBuild extends GenericCrafterBuild implements HeatBlock {
        public float heat, efficiencyMultiplier = 1f;

        @Override
        public void updateEfficiencyMultiplier(){
            if(filterItem != null){
                float m = filterItem.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }else if(filterLiquid != null){
                float m = filterLiquid.efficiencyMultiplier(this);
                if(m > 0) efficiencyMultiplier = m;
            }
        }

        @Override
        public void updateTile() {
            super.updateTile();
            heat = Mathf.approachDelta(heat, heatOutput * efficiency * efficiencyMultiplier, warmupRate * delta());
        }

        @Override
        public float heat() {
            return heat;
        }

        @Override
        public float heatFrac() {
            return heat / heatOutput;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(heat);
        }

        @Override
        public void read(Reads read) {
            super.read(read);
            heat = read.f();
        }
    }
}