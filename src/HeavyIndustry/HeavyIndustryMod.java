package HeavyIndustry;

import HeavyIndustry.content.*;
import arc.*;
import arc.flabel.FLabel;
import arc.scene.ui.Label;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class HeavyIndustryMod extends Mod{
    public static String ModName = "heavy-industry";
    public static String name(String add){
        return ModName + "-" + add;
    }
    public HeavyIndustryMod(){
        Log.info("Loaded HeavyIndustry constructor.");
        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog(Core.bundle.format("mod.heavy-industry.name"));
                dialog.buttons.button("@close", dialog::hide).size(100f, 50f);
                dialog.cont.pane(table -> {
                    table.image(Core.atlas.find(name("img"))).left().row();
                    table.add(Core.bundle.format("mod.heavy-industry.version")).left().growX().wrap().pad(4).labelAlign(Align.left).row();
                    Label flabel1 = new FLabel(Core.bundle.format("mod.heavy-industry.author"));
                    table.add(flabel1).left().row();
                    table.add("Heavy Industry Java Mod").left().growX().wrap().pad(4).labelAlign(Align.left).row();
                    table.add(Core.bundle.format("mod.heavy-industry.note")).left().growX().wrap().width(550).maxWidth(600).pad(4).labelAlign(Align.left).row();
                    table.add(Core.bundle.format("mod.heavy-industry.prompt")).left().growX().wrap().width(550).maxWidth(600).pad(4).labelAlign(Align.left).row();
                }).grow().center().maxWidth(600);
                dialog.show();
            });
        });
    }
    @Override
    public void loadContent(){
        Log.info("Loading some heavy industry content.");
        HISounds.load();
        HIItems.load();
        HILiquids.load();
        HIAttribute.load();
        HIStatusEffects.load();
        HIUnitTypes.load();
        HIBlocks.load();
        HIWeathers.load();
        HIOverride.load();
        HISectorPresets.load();
        HITechTree.load();
        Log.info("Content loaded!");
    }
}
