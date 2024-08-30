package heavyindustry.core;

import heavyindustry.content.*;
import heavyindustry.gen.HIIcon;
import heavyindustry.gen.HISounds;
import heavyindustry.graphics.HICacheLayer;
import heavyindustry.graphics.HIShaders;
import heavyindustry.ui.dialogs.HIResearchDialog;
import heavyindustry.world.meta.HIAttribute;
import java.util.Objects;
import arc.*;
import arc.flabel.FLabel;
import arc.scene.ui.Label;
import arc.util.*;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.*;
import mindustry.ui.dialogs.*;

public class HeavyIndustryMod extends Mod{
    public static String ModName = "heavy-industry";
    public static String name(String add){
        return ModName + "-" + add;
    }

    private static final String linkGitHub = "https://github.com/Wisadell/HeavyIndustryMod";
    public HeavyIndustryMod(){
        Log.info("Loaded HeavyIndustry Mod constructor.");
        Events.on(EventType.ClientLoadEvent.class, e -> {
            HIIcon.load();
            new BaseDialog(Core.bundle.get("mod.heavy-industry.name")){{
                buttons.button("@close", this::hide).size(210f, 64f);
                buttons.button((Core.bundle.get("mod.heavy-industry.linkGithub")), () -> {
                    if (!Core.app.openURI(linkGitHub)) {
                        Vars.ui.showErrorMessage("@linkfail");
                        Core.app.setClipboardText(linkGitHub);
                    }
                }).size(210f, 64f);
                cont.pane(table -> {
                    table.image(Core.atlas.find(name("cover"))).left().size(600f, 287f).pad(3f).row();
                    table.add(Core.bundle.get("mod.heavy-industry.version")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
                    Label flabel1 = new FLabel(Core.bundle.get("mod.heavy-industry.author"));
                    table.add(flabel1).left().row();
                    table.add(Core.bundle.get("mod.heavy-industry.class")).left().growX().wrap().pad(4).labelAlign(Align.left).row();
                    table.add(Core.bundle.get("mod.heavy-industry.note")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
                    table.add(Core.bundle.get("mod.heavy-industry.prompt")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
                }).grow().center().maxWidth(600f);
                show();
            }};
        });

        Events.on(EventType.FileTreeInitEvent.class, e -> Core.app.post(() -> {
            HIShaders.init();
            HICacheLayer.init();
        }));

        Events.on(EventType.DisposeEvent.class, e ->
                HIShaders.dispose()
        );
    }
    @Override
    public void loadContent(){
        Log.info("Loading some heavy industry mod content.");
        HISounds.load();
        HIItems.load();
        HILiquids.load();
        HIAttribute.load();
        HIStatusEffects.load();
        HIUnitTypes.load();
        HIBlocks.load();
        HIWeathers.load();
        HIOverride.load();
        HIPlanets.load();
        HISectorPresets.load();
        HITechTree.load();
    }

    @Override
    public void init(){
        super.init();
        HIResearchDialog dialog = new HIResearchDialog();
        ResearchDialog research = Vars.ui.research;
        research.shown(() -> {
            dialog.show();
            Objects.requireNonNull(research);
            Time.runTask(1f, research::hide);
        });
    }
}
