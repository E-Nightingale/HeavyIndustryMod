package heavyindustry.content;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static heavyindustry.core.HeavyIndustryMod.*;
import static mindustry.Vars.*;

public class HIGet {
    public static Color
            c1 = new Color(),
            c2 = new Color(),
            c3 = new Color(),
            c4 = new Color(),
            c5 = new Color(),
            c6 = new Color(),
            c7 = new Color(),
            c8 = new Color(),
            c9 = new Color(),
            c10 = new Color();

    public static Vec2
            v1 = new Vec2(),
            v2 = new Vec2(),
            v3 = new Vec2();

    public static class ExtendedPosition implements Position {
        public float x, y;

        public ExtendedPosition set(float x, float y){
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public float getX() {
            return x;
        }

        @Override
        public float getY() {
            return y;
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static Position pos(float x, float y){
        return new Position() {
            @Override
            public float getX() {
                return x;
            }

            @Override
            public float getY() {
                return y;
            }
        };
    }

    public static float dx(float px, float r, float angle){
        return px + r * (float) Math.cos(angle * Math.PI/180);
    }

    public static float dy(float py, float r, float angle){
        return py + r * (float) Math.sin(angle * Math.PI/180);
    }

    public static float posx(float x, float length, float angle){
        float a = (float) ((Math.PI * angle)/180);
        float cos = (float) Math.cos(a);
        return x + length * cos;
    }

    public static float posy(float y, float length, float angle){
        float a = (float) ((Math.PI * angle)/180);
        float sin = (float) Math.sin(a);
        return y + length * sin;
    }

    public static boolean isInstanceButNotSubclass(Object obj, Class<?> clazz) {
        if (clazz.isInstance(obj)) {
            try {
                if (getClassSubclassHierarchy(obj.getClass()).contains(clazz)) {
                    return false;
                }
            } catch (ClassCastException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static Seq<Class<?>> getClassSubclassHierarchy(Class<?> clazz) {
        Class<?> c = clazz.getSuperclass();
        Seq<Class<?>> hierarchy = new Seq<>();
        while (c != null) {
            hierarchy.add(c);
            Class<?>[] interfaces = c.getInterfaces();
            hierarchy.addAll(Arrays.asList(interfaces));
            c = c.getSuperclass();
        }
        return hierarchy;
    }

    public static Seq<Turret> turrets(){
        Seq<Turret> turretSeq = new Seq<>();
        int size = content.blocks().size;
        for(int i = 0; i < size; i++){
            Block b = content.block(i);
            if(b instanceof Turret){
                turretSeq.addUnique((Turret) b);
            }
        }
        return turretSeq;
    }

    /** turret and unit only, not use contents.bullets() */
    public static Seq<BulletType> bulletTypes(){//use item
        Seq<BulletType> bullets = new Seq<>();
        for(Turret t : turrets()){
            if(t instanceof ItemTurret){
                for(Item i : ((ItemTurret) t).ammoTypes.keys()){
                    BulletType b = ((ItemTurret) t).ammoTypes.get(i);
                    if(t.shoot.shots == 1 || b instanceof PointBulletType || b instanceof ArtilleryBulletType){
                        bullets.add(b);
                    } else {
                        BulletType bulletType = new BulletType() {{
                            fragBullet = b;
                            fragBullets = t.shoot.shots;
                            fragAngle = 0;
                            if (t.shoot instanceof ShootSpread) {
                                fragSpread = ((ShootSpread) (t.shoot)).spread;
                            }
                            fragRandomSpread = t.inaccuracy;
                            fragVelocityMin = 1 - t.velocityRnd;
                            absorbable = hittable = collides = collidesGround = collidesAir = false;
                            despawnHit = true;
                            lifetime = damage = speed = 0;
                            hitEffect = despawnEffect = Fx.none;
                        }};
                        bullets.add(bulletType);
                    }
                }
            }
        }
        return bullets;
    }

    //use for cst bullet
    public static Bullet anyOtherCreate(Bullet bullet, BulletType bt, Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY){
        bullet.type = bt;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.originX = x;
        bullet.originY = y;
        if(!(aimX == -1f && aimY == -1f)){
            bullet.aimTile = world.tileWorld(aimX, aimY);
        }
        bullet.aimX = aimX;
        bullet.aimY = aimY;

        bullet.initVel(angle, bt.speed * velocityScl);
        if(bt.backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = bt.lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = bt.drag;
        bullet.hitSize = bt.hitSize;
        bullet.mover = mover;
        bullet.damage = (damage < 0 ? bt.damage : damage) * bullet.damageMultiplier();
        if(bullet.trail != null){
            bullet.trail.clear();
        }
        bullet.add();

        if(bt.keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc)owner).vel());

        return bullet;
    }

    public static void liquid(ObjectMap<Integer, Cons<Liquid>> cons, String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        for (int i = 1 ; i < 10 ; i++){
            int index = i;
            var l = new Liquid(name + index, color){{
                explosiveness = exp * index;
                flammability = fla * index;
                heatCapacity = htc * index;
                viscosity = vis * index;
                temperature = temp / index;
            }};
            if(cons != null && cons.size > 0 && cons.containsKey(i)){
                cons.get(i).get(l);
            }
        }
    }

    public static void liquid(String name, Color color, float exp, float fla, float htc, float vis, float temp) {
        liquid(null, name, color, exp, fla, htc, vis, temp);
    }

    public static void item(ObjectMap<Integer, Cons<Item>> cons, String name, Color color, float exp, float fla, float cos, float radio, float chg, float health) {
        for (int i = 1 ; i < 10 ; i++){
            int index = i;
            var item = new Item(name + index, color){{
                explosiveness = exp * index;
                flammability = fla * index;
                cost = cos * index;
                radioactivity = radio * index;
                charge = chg * index;
                healthScaling = health * index;
            }};
            if(cons != null && cons.size > 0 && cons.containsKey(i)){
                cons.get(i).get(item);
            }
        }
    }

    /**
     * 1. Cannot use {@link Vars#content}
     * 2. Cannot be used for init() anymore
     * @author guiY
     */
    public static void test(){
        int size = 40;
        for(var l : new Liquid[]{Liquids.water, Liquids.slag, Liquids.oil, Liquids.cryofluid,
                Liquids.arkycite, Liquids.gallium, Liquids.neoplasm,
                Liquids.ozone, Liquids.hydrogen, Liquids.nitrogen, Liquids.cyanogen}){
            if(l.hidden) continue;
            ObjectMap<Integer, Cons<Liquid>> cons = getEntries(l, size);
            liquid(cons, l.name, l.color, l.explosiveness, l.flammability, l.heatCapacity, l.viscosity, l.temperature);
        }

        for(var item : new Item[]{Items.scrap, Items.copper, Items.lead, Items.graphite, Items.coal, Items.titanium, Items.thorium, Items.silicon, Items.plastanium,
                Items.phaseFabric, Items.surgeAlloy, Items.sporePod, Items.sand, Items.blastCompound, Items.pyratite, Items.metaglass,
                Items.beryllium, Items.tungsten, Items.oxide, Items.carbide, Items.fissileMatter, Items.dormantCyst}){
            if(item.hidden) continue;
            ObjectMap<Integer, Cons<Item>> cons = getEntries(item, size);
            item(cons, item.name, item.color, item.explosiveness, item.flammability, item.cost, item.radioactivity, item.charge, item.healthScaling);
        }
        Draw.color();
    }

    private static ObjectMap<Integer, Cons<Item>> getEntries(Item item, int size) {
        ObjectMap<Integer, Cons<Item>> cons = new ObjectMap<>();
        for(int i = 1; i < 10; i++){
            int finalI = i;
            cons.put(i, it -> {
                PixmapRegion base = Core.atlas.getPixmap(item.uiIcon);
                var mix = base.crop();
                var number = Core.atlas.find(name("number-" + finalI));
                if(number.found()) {
                    PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                    mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                }

                it.uiIcon = it.fullIcon = new TextureRegion(new Texture(mix));

                it.buildable = item.buildable;
                it.hardness = item.hardness + finalI;
                it.lowPriority = item.lowPriority;
            });
        }
        return cons;
    }

    private static ObjectMap<Integer, Cons<Liquid>> getEntries(Liquid liquid, int size) {
        ObjectMap<Integer, Cons<Liquid>> cons = new ObjectMap<>();
        for(int i = 1; i < 10; i++){
            int finalI = i;
            cons.put(i, ld -> {
                PixmapRegion base = Core.atlas.getPixmap(liquid.uiIcon);
                var mix = base.crop();
                var number = Core.atlas.find(name("number-" + finalI));
                if(number.found()) {
                    PixmapRegion region = TextureAtlas.blankAtlas().getPixmap(number);

                    mix.draw(region.pixmap, region.x, region.y, region.width, region.height, 0, base.height - size, size, size, false, true);
                }

                ld.uiIcon = ld.fullIcon = new TextureRegion(new Texture(mix));
            });
        }
        return cons;
    }
}
