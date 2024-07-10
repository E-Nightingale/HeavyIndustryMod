package HeavyIndustry.content;

import HeavyIndustry.HeavyIndustryMod;
import arc.audio.Sound;
import mindustry.Vars;
import mindustry.mod.Mods;

public class HISounds {
    public static Mods.LoadedMod HI;
    public static Sound ct1;
    public static Sound dbz1;
    public static Sound dd1;
    public static Sound fj;
    public static Sound jg1;
    public static Sound flak;
    public static Sound gauss;
    public static Sound largeBeam;
    public static Sound hailRain;
    public static Sound bigHailstoneHit;

    public HISounds() {
    }

    public static void load() {
        ct1 = loadSound("ct1.ogg");
        dbz1 = loadSound("dbz1.ogg");
        dd1 = loadSound("dd1.ogg");
        fj = loadSound("fj.ogg");
        jg1 = loadSound("jg1.ogg");
        flak = loadSound("flak.ogg");
        gauss = loadSound("gauss.ogg");
        largeBeam = loadSound("largeBeam.ogg");
        hailRain = loadSound("hailRain.ogg");
        bigHailstoneHit = loadSound("bigHailstoneHit.ogg");
    }

    public static Sound loadSound(String name) {
        return new Sound(HI.root.child("sounds").child(name));
    }

    static {
        HI = Vars.mods.getMod(HeavyIndustryMod.class);
        ct1 = new Sound();
        dbz1 = new Sound();
        dd1 = new Sound();
        fj = new Sound();
        jg1 = new Sound();
        flak = new Sound();
        gauss = new Sound();
        largeBeam = new Sound();
        hailRain = new Sound();
        bigHailstoneHit = new Sound();
    }
}
