package HeavyIndustry.content;

import static HeavyIndustry.HeavyIndustryMod.name;
import static arc.Core.atlas;

import HeavyIndustry.graphics.g3d.CircleMesh;
import HeavyIndustry.maps.ColorPass.*;
import HeavyIndustry.maps.HeightPass.*;
import HeavyIndustry.maps.planets.PunkHazardPlanetGenerator;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.geom.Vec3;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.game.Team;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.type.Planet;

public class HIPlanets {
    public static Planet punkHazard;
    public static void load(){
        punkHazard = new Planet("punk-hazard", Planets.sun, 1f, 3){{
            icon = "punk-hazard";
            generator = new PunkHazardPlanetGenerator(){{
                baseHeight = -1f;
                baseColor = HIBlocks.albaster.mapColor;
                heights.addAll(
                        new AngleInterpHeight(){{
                            interp = new Interp.ExpIn(2, 10);
                            dir.set(1f, 0f, 0f);
                            magnitude = 5;
                        }},
                        new AngleInterpHeight(){{
                            interp = new Interp.ExpIn(2, 10);
                            dir.set(-0.5f, 0.5f, 1);
                            magnitude = 5;
                        }},
                        new AngleInterpHeight(){{
                            interp = new Interp.ExpIn(2, 10);
                            dir.set(-0.3f, -1f, -0.6f);
                            magnitude = 5;
                        }},
                        new ClampHeight(0f, 0.8f),
                        new NoiseHeight(){{
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 3;
                            magnitude = 1.2f;
                            heightOffset = -1f;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new ClampHeight(-0.2f, 0.8f),
                        new CraterHeight(new Vec3(-0.5f, 0.25f, 1f), 0.3f, -0.3f),
                        new CraterHeight(new Vec3(-0.3f, 0.5f, 0.8f), 0.17f, 0.2f) {{
                            set = true;
                        }},
                        new CraterHeight(new Vec3(1f, 0f, 0.6f), 0.17f, 0.1f) {{
                            set = true;
                        }},
                        new CraterHeight(new Vec3(1f, 0f, 0f), 0.17f, -0.2f)
                );

                colors.addAll(
                        new NoiseColorPass(){{
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 3;
                            magnitude = 1.2f;
                            minNoise = 0.3f;
                            maxNoise = 0.6f;
                            out = HIBlocks.deadGrass.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new NoiseColorPass(){{
                            seed = 5;
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 5;
                            magnitude = 1.2f;
                            minNoise = 0.1f;
                            maxNoise = 0.4f;
                            out = HIBlocks.quartzSand.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new NoiseColorPass(){{
                            seed = 8;
                            scale = 1.5;
                            persistence = 0.5;
                            octaves = 7;
                            magnitude = 1.2f;
                            minNoise = 0.1f;
                            maxNoise = 0.4f;
                            out = HIBlocks.quartzSand.mapColor;
                            offset.set(1500f, 300f, -500f);
                        }},
                        new FlatColorPass(){{
                            minHeight = -1f;
                            maxHeight = -0.19f;
                            out = Blocks.deepwater.mapColor;
                        }},
                        new CraterColorPass(new Vec3(-0.5f, 0.25f, 1f), 0.4f, HIBlocks.grenite.mapColor),
                        new CraterColorPass(new Vec3(-0.3f, 0.5f, 0.8f), 0.1f, Blocks.water.mapColor),
                        new CraterColorPass(new Vec3(1f, 0f, 0.6f), 0.2f, HIBlocks.grenite.mapColor),
                        new CraterColorPass(new Vec3(1f, 0f, 0f), 0.25f, HIBlocks.grenite.mapColor)
                );
            }};
            Vec3 ringPos = new Vec3(0,1,0).rotate(Vec3.X, 25);
            meshLoader = () -> new MultiMesh(
                    new HexMesh(this, 6),
                    new CircleMesh(atlas.find(name("ring4")), this, 80, 2.55f, 2.6f, ringPos),
                    new CircleMesh(atlas.find(name("ring3")), this,80, 2.2f, 2.5f, ringPos),
                    new CircleMesh(atlas.find(name("ring2")), this,80, 1.9f, 2.1f, ringPos),
                    new CircleMesh(atlas.find(name("ring1")), this,80, 1.8f, 1.85f, ringPos)
            );
            cloudMeshLoader = () -> new MultiMesh(
                    new HexSkyMesh(this, 6, -0.5f, 0.14f, 6, Blocks.water.mapColor.cpy().a(0.2f), 2, 0.42f, 1f, 0.6f),
                    new HexSkyMesh(this, 1, 0.6f, 0.15f, 6, Blocks.water.mapColor.cpy().a(0.2f), 2, 0.42f, 1.2f, 0.5f)
            );
            launchCapacityMultiplier = 0.2f;
            sectorSeed = 3;
            allowWaves = true;
            allowWaveSimulation = true;
            allowSectorInvasion = false;
            allowLaunchSchematics = true;
            enemyCoreSpawnReplace = true;
            allowLaunchLoadout = true;
            prebuildBase = false;
            ruleSetter = r -> {
                r.waveTeam = Team.crux;
                r.placeRangeCheck = false;
                r.showSpawns = false;
            };
            iconColor = Color.valueOf("686b7b");
            atmosphereColor = Color.valueOf("8f94b3");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            startSector = 0;
            alwaysUnlocked = true;
            landCloudColor = Pal.spore.cpy().a(0.5f);
            hiddenItems.addAll(Items.erekirItems).removeAll(Items.serpuloItems);
        }};
    }
}
