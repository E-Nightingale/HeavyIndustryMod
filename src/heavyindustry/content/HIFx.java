package heavyindustry.content;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.core.*;
import heavyindustry.entities.bullet.HailStoneBulletType.*;
import heavyindustry.entities.effect.*;
import heavyindustry.graphics.*;
import heavyindustry.graphics.Draws.*;
import heavyindustry.graphics.HITrails.*;
import heavyindustry.math.*;
import heavyindustry.struct.*;
import heavyindustry.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;

import java.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

/**
 * Defines the {@linkplain Effect visual effects} this mod offers.
 *
 * @author Eipusino
 */
public final class HIFx {
    public static final float EFFECT_MASK = Layer.effect + 0.0001f;
    public static final float EFFECT_BOTTOM = Layer.bullet - 0.11f;

    public static final float lightningAlign = 0.5f;

    public static final Rand rand = new Rand(), rand0 = new Rand(0), rand1 = new Rand(), rand2 = new Rand(), globalEffectRand = new Rand(0);
    public static final Vec2 v7 = new Vec2(), v8 = new Vec2(), v9 = new Vec2();

    public static final IntMap<Effect> same = new IntMap<>();

    private static int integer;

    /** HIFx should not be instantiated. */
    private HIFx() {}

    public static float fout(float fin, float margin) {
        if (fin >= 1f - margin) {
            return 1f - (fin - (1f - margin)) / margin;
        } else {
            return 1f;
        }
    }

    public static Effect shoot(Color color) {
        return new Effect(12, e -> {
            Draw.color(Color.white, color, e.fin());
            Lines.stroke(e.fout() * 1.2f + 0.5f);
            Angles.randLenVectors(e.id, 7, 25f * e.finpow(), e.rotation, 50f, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5f + 2f);
            });
        });
    }

    public static Effect flameShoot(Color colorBegin, Color colorTo, Color colorFrom, float length, float cone, int number, float lifetime) {
        return new Effect(lifetime, 80, e -> {
            Draw.color(colorBegin, colorTo, colorFrom, e.fin());
            Angles.randLenVectors(e.id, number, e.finpow() * length, e.rotation, cone, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 0.65f + e.fout() * 1.5f);
            });
        });
    }

    public static Effect casing(float lifetime) {
        return new Effect(lifetime, e -> {
            Draw.color(Pal.lightOrange, Color.lightGray, Pal.lightishGray, e.fin());
            Draw.alpha(e.fout(0.5f));
            float rot = Math.abs(e.rotation) + 90f;
            int i = -Mathf.sign(e.rotation);
            float len = (2f + e.finpow() * 10f) * i;
            float lr = rot + e.fin() * 20f * i;
            Draw.rect(atlas.find("casing"), e.x + Angles.trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()), e.y + Angles.trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()), 2f, 3f, rot + e.fin() * 50f * i);
        }).layer(Layer.bullet);
    }

    public static Effect edessp(float lifetime) {
        return new Effect(lifetime, e -> {
            if (e.data instanceof Object[] objects && objects.length == 4 && objects[0] instanceof TextureRegion region && objects[1] instanceof Float range && objects[2] instanceof Float rot && objects[3] instanceof Float rRot) {
                float ex = e.x + Angles.trnsx(e.rotation + rRot * e.fin(), range * e.fout()), ey = e.y + Angles.trnsy(e.rotation + rRot * e.fin(), range * e.fout());
                Draw.rect(region, ex, ey, region.width / 3f * e.fin(), region.height / 3f * e.fin(), rot);
            }
        }).followParent(true);
    }

    public static Effect fireworksShoot(float r) {
        return new Effect(30, e -> {
            Draw.z(Layer.effect - 0.1f);
            Draw.color(Utils.c7.set(HIPal.rainBowRed).shiftHue(Time.time * 2f));
            Angles.randLenVectors(e.id, 1, e.fin() * 20f, e.rotation + r, 0, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 2 * e.fout());
            });
            Angles.randLenVectors(e.id, 1, e.fin() * 20f, e.rotation - r, 0, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, 2 * e.fout());
            });
            Draw.blend();
            Draw.reset();
        });
    }

    public static float fslope(float fin) {
        return (0.5f - Math.abs(fin - 0.5f)) * 2f;
    }

    public static Effect electricExp(float lifetime, float sw, float r) {
        return new Effect(lifetime, e -> {
            if (e.time < sw) {
                float fin = e.time / sw, fout = 1 - fin;
                Lines.stroke(r / 12 * fout, Pal.heal);
                Lines.circle(e.x, e.y, r * fout);
            } else {
                float fin = (e.time - sw) / (e.lifetime - sw), fout = 1 - fin;
                float fbig = Math.min(fin * 10, 1);
                Lines.stroke(r / 2 * fout, Pal.heal);
                Lines.circle(e.x, e.y, r * fbig);
                for (int i = 0; i < 2; i++) {
                    float angle = i * 180 + 60;
                    Drawf.tri(e.x + Angles.trnsx(angle, r * fbig), e.y + Angles.trnsy(angle, r * fbig), 40 * fout, r / 1.5f, angle);
                }
                Draw.z(Layer.effect + 0.001f);
                Lines.stroke(r / 18 * fout, Pal.heal);
                Angles.randLenVectors(e.id + 1, fin * fin + 0.001f, 20, r * 2, (x, y, in, out) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + out * r / 4);
                    Drawf.light(e.x + x, e.y + y, out * r, Draw.getColor(), 0.8f);
                });
                Effect.shake(3, 3, e.x, e.y);
            }
        });
    }

    public static Effect circleOut(float lifetime, float radius, float thick) {
        return new Effect(lifetime, radius * 2f, e -> {
            Draw.color(e.color, Color.white, e.fout() * 0.7f);
            Lines.stroke(thick * e.fout());
            Lines.circle(e.x, e.y, radius * e.fin(Interp.pow3Out));
        });
    }

    public static Effect circleOut(Color color, float range) {
        return new Effect(Mathf.clamp(range / 2, 45f, 360f), range * 1.5f, e -> {
            rand.setSeed(e.id);

            Draw.color(Color.white, color, e.fin() + 0.6f);
            float circleRad = e.fin(Interp.circleOut) * range;
            Lines.stroke(Mathf.clamp(range / 24, 4, 20) * e.fout());
            Lines.circle(e.x, e.y, circleRad);
            for (int i = 0; i < Mathf.clamp(range / 12, 9, 60); i++) {
                Tmp.v1.set(1, 0).setToRandomDirection(rand).scl(circleRad);
                Drawn.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, rand.random(circleRad / 16, circleRad / 12) * e.fout(), rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180);
            }
        });
    }

    public static Effect circleSplash(Color color, float lifetime, int num, float range, float size) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            rand.setSeed(e.id);
            Angles.randLenVectors(e.id, num, range * e.finpow(), (x, y) -> {
                float s = e.fout(Interp.pow3In) * (size + rand.range(size / 3f));
                Fill.circle(e.x + x, e.y + y, s);
                Drawf.light(e.x + x, e.y + y, s * 2.25f, color, 0.7f);
            });
        });
    }

    public static Effect squareRand(Color color, float sizeMin, float sizeMax) {
        return new Effect(20f, sizeMax * 2f, e -> {
            Draw.color(Color.white, color, e.fin() + 0.15f);
            if (e.id % 2 == 0) {
                Lines.stroke(1.5f * e.fout(Interp.pow3Out));
                Lines.square(e.x, e.y, Mathf.randomSeed(e.id, sizeMin, sizeMax) * e.fin(Interp.pow2Out) + 3, 45);
            } else {
                Fill.square(e.x, e.y, Mathf.randomSeed(e.id, sizeMin * 0.5f, sizeMin * 0.8f) * e.fout(Interp.pow2Out), 45);
            }
        });
    }

    public static Effect blast(Color color, float range) {
        float lifetime = Mathf.clamp(range * 1.5f, 90f, 600f);
        return new Effect(lifetime, range * 2.5f, e -> {
            Draw.color(color);
            Drawf.light(e.x, e.y, e.fout() * range, color, 0.7f);

            e.scaled(lifetime / 3, t -> {
                Lines.stroke(3f * t.fout());
                Lines.circle(e.x, e.y, 8f + t.fin(Interp.circleOut) * range * 1.35f);
            });

            e.scaled(lifetime / 2, t -> {
                Fill.circle(t.x, t.y, t.fout() * 8f);
                Angles.randLenVectors(t.id + 1, (int) (range / 13), 2 + range * 0.75f * t.finpow(), (x, y) -> {
                    Fill.circle(t.x + x, t.y + y, t.fout(Interp.pow2Out) * Mathf.clamp(range / 15f, 3f, 14f));
                    Drawf.light(t.x + x, t.y + y, t.fout(Interp.pow2Out) * Mathf.clamp(range / 15f, 3f, 14f), color, 0.5f);
                });
            });

            Draw.z(Layer.bullet - 0.001f);
            Draw.color(Color.gray);
            Draw.alpha(0.85f);
            float intensity = Mathf.clamp(range / 10f, 5f, 25f);
            for (int i = 0; i < 4; i++) {
                rand.setSeed(((long) e.id << 1) + i);
                float lenScl = rand.random(0.4f, 1f);
                int fi = i;
                e.scaled(e.lifetime * lenScl, eIn -> Angles.randLenVectors(eIn.id + fi - 1, eIn.fin(Interp.pow10Out), (int) (intensity / 2.5f), 8f * intensity, (x, y, in, out) -> {
                    float fout = eIn.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                    Fill.circle(eIn.x + x, eIn.y + y, fout * ((2f + intensity) * 1.8f));
                }));
            }
        });
    }

    public static Effect laserEffect(float num) {
        return new Effect(26f, e -> {
            Draw.color(Color.white);
            float length = e.data instanceof Float f ? f : 70f;
            Angles.randLenVectors(e.id, (int) (length / num), length, e.rotation, 0f, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 9f);
                Drawf.light(e.x + x, e.y + y, e.fout(0.25f) * 12f, Color.white, 0.7f);
            });
        });
    }

    public static Effect chargeEffectSmall(Color color, float lifetime) {
        return new Effect(lifetime, 100f, e -> {
            Draw.color(color);
            Drawf.light(e.x, e.y, e.fin() * 55f, color, 0.7f);
            Angles.randLenVectors(e.id, 7, 3 + 50 * e.fout(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.finpow() * 3f);
            });
            Lines.stroke(e.fin() * 1.75f);
            Lines.circle(e.x, e.y, e.fout() * 40f);
            Angles.randLenVectors(e.id + 1, 16, 3 + 70 * e.fout(), (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 7 + 3);
            });
        });
    }

    public static Effect chargeBeginEffect(Color color, float size, float lifetime) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            Drawf.light(e.x, e.y, e.fin() * size, color, 0.7f);
            Fill.circle(e.x, e.y, size * e.fin());
        });
    }

    public static Effect crossBlast(Color color) {
        return get("crossBlast", color, crossBlast(color, 72));
    }

    public static Effect crossBlast(Color color, float size) {
        return crossBlast(color, size, 0);
    }

    public static Effect crossBlast(Color color, float size, float rotate) {
        return new Effect(Mathf.clamp(size / 3f, 35f, 240f), size * 2, e -> {
            Draw.color(color, Color.white, e.fout() * 0.55f);
            Drawf.light(e.x, e.y, e.fout() * size, color, 0.7f);
            e.scaled(10f, i -> {
                Lines.stroke(1.35f * i.fout());
                Lines.circle(e.x, e.y, size * 0.7f * i.finpow());
            });
            rand.setSeed(e.id);
            float sizeDiv = size / 1.5f;
            float randL = rand.random(sizeDiv);
            for (int i = 0; i < 4; i++) {
                Drawn.tri(e.x, e.y, size / 20 * (e.fout() * 3f + 1) / 4 * (e.fout(Interp.pow3In) + 0.5f) / 1.5f, (sizeDiv + randL) * Mathf.curve(e.fin(), 0, 0.05f) * e.fout(Interp.pow3), i * 90 + rotate);
            }
        });
    }

    public static Effect hyperBlast(Color color) {
        return get("hyperBlast", color, new Effect(30f, e -> {
            Draw.color(color, Color.white, e.fout() * 0.75f);
            Drawf.light(e.x, e.y, e.fout() * 55f, color, 0.7f);
            Lines.stroke(1.3f * e.fslope());
            Lines.circle(e.x, e.y, 45f * e.fin());
            Angles.randLenVectors(e.id + 1, 5, 8f + 50 * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 7f);
            });
        }));
    }

    public static Effect instShoot(Color color, Color colorInner) {
        return new Effect(24f, e -> {
            e.scaled(10f, (b) -> {
                Draw.color(Color.white, color, b.fin());
                Lines.stroke(b.fout() * 3f + 0.2f);
                Lines.circle(b.x, b.y, b.fin() * 50f);
            });
            Draw.color(color);

            for (int i : Mathf.signs) {
                Drawn.tri(e.x, e.y, 8f * e.fout(), 85f, e.rotation + 90f * i);
                Drawn.tri(e.x, e.y, 8f * e.fout(), 50f, 90 + 90f * i);
            }

            Draw.color(colorInner);

            for (int i : Mathf.signs) {
                Drawn.tri(e.x, e.y, 5f * e.fout(), 48f, e.rotation + 90f * i);
                Drawn.tri(e.x, e.y, 5f * e.fout(), 29f, 90 + 90f * i);
            }
        });
    }

    public static Effect hitSpark(Color color, float lifetime, int num, float range, float stroke, float length) {
        return new Effect(lifetime, e -> {
            Draw.color(color, Color.white, e.fout() * 0.3f);
            Lines.stroke(e.fout() * stroke);

            Angles.randLenVectors(e.id, num, e.finpow() * range, e.rotation, 360f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * length * 0.85f + length * 0.15f);
            });
        });
    }

    public static Effect get(String m, Color c, Effect effect) {
        int hash = Objects.hash(m, c);
        Effect or = same.get(hash);
        if (or == null) same.put(hash, effect);
        return or == null ? effect : or;
    }

    public static Effect shootLine(float size, float angleRange) {
        int num = Mathf.clamp((int) size / 6, 6, 20);
        float thick = Mathf.clamp(0.75f, 2f, size / 22f);

        return new Effect(37f, e -> {
            Draw.color(e.color, Color.white, e.fout() * 0.7f);
            rand.setSeed(e.id);
            Drawn.randLenVectors(e.id, num, 4 + (size * 1.2f) * e.fin(), size * 0.15f * e.fin(), e.rotation, angleRange, (x, y) -> {
                Lines.stroke(thick * e.fout(0.32f));
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), (e.fslope() + e.fin()) * 0.5f * (size * rand.random(0.15f, 0.5f) + rand.random(2f)) + rand.random(2f));
                Drawf.light(e.x + x, e.y + y, e.fslope() * (size * 0.5f + 14f) + 3, e.color, 0.7f);
            });
        });
    }

    public static Effect shootCircleSmall(Color color) {
        return get("shootCircleSmall", color, new Effect(30, e -> {
            Draw.color(color, Color.white, e.fout() * 0.75f);
            rand.setSeed(e.id);
            Angles.randLenVectors(e.id, 3, 3 + 23 * e.fin(), e.rotation, 22, (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * rand.random(1.5f, 3.2f));
                Drawf.light(e.x + x, e.y + y, e.fout() * 4.5f, color, 0.7f);
            });
        }));
    }

    public static Effect shootLineSmall(Color color) {
        return get("shootLineSmall", color, new Effect(37f, e -> {
            Draw.color(color, Color.white, e.fout() * 0.7f);
            Angles.randLenVectors(e.id, 4, 8 + 32 * e.fin(), e.rotation, 22F, (x, y) -> {
                Lines.stroke(1.25f * e.fout(0.2f));
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 6f + 3);
                Drawf.light(e.x + x, e.y + y, e.fout() * 13f + 3, color, 0.7f);
            });
        }));
    }

    public static Effect square(Color color, float lifetime, int num, float range, float size) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            rand.setSeed(e.id);
            Angles.randLenVectors(e.id, num, range * e.finpow(), (x, y) -> {
                float s = e.fout(Interp.pow3In) * (size + rand.range(size / 3f));
                Fill.square(e.x + x, e.y + y, s, 45);
                Drawf.light(e.x + x, e.y + y, s * 2.25f, color, 0.7f);
            });
        });
    }

    public static Effect sharpBlast(Color colorExternal, Color colorInternal, float lifetime, float range) {
        return new Effect(lifetime, range * 2, e -> {
            Angles.randLenVectors(e.id, (int) Mathf.clamp(range / 8, 4, 18), range / 8, range * (1 + e.fout(Interp.pow2OutInverse)) / 2f, (x, y) -> {
                float angle = Mathf.angle(x, y);
                float width = e.foutpowdown() * rand.random(range / 6, range / 3) / 2 * e.fout();

                rand.setSeed(e.id);
                float length = rand.random(range / 2, range * 1.1f) * e.fout();

                Draw.color(colorExternal);
                Drawn.tri(e.x + x, e.y + y, width, range / 3 * e.fout(Interp.pow2In), angle - 180);
                Drawn.tri(e.x + x, e.y + y, width, length, angle);

                Draw.color(colorInternal);

                width *= e.fout();

                Drawn.tri(e.x + x, e.y + y, width / 2, range / 3 * e.fout(Interp.pow2In) * 0.9f * e.fout(), angle - 180);
                Drawn.tri(e.x + x, e.y + y, width / 2, length / 1.5f * e.fout(), angle);
            });
        });
    }

    public static Effect sharpBlastRand(Color colorExternal, Color colorInternal, float rotation, float ranAngle, float lifetime, float range) {
        return new Effect(lifetime, range * 2, e -> {
            Angles.randLenVectors(e.id, (int) Mathf.clamp(range / 8, 2, 6), (1 + e.fout(Interp.pow2OutInverse)) / 2f, rotation, ranAngle, (x, y) -> {
                float angle = Mathf.angle(x, y);
                float width = e.foutpowdown() * rand.random(range / 6, range / 3) / 2 * e.fout();

                rand.setSeed(e.id);
                float length = rand.random(range / 2, range * 1.1f) * e.fout();

                Draw.color(colorExternal);
                Drawn.tri(e.x + x, e.y + y, width, length, angle);

                Draw.color(colorInternal);
                width *= e.fout();
                Drawn.tri(e.x + x, e.y + y, width / 2, length / 1.5f * e.fout(), angle);
            });
        });
    }

    public static Effect instBomb(Color color) {
        return get("instBomb", color, instBombSize(color, 4, 80f));
    }

    public static Effect instBombSize(Color color, int num, float size) {
        return new Effect(22f, size * 1.5f, e -> {
            Draw.color(color);
            Lines.stroke(e.fout() * 4f);
            Lines.circle(e.x, e.y, 4f + e.finpow() * size / 4f);
            Drawf.light(e.x, e.y, e.fout() * size, color, 0.7f);

            int i;
            for (i = 0; i < num; ++i) {
                Drawn.tri(e.x, e.y, size / 12f, size * e.fout(), (float) (i * 90 + 45));
            }

            Draw.color();

            for (i = 0; i < num; ++i) {
                Drawn.tri(e.x, e.y, size / 26f, size / 2.5f * e.fout(), (float) (i * 90 + 45));
            }
        });
    }

    public static Effect instHit(Color color) {
        return get("instHit", color, instHit(color, 5, 50));
    }

    public static Effect instHit(Color color, int num, float size) {
        return new Effect(20f, size * 1.5f, e -> {
            rand.setSeed(e.id);

            for (int i = 0; i < 2; ++i) {
                Draw.color(i == 0 ? color : color.cpy().lerp(Color.white, 0.25f));
                float m = i == 0 ? 1f : 0f;

                for (int j = 0; j < num; ++j) {
                    float rot = e.rotation + rand.range(size);
                    float w = 15f * e.fout() * m;
                    Drawn.tri(e.x, e.y, w, (size + rand.range(size * 0.6f)) * m, rot);
                    Drawn.tri(e.x, e.y, w, size * 0.3f * m, rot + 180f);
                }
            }

            e.scaled(12f, (c) -> {
                Draw.color(color.cpy().lerp(Color.white, 0.25f));
                Lines.stroke(c.fout() * 2f + 0f);
                Lines.circle(e.x, e.y, c.fin() * size * 1.1f);
            });

            e.scaled(18f, (c) -> {
                Draw.color(color);
                Angles.randLenVectors(e.id, 25, 8f + e.fin() * size * 1.25f, e.rotation, 60f, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, c.fout() * 3f, 45f);
                });
            });

            Drawf.light(e.x, e.y, e.fout() * size, color, 0.7f);
        });
    }

    public static Effect instTrail(Color color, float angle, boolean random) {
        return new Effect(30f, e -> {
            for (int j : angle == 0 ? Drawn.oneArr : Mathf.signs) {
                for (int i = 0; i < 2; ++i) {
                    Draw.color(i == 0 ? color : color.cpy().lerp(Color.white, 0.15f));
                    float m = i == 0 ? 1f : 0.5f;
                    float rot = e.rotation + 180f;
                    float w = 10f * e.fout() * m;
                    Drawn.tri(e.x, e.y, w, 30f + (random ? Mathf.randomSeedRange(e.id, 15f) : 8) * m, rot + j * angle);
                    if (angle == 0) Drawn.tri(e.x, e.y, w, 10f * m, rot + 180f + j * angle);
                    else Fill.circle(e.x, e.y, w / 2f);
                }
            }
        });
    }

    public static Effect smoothColorRect(Color out, float rad, float lifetime) {
        return new Effect(lifetime, rad * 2, e -> {
            Draw.blend(Blending.additive);
            float radius = e.fin(Interp.pow3Out) * rad;
            Fill.light(e.x, e.y, 4, radius, 45f, Color.clear, Tmp.c1.set(out).a(e.fout(Interp.pow5Out)));
            Draw.blend();
        }).layer(Layer.effect + 0.15f);
    }

    public static Effect smoothColorCircle(Color out, float rad, float lifetime) {
        return new Effect(lifetime, rad * 2, e -> {
            Draw.blend(Blending.additive);
            float radius = e.fin(Interp.pow3Out) * rad;
            Fill.light(e.x, e.y, Lines.circleVertices(radius), radius, Color.clear, Tmp.c1.set(out).a(e.fout(Interp.pow5Out)));
            Drawf.light(e.x, e.y, radius * 1.3f, out, 0.7f * e.fout(0.23f));
            Draw.blend();
        }).layer(Layer.effect + 0.15f);
    }

    public static Effect smoothColorCircle(Color out, float rad, float lifetime, float alpha) {
        return new Effect(lifetime, rad * 2, e -> {
            Draw.blend(Blending.additive);
            float radius = e.fin(Interp.pow3Out) * rad;
            Fill.light(e.x, e.y, Lines.circleVertices(radius), radius, Color.clear, Tmp.c1.set(out).a(e.fout(Interp.pow5Out) * alpha));
            Drawf.light(e.x, e.y, radius * 1.3f, out, 0.7f * e.fout(0.23f));
            Draw.blend();
        }).layer(Layer.effect + 0.15f);
    }

    public static Effect lineCircleOut(Color color, float lifetime, float size, float stroke) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            Lines.stroke(e.fout() * stroke);
            Lines.circle(e.x, e.y, e.fin(Interp.pow3Out) * size);
        });
    }

    public static Effect lineSquareOut(Color color, float lifetime, float size, float stroke, float rotation) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            Lines.stroke(e.fout() * stroke);
            Lines.square(e.x, e.y, e.fin(Interp.pow3Out) * size, rotation);
        });
    }

    public static Effect polyCloud(Color color, float lifetime, float size, float range, int num) {
        return (new Effect(lifetime, e -> {
            Angles.randLenVectors(e.id, num, range * e.finpow(), (x, y) -> {
                Draw.color(color, Pal.gray, e.fin() * 0.65f);
                Fill.poly(e.x + x, e.y + y, 6, size * e.fout(), e.rotation);
                Drawf.light(e.x + x, e.y + y, size * e.fout() * 2.5f, color, e.fout() * 0.65f);
                Draw.color(Color.white, Pal.gray, e.fin() * 0.65f);
                Fill.poly(e.x + x, e.y + y, 6, size * e.fout() / 2, e.rotation);
            });
        })).layer(Layer.bullet);
    }

    public static Effect polyTrail(Color fromColor, Color toColor, float size, float lifetime) {
        return new Effect(lifetime, size * 2, e -> {
            Draw.color(fromColor, toColor, e.fin());
            Fill.poly(e.x, e.y, 6, size * e.fout(), e.rotation);
            Drawf.light(e.x, e.y, e.fout() * size, fromColor, 0.7f);
        });
    }

    public static Effect genericCharge(Color color, float size, float range, float lifetime) {
        return new Effect(lifetime, e -> {
            Draw.color(color);
            Lines.stroke(size / 7f * e.fin());

            Angles.randLenVectors(e.id, 15, 3f + 60f * e.fout(), e.rotation, range, (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * size + size / 4f);
                Drawf.light(e.x + x, e.y + y, e.fout(0.25f) * size, color, 0.7f);
            });

            Fill.circle(e.x, e.y, size * 0.48f * Interp.pow3Out.apply(e.fin()));
        });
    }

    public static Effect lightningHitSmall(Color color) {
        return get("lightningHitSmall", color, new Effect(20, e -> {
            Draw.color(color, Color.white, e.fout() * 0.7f);
            Angles.randLenVectors(e.id, 5, 18 * e.fin(), (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 6 + 2);
                Drawf.light(e.x + x, e.y + y, e.fin() * 12 * e.fout(0.25f), color, 0.7f);
            });
        }));
    }

    public static Effect lightningHitLarge(Color color) {
        return get("lightningHitLarge", color, new Effect(50f, 180f, e -> {
            Draw.color(color);
            Drawf.light(e.x, e.y, e.fout() * 90f, color, 0.7f);
            e.scaled(25f, t -> {
                Lines.stroke(3f * t.fout());
                Lines.circle(e.x, e.y, 3f + t.fin(Interp.pow3Out) * 80f);
            });
            Fill.circle(e.x, e.y, e.fout() * 8f);
            Angles.randLenVectors(e.id + 1, 4, 1f + 60f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
            });

            Draw.color(Color.gray);
            Angles.randLenVectors(e.id, 8, 2f + 30f * e.finpow(), (x, y) -> {
                Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f);
            });
        }));
    }

    public static Effect subEffect(float lifetime, float radius, int num, float childLifetime, Interp spreadOutInterp, EffectParam effect) {
        return new Effect(lifetime, radius * 2f, e -> {
            rand.setSeed(e.id);
            float finT = e.lifetime * e.fin(spreadOutInterp);

            for (int s = 0; s < num; s++) {
                float sBegin = rand.random(e.lifetime - childLifetime);
                float fin = (finT - sBegin) / childLifetime;

                if (fin < 0 || fin > 1) continue;

                float fout = 1 - fin;

                rand2.setSeed(e.id + s);
                float theta = rand2.random(0f, Mathf.PI2);
                v9.set(Mathf.cos(theta), Mathf.sin(theta)).scl(radius * sBegin / (e.lifetime - childLifetime));

                Tmp.c1.set(e.color).lerp(Color.white, fout * 0.7f);
                Draw.color(Tmp.c1);
                effect.draw(e.id + s + 9999l, e.x + v9.x, e.y + v9.y, Mathf.radiansToDegrees * theta, fin);
            }
        });
    }

    public static Effect triSpark(float lifetime, Color colorFrom, Color colorTo) {
        return new Effect(lifetime, e -> {
            rand.setSeed(e.id);
            Draw.color(colorFrom, colorTo, e.fin());
            Angles.randLenVectors(e.id, 3, 3f + 24f * e.fin(), 5f, (x, y) -> {
                float randN = rand.random(120f);
                Fill.poly(e.x + x, e.y + y, 3, e.fout() * 8f * rand.random(0.8f, 1.2f), e.rotation + randN * e.fin());
            });
        });
    }

    public static final Effect
            hitOut = new Effect(60, e -> {
                if (e.data instanceof Unit u) {
                    UnitType type = u.type;
                    if (type != null) {
                        TextureRegion rg = type.fullIcon;
                        float w = rg.width * rg.scl() * Draw.xscl;
                        float h = rg.height * rg.scl() * Draw.yscl;
                        float dx = Utils.dx(e.x, Math.max(w, h) * 0.3f * e.finpow(), e.rotation), dy = Utils.dy(e.y, Math.max(w, h) * 0.3f * e.finpow(), e.rotation);
                        float z = Draw.z();
                        Draw.z(Layer.effect + 10);
                        Draw.alpha(e.foutpow());
                        Draw.rect(rg, dx, dy, w * 1.2f * e.finpow(), h * 1.2f * e.finpow(), u.rotation - 90);
                        Draw.z(z);
                    }
                }

                if (e.data instanceof Building b) {
                    Block type = b.block;
                    if (type != null) {
                        TextureRegion rg = type.fullIcon;
                        float w = rg.width * rg.scl() * Draw.xscl;
                        float h = rg.height * rg.scl() * Draw.yscl;
                        float dx = Utils.dx(e.x, h * 0.2f * e.finpow(), e.rotation), dy = Utils.dy(e.y, h * 0.2f * e.finpow(), e.rotation);
                        float z = Draw.z();
                        Draw.z(Layer.effect + 10);
                        Draw.alpha(e.foutpow());
                        Draw.rect(rg, dx, dy, w * 1.2f * e.finpow(), h * 1.2f * e.finpow());
                        Draw.z(z);
                    }
                }
            }),
            shieldDefense = new Effect(20, e -> {
                Draw.color(e.color);
                Lines.stroke(e.fslope() * 2.5f);
                Lines.poly(e.x, e.y, 6, 3 * e.fout() + 9);
                Angles.randLenVectors(e.id, 2, 32 * e.fin(), 0, 360, (x, y) -> {
                    Lines.poly(e.x + x, e.y + y, 6, 2 * e.fout() + 2);
                });
            }),
            missileShoot = new Effect(130f, 300f, e -> {
                Draw.color(e.color);
                Draw.alpha(0.67f * e.fout(0.9f));
                rand.setSeed(e.id);
                for (int i = 0; i < 35; i++) {
                    v9.trns(e.rotation + 180f + rand.range(21f), rand.random(e.finpow() * 90f)).add(rand.range(3f), rand.range(3f));
                    e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> {
                        Fill.circle(e.x + v9.x, e.y + v9.y, b.fout() * 9f + 0.3f);
                    });
                }
            }),
            shuttle = new Effect(70f, 800f, e -> {
                if (!(e.data instanceof Float len)) return;

                Draw.color(e.color, Color.white, e.fout() * 0.3f);
                Lines.stroke(e.fout() * 2.2F);

                Angles.randLenVectors(e.id, (int) Mathf.clamp(len / 12, 10, 40), e.finpow() * len, e.rotation, 360f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * len * 0.15f + len * 0.025f);
                });

                float fout = e.fout(Interp.exp10Out);
                for (int i : Mathf.signs) {
                    Drawn.tri(e.x, e.y, len / 17f * fout * (Mathf.absin(0.8f, 0.07f) + 1), len * 3f * Interp.swingOut.apply(Mathf.curve(e.fin(), 0, 0.7f)) * (Mathf.absin(0.8f, 0.12f) + 1) * e.fout(0.2f), e.rotation + 90 + i * 90);
                }
            }),
            shuttleDark = new Effect(70f, 800f, e -> {
                if (!(e.data instanceof Float len)) return;

                Draw.color(e.color, Color.white, e.fout() * 0.3f);
                Lines.stroke(e.fout() * 2.2F);

                Angles.randLenVectors(e.id, (int) Mathf.clamp(len / 12, 10, 40), e.finpow() * len, e.rotation, 360f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * len * 0.15f + len * 0.025f);
                });

                float fout = e.fout(Interp.exp10Out);
                for (int i : Mathf.signs) {
                    Drawn.tri(e.x, e.y, len / 17f * fout * (Mathf.absin(0.8f, 0.07f) + 1), len * 3f * Interp.swingOut.apply(Mathf.curve(e.fin(), 0, 0.7f)) * (Mathf.absin(0.8f, 0.12f) + 1) * e.fout(0.2f), e.rotation + 90 + i * 90);
                }

                float len1 = len * 0.66f;
                Draw.z(EFFECT_MASK);
                Draw.color(Color.black);
                for (int i : Mathf.signs) {
                    Drawn.tri(e.x, e.y, len1 / 17f * fout * (Mathf.absin(0.8f, 0.07f) + 1), len1 * 3f * Interp.swingOut.apply(Mathf.curve(e.fin(), 0, 0.7f)) * (Mathf.absin(0.8f, 0.12f) + 1) * e.fout(0.2f), e.rotation + 90 + i * 90);
                }

                Draw.z(EFFECT_BOTTOM);
                for (int i : Mathf.signs) {
                    Drawn.tri(e.x, e.y, len1 / 17f * fout * (Mathf.absin(0.8f, 0.07f) + 1), len1 * 3f * Interp.swingOut.apply(Mathf.curve(e.fin(), 0, 0.7f)) * (Mathf.absin(0.8f, 0.12f) + 1) * e.fout(0.2f), e.rotation + 90 + i * 90);
                }
            }).layer(Layer.effect - 1f),
            shuttleLerp = new Effect(180f, 800f, e -> {
                if (!(e.data instanceof Float len)) return;
                float f = Mathf.curve(e.fin(Interp.pow5In), 0f, 0.07f) * Mathf.curve(e.fout(), 0f, 0.4f);

                Draw.color(e.color);
                v7.trns(e.rotation - 90, (len + Mathf.randomSeed(e.id, 0, len)) * e.fin(Interp.circleOut));
                for (int i : Mathf.signs)
                    Drawn.tri(e.x + v7.x, e.y + v7.y, Mathf.clamp(len / 8, 8, 25) * (f + e.fout(0.2f) * 2f) / 3.5f, len * 1.75f * e.fin(Interp.circleOut), e.rotation + 90 + i * 90);
            }),
            line = new Effect(30f, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.75f);
                Lines.stroke(2 * e.fout());
                Angles.randLenVectors(e.id, 6, 3 + e.rotation * e.fin(), (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 14 + 4);
                });
            }),
            skyTrail = new Effect(22, e -> {
                Draw.color(Pal.techBlue, Pal.gray, e.fin() * 0.6f);

                rand.setSeed(e.id);
                Angles.randLenVectors(e.id, 3, e.finpow() * 13, e.rotation - 180, 30f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 6, rand.random(2, 4) * e.foutpow(), e.rotation);
                });
            }),
            circle = new Effect(25f, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.65f);
                Lines.stroke(Mathf.clamp(e.rotation / 18f, 2, 6) * e.fout());
                Lines.circle(e.x, e.y, e.rotation * e.finpow());
            }),
            circleOut = new Effect(60f, 500f, e -> {
                Lines.stroke(2.5f * e.fout(), e.color);
                Lines.circle(e.x, e.y, e.rotation * e.fin(Interp.pow3Out));
            }),
            circleOutQuick = new Effect(30f, 500f, e -> {
                Lines.stroke(2.5f * e.fout(), e.color);
                Lines.circle(e.x, e.y, e.rotation * e.fin(Interp.pow3Out));
            }),
            circleOutLong = new Effect(120f, 500f, e -> {
                Lines.stroke(2.5f * e.fout(), e.color);
                Lines.circle(e.x, e.y, e.rotation * e.fin(Interp.pow3Out));
            }),
            circleSplash = new Effect(26f, e -> {
                Draw.color(Color.white, e.color, e.fin() + 0.15f);
                Angles.randLenVectors(e.id, 4, 3 + 23 * e.fin(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 3f);
                    Drawf.light(e.x + x, e.y + y, e.fout() * 3.5f, e.color, 0.7f);
                });
            }),
            hyperCloud = new Effect(140f, 400f, e -> {
                Angles.randLenVectors(e.id, 20, e.finpow() * 160f, (x, y) -> {
                    float size = e.fout() * 15f;
                    Draw.color(e.color, Color.lightGray, e.fin());
                    Fill.circle(e.x + x, e.y + y, size / 2f);
                    Drawf.light(e.x + x, e.y + y, e.fout() * size, e.color, 0.7f);
                });
            }),
            hyperExplode = new Effect(30f, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.75f);
                Lines.stroke(1.3f * e.fslope());
                Lines.circle(e.x, e.y, 45f * e.fin());
                Angles.randLenVectors(e.id + 1, 5, 8f + 60 * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 7f);
                });
                Drawf.light(e.x, e.y, e.fout() * 70f, e.color, 0.7f);
            }),
            ultFireBurn = new Effect(25f, e -> {
                Draw.color(Pal.techBlue, Color.gray, e.fin() * 0.75f);

                Angles.randLenVectors(e.id, 2, 2f + e.fin() * 7f, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 0.2f + e.fout() * 1.5f, 45);
                });
            }).layer(Layer.bullet + 1),
            boolSelector = new Effect(0, 0, e -> {
            }),
            lightningHitSmall = new Effect(Fx.chainLightning.lifetime, e -> {
                Draw.color(Color.white, e.color, e.fin() + 0.25f);

                e.scaled(7f, s -> {
                    Lines.stroke(0.5f + s.fout());
                    Lines.circle(e.x, e.y, s.fin() * (e.rotation + 12f));
                });

                Lines.stroke(0.75f + e.fout());

                Angles.randLenVectors(e.id, 6, e.fin() * e.rotation + 7f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 4 + 2f);
                });

                Fill.circle(e.x, e.y, 2.5f * e.fout());
            }),
            lightningFade = (new Effect(PositionLightning.lifetime, 1200f, e -> {
                if (!(e.data instanceof Vec2Seq v)) return;

                e.lifetime = v.size() < 2 ? 0 : 1000;
                int strokeOffset = (int) e.rotation;

                if (v.size() > strokeOffset + 1 && strokeOffset > 0 && v.size() > 2) {
                    v.removeRange(0, v.size() - strokeOffset - 1);
                }

                if (!state.isPaused() && v.any()) {
                    v.remove(0);
                }

                if (v.size() < 2) return;

                Vec2 data = v.peekTmp();
                float stroke = data.x;
                float fadeOffset = data.y;

                Draw.color(e.color);
                for (int i = 1; i < v.size() - 1; i++) {
                    Lines.stroke(Mathf.clamp((i + fadeOffset / 2f) / v.size() * (strokeOffset - (v.size() - i)) / strokeOffset) * stroke);
                    Vec2 from = v.setVec2(i - 1, Tmp.v1);
                    Vec2 to = v.setVec2(i, Tmp.v2);
                    Lines.line(from.x, from.y, to.x, to.y, false);
                    Fill.circle(from.x, from.y, Lines.getStroke() / 2);
                }

                Vec2 last = v.tmpVec2(v.size() - 2);
                Fill.circle(last.x, last.y, Lines.getStroke() / 2);
            })).layer(Layer.effect - 0.001f),
            techBlueCircleSplash = new Effect(26f, e -> {
                Draw.color(Pal.techBlue);
                Angles.randLenVectors(e.id, 4, 3 + 23 * e.fin(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 3f);
                    Drawf.light(e.x + x, e.y + y, e.fout() * 3.5f, Pal.techBlue, 0.7f);
                });
            }),
            techBlueExplosion = new Effect(40, e -> {
                Draw.color(Pal.techBlue);
                e.scaled(20, i -> {
                    Lines.stroke(3f * i.foutpow());
                    Lines.circle(e.x, e.y, 3f + i.finpow() * 80f);
                });

                Lines.stroke(e.fout());
                Angles.randLenVectors(e.id + 1, 8, 1f + 60f * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));

                Draw.color(Color.gray);

                Angles.randLenVectors(e.id, 5, 2f + 70 * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f));

                Drawf.light(e.x, e.y, e.fout(Interp.pow2Out) * 100f, Pal.techBlue, 0.7f);
            }),
            techBlueCharge = new Effect(130f, e -> {
                rand.setSeed(e.id);
                Angles.randLenVectors(e.id, 12, 140f * e.fout(Interp.pow3Out), (x, y) -> {
                    Draw.color(Pal.techBlue);
                    float rad = rand.random(9f, 18f);
                    float scl = rand.random(0.6f, 1f);
                    float dx = e.x + scl * x, dy = e.y + scl * y;
                    Fill.circle(dx, dy, e.fin() * rad);
                    Draw.color(Pal.techBlue);
                    Draw.z(EFFECT_MASK);
                    Fill.circle(dx, dy, e.fin() * rad / 1.8f);
                    Draw.z(EFFECT_BOTTOM);
                    Fill.circle(dx, dy, e.fin() * rad / 1.8f);
                    Draw.z(Layer.effect);
                    Drawf.light(dx, dy, e.fin() * rad * 1.5f, Pal.techBlue, 0.7f);
                });
            }),
            techBlueChargeBegin = new Effect(130f, e -> {
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fin() * 32);
                Lines.stroke(e.fin() * 3.7f);
                Lines.circle(e.x, e.y, e.fout() * 80);
                Draw.z(EFFECT_MASK);
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fin() * 20);

                Draw.z(EFFECT_BOTTOM);
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fin() * 22);
                Drawf.light(e.x, e.y, e.fin() * 35f, Pal.techBlue, 0.7f);
            }),
            largeTechBlueHitCircle = new Effect(20f, e -> {
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 44);
                Angles.randLenVectors(e.id, 5, 60f * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 8));
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 30);
                Drawf.light(e.x, e.y, e.fout() * 55f, Pal.techBlue, 0.7f);
            }),
            largeTechBlueHit = new Effect(50, e -> {
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 44);
                Lines.stroke(e.fout() * 3.2f);
                Lines.circle(e.x, e.y, e.fin() * 80);
                Lines.stroke(e.fout() * 2.5f);
                Lines.circle(e.x, e.y, e.fin() * 50);
                Lines.stroke(e.fout() * 3.2f);
                Angles.randLenVectors(e.id, 30, 18 + 80 * e.fin(), (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 14 + 5);
                });

                Draw.z(EFFECT_MASK);
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 30);
                Drawf.light(e.x, e.y, e.fout() * 80f, Pal.techBlue, 0.7f);

                Draw.z(EFFECT_BOTTOM);
                Fill.circle(e.x, e.y, e.fout() * 31);
                Draw.z(Layer.effect - 0.0001f);
            }).layer(Layer.effect - 0.0001f),
            mediumTechBlueHit = new Effect(23, e -> {
                Draw.color(Pal.techBlue);
                Lines.stroke(e.fout() * 2.8f);
                Lines.circle(e.x, e.y, e.fin() * 60);
                Lines.stroke(e.fout() * 2.12f);
                Lines.circle(e.x, e.y, e.fin() * 35);

                Lines.stroke(e.fout() * 2.25f);
                Angles.randLenVectors(e.id, 9, 7f + 60f * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 4f + e.fout() * 12f));

                Fill.circle(e.x, e.y, e.fout() * 22);
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 14);
                Drawf.light(e.x, e.y, e.fout() * 80f, Pal.techBlue, 0.7f);
            }),
            techBlueSmokeBig = new Effect(30f, e -> {
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 32);
                Draw.color(Pal.techBlue);
                Fill.circle(e.x, e.y, e.fout() * 20);
                Drawf.light(e.x, e.y, e.fout() * 36f, Pal.techBlue, 0.7f);
            }),
            techBlueShootBig = new Effect(40f, 100, e -> {
                Draw.color(Pal.techBlue);
                Lines.stroke(e.fout() * 3.7f);
                Lines.circle(e.x, e.y, e.fin() * 100 + 15);
                Lines.stroke(e.fout() * 2.5f);
                Lines.circle(e.x, e.y, e.fin() * 60 + 15);
                Angles.randLenVectors(e.id, 15, 7f + 60f * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 4f + e.fout() * 16f));
                Drawf.light(e.x, e.y, e.fout() * 120f, Pal.techBlue, 0.7f);
            }),
            crossBlast = new Effect(35, 140, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.55f);
                Drawf.light(e.x, e.y, e.fout() * 70, e.color, 0.7f);

                e.scaled(10f, i -> {
                    Lines.stroke(1.35f * i.fout());
                    Fill.circle(e.x, e.y, 49 * i.finpow());
                });

                rand.setSeed(e.id);
                float sizeDiv = 35;
                float randL = rand.random(sizeDiv);

                for (int i = 0; i < 4; i++) {
                    Drawn.tri(e.x, e.y, 3.5f * (e.fout() * 3f + 1) / 4 * (e.fout(Interp.pow3In) + 0.5f) / 1.5f, (sizeDiv + randL) * Mathf.curve(e.fin(), 0, 0.05f) * e.fout(Interp.pow3), i * 90);
                }
            }),
            crossBlast_45 = new Effect(35, 140, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.55f);
                Drawf.light(e.x, e.y, e.fout() * 70, e.color, 0.7f);

                e.scaled(10f, i -> {
                    Lines.stroke(1.35f * i.fout());
                    Fill.circle(e.x, e.y, 55 * i.finpow());
                });

                rand.setSeed(e.id);
                float sizeDiv = 60;
                float randL = rand.random(sizeDiv);

                for (int i = 0; i < 4; i++) {
                    Drawn.tri(e.x, e.y, 5.85f * (e.fout() * 3f + 1) / 4 * (e.fout(Interp.pow3In) + 0.5f) / 1.5f, (sizeDiv + randL) * Mathf.curve(e.fin(), 0, 0.05f) * e.fout(Interp.pow3), i * 90 + 45);
                }
            }),
            energyUnitBlast = new Effect(150F, 1600f, e -> {
                float rad = e.rotation;
                rand.setSeed(e.id);

                Draw.color(Color.white, e.color, e.fin() / 5 + 0.6f);
                float circleRad = e.fin(Interp.circleOut) * rad;
                Lines.stroke(12 * e.fout());
                Lines.circle(e.x, e.y, circleRad);

                e.scaled(120f, i -> {
                    Fill.circle(i.x, i.y, rad * i.fout() / 2);
                    Lines.stroke(18 * i.fout());
                    Lines.circle(i.x, i.y, i.fin(Interp.circleOut) * rad * 1.2f);

                    Angles.randLenVectors(i.id, (int) (rad / 4), rad / 6, rad * (1 + i.fout(Interp.circleOut)) / 2f, (x, y) -> {
                        float angle = Mathf.angle(x, y);
                        float width = i.foutpowdown() * rand.random(rad / 8, rad / 10);
                        float length = rand.random(rad / 2, rad) * i.fout(Interp.circleOut);

                        Draw.color(i.color);
                        Drawn.tri(i.x + x, i.y + y, width, rad / 8 * i.fout(Interp.circleOut), angle - 180);
                        Drawn.tri(i.x + x, i.y + y, width, length, angle);

                        Draw.color(Color.black);

                        width *= i.fout();

                        Drawn.tri(i.x + x, i.y + y, width / 2, rad / 8 * i.fout(Interp.circleOut) * 0.9f * i.fout(), angle - 180);
                        Drawn.tri(i.x + x, i.y + y, width / 2, length / 1.5f * i.fout(), angle);
                    });

                    Draw.color(Color.black);
                    Fill.circle(i.x, i.y, rad * i.fout() * 0.375f);
                });

                Drawf.light(e.x, e.y, rad * e.fout() * 4f * Mathf.curve(e.fin(), 0f, 0.05f), e.color, 0.7f);
            }).layer(Layer.effect + 0.001f),
            triSpark1 = new Effect(26, e -> {
                rand.setSeed(e.id);
                Draw.color(Pal.techBlue, Color.white, e.fin());
                Angles.randLenVectors(e.id, 3, 3f + 24f * e.fin(), 5f, (x, y) -> {
                    float randN = rand.random(120f);
                    Fill.poly(e.x + x, e.y + y, 3, e.fout() * 8f * rand.random(0.8f, 1.2f), e.rotation + randN * e.fin());
                });
            }),
            triSpark2 = new Effect(26, e -> {
                rand.setSeed(e.id);
                Draw.color(HIPal.ancient, Color.white, e.fin());
                Angles.randLenVectors(e.id, 3, 3f + 24f * e.fin(), 5f, (x, y) -> {
                    float randN = rand.random(120f);
                    Fill.poly(e.x + x, e.y + y, 3, e.fout() * 8f * rand.random(0.8f, 1.2f), e.rotation + randN * e.fin());
                });
            }),
            eruptorBurn = new Effect(30f, e -> {
                Draw.color(Pal.slagOrange);
                Angles.randLenVectors(e.id, 6, 64 * e.fin(), e.rotation, 20f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Angles.angle(x, y), 8f * e.fout());
                });
            }),
            hugeTrail = new Effect(40f, e -> {
                Draw.color(e.color);
                Draw.alpha(e.fout(0.85f) * 0.85f);
                Angles.randLenVectors(e.id, 6, 2f + e.rotation * 5f * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout(Interp.pow3Out) * e.rotation);
                });
            }),
            hugeSmokeGray = new Effect(40f, e -> {
                Draw.color(Color.gray, Color.darkGray, e.fin());
                Angles.randLenVectors(e.id, 6, 2f + 19f * e.finpow(), (x, y) -> Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 2f));
                e.scaled(25f, i -> Angles.randLenVectors(e.id, 6, 2f + 19f * i.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, i.fout() * 4f)));
            }),
            hugeSmoke = new Effect(40f, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 6, 2f + 19f * e.finpow(), (x, y) -> Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 2f));
                e.scaled(25f, i -> Angles.randLenVectors(e.id, 6, 2f + 19f * i.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, i.fout() * 4f)));
            }),
            hugeSmokeLong = new Effect(120f, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 6, 2f + 19f * e.finpow(), (x, y) -> Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 2f));
                e.scaled(25f, i -> Angles.randLenVectors(e.id, 6, 2f + 19f * i.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, i.fout() * 4f)));
            }),
            square45_4_45 = new Effect(45f, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 5, 20f * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 4f * e.fout(), 45);
                    Drawf.light(e.x + x, e.y + y, e.fout() * 6f, e.color, 0.7f);
                });
            }),
            square45_6_45 = new Effect(45f, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.6f);
                Angles.randLenVectors(e.id, 6, 27f * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 5f * e.fout(), 45);
                    Drawf.light(e.x + x, e.y + y, e.fout() * 9F, e.color, 0.7f);
                });
            }),
            square45_6_45_Charge = new Effect(90f, e -> {
                Draw.color(e.color, Color.white, e.fin() * 0.6f);
                Angles.randLenVectors(e.id, 12, 60 * e.fout(Interp.pow4Out), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 5f * e.fin(), 45);
                    Drawf.light(e.x + x, e.y + y, e.fin() * 9f, e.color, 0.7f);
                });

                Lines.stroke(2f * e.fin());
                Lines.circle(e.x, e.y, 80 * e.fout(Interp.pow5Out));
            }),
            square45_8_45 = new Effect(45f, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.6f);
                Angles.randLenVectors(e.id, 7, 34f * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 8f * e.fout(), 45);
                    Drawf.light(e.x + x, e.y + y, e.fout() * 12f, e.color, 0.7f);
                });
            }),
            posLightning = (new Effect(PositionLightning.lifetime, 1200f, e -> {
                if (!(e.data instanceof Vec2Seq v)) return;

                Draw.color(e.color, Color.white, e.fout() * 0.6f);

                Lines.stroke(e.rotation * e.fout());

                Fill.circle(v.firstTmp().x, v.firstTmp().y, Lines.getStroke() / 2f);

                for (int i = 0; i < v.size() - 1; i++) {
                    Vec2 cur = v.setVec2(i, Tmp.v1);
                    Vec2 next = v.setVec2(i + 1, Tmp.v2);

                    Lines.line(cur.x, cur.y, next.x, next.y, false);
                    Fill.circle(next.x, next.y, Lines.getStroke() / 2f);
                }
            })).layer(Layer.effect - 0.001f),
            chainLightningFade = new Effect(220f, 500f, e -> {
                if (!(e.data instanceof Position p)) return;
                float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
                Tmp.v1.set(p).sub(e.x, e.y).nor();

                e.lifetime = dst * 0.3f;
                float normx = Tmp.v1.x, normy = Tmp.v1.y;
                float range = e.rotation;
                int links = Mathf.ceil(dst / range);
                float spacing = dst / links;

                Lines.stroke(2.5f * Mathf.curve(e.fout(), 0, 0.7f));
                Draw.color(e.color, Color.white, e.fout() * 0.6f);

                Lines.beginLine();

                Fill.circle(e.x, e.y, Lines.getStroke() / 2);
                Lines.linePoint(e.x, e.y);

                rand.setSeed(e.id);

                float fin = Mathf.curve(e.fin(), 0, lightningAlign);
                int i;
                float nx = e.x, ny = e.y;
                for (i = 0; i < (int) (links * fin); i++) {
                    if (i == links - 1) {
                        nx = tx;
                        ny = ty;
                    } else {
                        float len = (i + 1) * spacing;
                        Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                        nx = e.x + normx * len + Tmp.v1.x;
                        ny = e.y + normy * len + Tmp.v1.y;
                    }

                    Lines.linePoint(nx, ny);
                }

                if (i < links) {
                    float f = Mathf.clamp(fin * links % 1);
                    float len = (i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                    Tmp.v2.set(nx, ny);
                    if (i == links - 1) Tmp.v2.lerp(tx, ty, f);
                    else Tmp.v2.lerp(e.x + (normx * len + Tmp.v1.x), e.y + (normy * len + Tmp.v1.y), f);

                    Lines.linePoint(Tmp.v2.x, Tmp.v2.y);
                    Fill.circle(Tmp.v2.x, Tmp.v2.y, Lines.getStroke() / 2);
                }

                Lines.endLine();
            }).followParent(false),
            chainLightningFadeReversed = new Effect(220f, 500f, e -> {
                if (!(e.data instanceof Position p)) return;

                float tx = e.x, ty = e.y, dst = Mathf.dst(p.getX(), p.getY(), tx, ty);
                Tmp.v1.set(e.x, e.y).sub(p).nor();

                e.lifetime = dst * 0.3f;
                float normx = Tmp.v1.x, normy = Tmp.v1.y;
                float range = e.rotation;
                int links = Mathf.ceil(dst / range);
                float spacing = dst / links;

                Lines.stroke(2.5f * Mathf.curve(e.fout(), 0, 0.7f));
                Draw.color(e.color, Color.white, e.fout() * 0.6f);

                Lines.beginLine();

                Fill.circle(p.getX(), p.getY(), Lines.getStroke() / 2);
                Lines.linePoint(p);

                rand.setSeed(e.id);

                float fin = Mathf.curve(e.fin(), 0, lightningAlign);
                int i;
                float nx = p.getX(), ny = p.getY();
                for (i = 0; i < (int) (links * fin); i++) {
                    if (i == links - 1) {
                        nx = tx;
                        ny = ty;
                    } else {
                        float len = (i + 1) * spacing;
                        Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                        nx = p.getX() + normx * len + Tmp.v1.x;
                        ny = p.getY() + normy * len + Tmp.v1.y;
                    }

                    Lines.linePoint(nx, ny);
                }

                if (i < links) {
                    float f = Mathf.clamp(fin * links % 1);
                    float len = (i + 1) * spacing;
                    Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                    Tmp.v2.set(nx, ny);
                    if (i == links - 1) Tmp.v2.lerp(tx, ty, f);
                    else Tmp.v2.lerp(p.getX() + (normx * len + Tmp.v1.x), p.getY() + (normy * len + Tmp.v1.y), f);

                    Lines.linePoint(Tmp.v2.x, Tmp.v2.y);
                    Fill.circle(Tmp.v2.x, Tmp.v2.y, Lines.getStroke() / 2);
                }

                Lines.endLine();
            }).followParent(false),
            lightningHitLarge = new Effect(50f, 180f, e -> {
                Draw.color(e.color);
                Drawf.light(e.x, e.y, e.fout() * 90f, e.color, 0.7f);
                e.scaled(25f, t -> {
                    Lines.stroke(3f * t.fout());
                    Lines.circle(e.x, e.y, 3f + t.fin(Interp.pow3Out) * 80f);
                });
                Fill.circle(e.x, e.y, e.fout() * 8f);
                Angles.randLenVectors(e.id + 1, 4, 1f + 60f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5f));

                Draw.color(Color.gray);
                Angles.randLenVectors(e.id, 8, 2f + 30f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f));
            }),
            collapserBulletExplode = new Effect(300F, 1600f, e -> {
                float rad = 150f;
                rand.setSeed(e.id);

                Draw.color(Color.white, e.color, e.fin() + 0.6f);
                float circleRad = e.fin(Interp.circleOut) * rad * 4f;
                Lines.stroke(12 * e.fout());
                Lines.circle(e.x, e.y, circleRad);
                for (int i = 0; i < 24; i++) {
                    Tmp.v1.set(1, 0).setToRandomDirection(rand).scl(circleRad);
                    Drawn.tri(e.x + Tmp.v1.x, e.y + Tmp.v1.y, rand.random(circleRad / 16, circleRad / 12) * e.fout(), rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180);
                }

                Draw.blend(Blending.additive);
                Draw.z(Layer.effect + 0.1f);

                Fill.light(e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear, Tmp.c1.set(Draw.getColor()).a(e.fout(Interp.pow10Out)));
                Draw.blend();
                Draw.z(Layer.effect);

                e.scaled(120f, i -> {
                    Draw.color(Color.white, i.color, i.fin() + 0.4f);
                    Fill.circle(i.x, i.y, rad * i.fout());
                    Lines.stroke(18 * i.fout());
                    Lines.circle(i.x, i.y, i.fin(Interp.circleOut) * rad * 1.2f);
                    Angles.randLenVectors(i.id, 40, rad / 3, rad * i.fin(Interp.pow2Out), (x, y) -> {
                        Lines.lineAngle(i.x + x, i.y + y, Mathf.angle(x, y), i.fslope() * 25 + 10);
                    });

                    Angles.randLenVectors(i.id, (int) (rad / 4), rad / 6, rad * (1 + i.fout(Interp.circleOut)) / 1.5f, (x, y) -> {
                        float angle = Mathf.angle(x, y);
                        float width = i.foutpowdown() * rand.random(rad / 6, rad / 3);
                        float length = rand.random(rad / 2, rad * 5) * i.fout(Interp.circleOut);

                        Draw.color(i.color);
                        Drawn.tri(i.x + x, i.y + y, width, rad / 3 * i.fout(Interp.circleOut), angle - 180);
                        Drawn.tri(i.x + x, i.y + y, width, length, angle);

                        Draw.color(Color.black);

                        width *= i.fout();

                        Drawn.tri(i.x + x, i.y + y, width / 2, rad / 3 * i.fout(Interp.circleOut) * 0.9f * i.fout(), angle - 180);
                        Drawn.tri(i.x + x, i.y + y, width / 2, length / 1.5f * i.fout(), angle);
                    });

                    Draw.color(Color.black);
                    Fill.circle(i.x, i.y, rad * i.fout() * 0.75f);
                });

                Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f);
            }).layer(Layer.effect + 0.001f),
            hitSpark = new Effect(45, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.3f);
                Lines.stroke(e.fout() * 1.6f);

                rand.setSeed(e.id);
                Angles.randLenVectors(e.id, 8, e.finpow() * 20f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * rand.random(1.95f, 4.25f) + 1f);
                });
            }),
            hitSparkLarge = new Effect(40, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.3f);
                Lines.stroke(e.fout() * 1.6f);

                rand.setSeed(e.id);
                Angles.randLenVectors(e.id, 18, e.finpow() * 27f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * rand.random(4, 8) + 2f);
                });
            }),
            hitSparkHuge = new Effect(70, e -> {
                Draw.color(e.color, Color.white, e.fout() * 0.3f);
                Lines.stroke(e.fout() * 1.6f);

                rand.setSeed(e.id);
                Angles.randLenVectors(e.id, 26, e.finpow() * 65f, (x, y) -> {
                    float ang = Mathf.angle(x, y);
                    Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * rand.random(6, 9) + 3f);
                });
            }),
            shareDamage = new Effect(45f, e -> {
                if (!(e.data instanceof Number n)) return;
                Draw.color(e.color);
                Draw.alpha((n).floatValue() * e.fout());
                Fill.square(e.x, e.y, e.rotation);
            }),
            lightningSpark = new Effect(Fx.chainLightning.lifetime, e -> {
                Draw.color(Color.white, e.color, e.fin() + 0.25f);

                Lines.stroke(0.65f + e.fout());

                Angles.randLenVectors(e.id, 3, e.fin() * e.rotation + 6f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 4 + 2f));

                Fill.circle(e.x, e.y, 2.5f * e.fout());
            }),
            attackWarningPos = new Effect(120f, 2000f, e -> {
                if (!(e.data instanceof Position p)) return;

                e.lifetime = e.rotation;

                Draw.color(e.color);
                TextureRegion arrowRegion = atlas.find(HeavyIndustryMod.modName + "-jump-gate-arrow");
                float scl = Mathf.curve(e.fout(), 0f, 0.1f);
                Lines.stroke(2 * scl);
                Lines.line(p.getX(), p.getY(), e.x, e.y);
                Fill.circle(p.getX(), p.getY(), Lines.getStroke());
                Fill.circle(e.x, e.y, Lines.getStroke());
                Tmp.v1.set(e.x, e.y).sub(p).scl(e.fin(Interp.pow2In)).add(p);
                Draw.rect(arrowRegion, Tmp.v1.x, Tmp.v1.y, arrowRegion.width * scl * Draw.scl, arrowRegion.height * scl * Draw.scl, p.angleTo(e.x, e.y) - 90f);
            }),
            attackWarningRange = new Effect(120f, 2000f, e -> {
                Draw.color(e.color);
                Lines.stroke(2 * e.fout());
                Lines.circle(e.x, e.y, e.rotation);

                for (float i = 0.75f; i < 1.5f; i += 0.25f) {
                    Lines.spikes(e.x, e.y, e.rotation / i, e.rotation / 10f, 4, e.time);
                    Lines.spikes(e.x, e.y, e.rotation / i / 1.5f, e.rotation / 12f, 4, -e.time * 1.25f);
                }

                TextureRegion arrowRegion = atlas.find(HeavyIndustryMod.modName + "-jump-gate-arrow");
                float scl = Mathf.curve(e.fout(), 0f, 0.1f);

                for (int l = 0; l < 4; l++) {
                    float angle = 90 * l;
                    float regSize = e.rotation / 150f;
                    for (int i = 0; i < 4; i++) {
                        Tmp.v1.trns(angle, (i - 4) * tilesize * e.rotation / tilesize / 4);
                        float f = (100 - (Time.time - 25 * i) % 100) / 100;

                        Draw.rect(arrowRegion, e.x + Tmp.v1.x, e.y + Tmp.v1.y, arrowRegion.width * regSize * f * scl, arrowRegion.height * regSize * f * scl, angle - 90);
                    }
                }
            }),
            trailToGray = new Effect(50f, e -> {
                Draw.color(e.color, Color.gray, e.fin());
                Angles.randLenVectors(e.id, 2, tilesize * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.rotation * e.fout()));
            }),
            laserBeam = new Effect(30f, e -> {
                Rand rand = new Rand(e.id);
                Draw.color(e.color, Color.white, e.fout() * 0.66f);
                Draw.alpha(0.55f * e.fout() + 0.5f);
                Angles.randLenVectors(e.id, 2, 4f + e.finpow() * 17f, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * rand.random(2.5f, 4), 45);
                });
            }),
            resonance = new Effect(30f, e -> {
                Rand rand = new Rand(e.id);
                Draw.color(e.color, Color.white, e.fout() * 0.66f);
                Draw.alpha(0.55f * e.fout() + 0.5f);
                Angles.randLenVectors(e.id, 2, 4f + e.finpow() * 17f, (x, y) -> {
                    Fill.square(e.x + x, e.y + y, e.fout() * rand.random(2.5f, 4));
                });
            }),
            implosion = new Effect(30f, e -> {
                Rand rand = new Rand(e.id);
                Draw.color(e.color, Color.white, e.fout() * 0.66f);
                Draw.alpha(0.55f * e.fout() + 0.5f);
                Angles.randLenVectors(e.id, 4, 4f + e.finpow() * 17f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 3, e.fout() * rand.random(2.5f, 4), rand.random(360));
                });
            }),
            crit = new Effect(120f, e -> {
                Tmp.v1.trns(e.rotation + 90f, 0f, 48f * e.fin(Interp.pow2Out));

                Draw.color(e.color, e.fout());
                Angles.randLenVectors(e.id, 6, 24f, (x, y) -> {
                    float rot = Mathf.randomSeed((long) (e.id + x + y), 360);
                    float tx = x * e.fin(Interp.pow2Out), ty = y * e.fin(Interp.pow2Out);
                    Drawm.plus(e.x + tx + Tmp.v1.x, e.y + ty + Tmp.v1.y, 4f, rot);
                });
            }),
            critPierce = new Effect(20f, e -> {
                float rot = e.rotation - 90f, fin = e.fin(Interp.pow5Out), end = e.lifetime - 6f;
                float fout = 1f - Interp.pow2Out.apply(Mathf.curve(e.time, end, e.lifetime));
                float width = fin * fout;

                e.scaled(7f, s -> {
                    Lines.stroke(0.5f + s.fout());
                    Draw.color(Color.white, e.color, s.fin());
                    Lines.circle(e.x + Angles.trnsx(rot, 0f, 5f * fin), e.y + Angles.trnsy(rot, 0f, 5f * fin), s.fin() * 6f);
                });

                Draw.color(Color.white, e.color, Mathf.curve(e.time, 0f, end));

                Fill.quad(
                        e.x + Angles.trnsx(rot, 0f, 2f * fin), e.y + Angles.trnsy(rot, 0f, 2f * fin),
                        e.x + Angles.trnsx(rot, 4f * width, -4f * fin), e.y + Angles.trnsy(rot, 4f * width, -4f * fin),
                        e.x + Angles.trnsx(rot, 0f, 8f * fin), e.y + Angles.trnsy(rot, 0f, 8f * fin),
                        e.x + Angles.trnsx(rot, -4f * width, -4f * fin), e.y + Angles.trnsy(rot, -4f * width, -4f * fin)
                );
            }),
            miniCrit = new Effect(90f, e -> {
                Tmp.v1.trns(e.rotation + 90f, 0f, 32f * e.fin(Interp.pow2Out));

                Draw.color(e.color, e.fout());
                Angles.randLenVectors(e.id, 2, 18f, (x, y) -> {
                    float rot = Mathf.randomSeed((long) (e.id + x + y), 360);
                    float tx = x * e.fin(Interp.pow2Out);
                    float ty = y * e.fin(Interp.pow2Out);
                    Drawm.plus(e.x + tx + Tmp.v1.x, e.y + ty + Tmp.v1.y, 3f, rot);
                });
            }),
            critTrailFade = new Effect(400f, e -> {
                if (!(e.data instanceof CritTrail trail)) return;

                e.lifetime = trail.length * 1.4f;

                if (!state.isPaused()) {
                    trail.shorten();
                }
                trail.drawCap(e.color, e.rotation);
                trail.draw(e.color, e.rotation);
            }),
            bigExplosionStone = new Effect(80f, e -> Angles.randLenVectors(e.id, 22, e.fin() * 50f, (x, y) -> {
                float elevation = Interp.bounceIn.apply(e.fout() - 0.3f) * (Mathf.randomSeed((int) Angles.angle(x, y), 30f, 60f));

                Draw.z(Layer.power + 0.1f);
                Draw.color(Pal.shadow);
                Fill.circle(e.x + x, e.y + y, 12f);

                Draw.z(Layer.power + 0.2f);
                Draw.color(e.color);
                Fill.circle(e.x + x, e.y + y + elevation, 12f);
            })),
            explosionStone = new Effect(60f, e -> Angles.randLenVectors(e.id, 12, e.fin() * 50f, (x, y) -> {
                float elevation = Interp.bounceIn.apply(e.fout() - 0.3f) * (Mathf.randomSeed((int) Angles.angle(x, y), 30f, 60f));

                Draw.z(Layer.power + 0.1f);
                Draw.color(Pal.shadow);
                Fill.circle(e.x + x, e.y + y, 12f);

                Draw.z(Layer.power + 0.2f);
                Draw.color(e.color);
                Fill.circle(e.x + x, e.y + y + elevation, 12f);
            })),
            fellStone = new Effect(120f, e -> {
                if (!(e.data instanceof HailStoneData data)) return;

                v7.trns(Mathf.randomSeed(e.id) * 360, data.fallTime / 2 + Mathf.randomSeed(e.id + 1) * data.fallTime);
                float scl = Interp.bounceIn.apply(e.fout() - 0.3f);
                float rot = v7.angle();
                float x = e.x + (v7.x * e.finpow()), y = e.y + (v7.y * e.finpow());

                Draw.z(Layer.power + 0.1f);
                Drawm.shadow(data.region, x, y, rot, Math.min(e.fout(), Pal.shadow.a));

                Draw.z(Layer.power + 0.2f);
                Draw.color(e.color);
                Draw.alpha(e.fout());
                Draw.rect(data.region, x, y + (scl * data.fallTime / 2), rot);
            }),
            fellStoneAghanite = new Effect(120f, e -> {
                if (!(e.data instanceof HailStoneData data)) return;

                rand.setSeed(e.id);
                v7.trns(e.rotation + rand.range(30f), data.fallTime / 2f + rand.random(data.fallTime));
                float scl = Interp.bounceIn.apply(e.fout() - 0.3f);
                float rot = v7.angle();
                float x = e.x + (v7.x * e.finpow()), y = e.y + (v7.y * e.finpow());

                Draw.z(Layer.power + 0.1f);
                Drawm.shadow(data.region, x, y, rot, Math.min(e.fout(), Pal.shadow.a));

                Draw.z(Layer.power + 0.2f);
                Draw.color(e.color);
                Draw.alpha(e.fout());
                Draw.rect(data.region, x, y + (scl * data.fallTime / 2f), rot);
            }),
            staticStone = new Effect(250f, e -> {
                if (!(e.data instanceof HailStoneData data)) return;

                Draw.z(Layer.power + 0.1f);
                Draw.color(e.color);
                Draw.alpha(e.fout());
                Draw.rect(data.region, e.x, e.y, Mathf.randomSeed(e.id) * 360);
            }),
            windTail = new Effect(100f, e -> {
                Draw.color(Color.white);
                Draw.z(Layer.space - 0.1f);

                rand.setSeed(e.id);

                float rx = rand.random(-1, 1) + 0.01f, ry = rand.random(-1, 1) - 0.01f, dis = rand.random(120, 200);
                float force = rand.random(10, 40);
                float z = rand.random(0, 10);
                Vec3[] windTailPoints = new Vec3[12];

                for (int i = 0; i < 12; i++) {
                    float scl = (e.fin() - i * 0.05f);
                    float x = (scl * dis) + Mathf.cos(scl * 10) * force * rx;
                    float y = Mathf.sin(scl * 10) * force * ry;

                    v8.trns(e.rotation, x, y);
                    v8.add(e.x, e.y);
                    v8.add(Math3D.xOffset(e.x, z), Math3D.yOffset(e.y, z));

                    windTailPoints[i] = new Vec3(v8.x, v8.y, e.fslope());
                }

                for (int i = 0; i < windTailPoints.length - 1; i++) {
                    Vec3 v1 = windTailPoints[i];
                    Vec3 v2 = windTailPoints[i + 1];

                    Draw.alpha(Mathf.clamp(v1.z, 0.04f, 0.1f));
                    Lines.stroke(v1.z);
                    Lines.line(v1.x, v1.y, v2.x, v2.y);
                }
            }),
            gasLeak = new Effect(90, e -> {
                if (!(e.data instanceof Number n)) return;
                float param = n.floatValue();

                Draw.color(e.color, Color.lightGray, e.fin());
                Draw.alpha(0.75f * param * e.fout());

                Angles.randLenVectors(e.id, 1, 8f + e.fin() * (param + 3), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.55f + e.fslope() * 4.5f);
                });
            }),
            moveParticle = new Effect(90, e -> {
                Draw.color(e.color);

                Tmp.v1.setZero();
                if (e.data instanceof Number n) {
                    Tmp.v1.set(n.floatValue(), 0).setAngle(e.rotation);
                }

                float rad = Mathf.randomSeed(e.id, 1f, 3f) * e.fout(Interp.pow2Out);

                Fill.circle(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad);
            }),
            moveDiamondParticle = new Effect(90, e -> {
                Draw.color(e.color);

                Tmp.v1.setZero();
                if (e.data instanceof Number n) {
                    Tmp.v1.set(n.floatValue(), 0).setAngle(e.rotation);
                }

                float rad = Mathf.randomSeed(e.id, 1.6f, 3.4f) * e.fout(Interp.pow2Out);

                if (Mathf.randomSeed(e.id) > 0.5f) {
                    Lines.stroke(rad / 2f);
                    Lines.square(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad, e.fin() * Mathf.randomSeed(e.id, 180f, 480f));
                } else {
                    Fill.square(e.x + Tmp.v1.x * e.fin(), e.y + Tmp.v1.y * e.fin(), rad, e.fin() * Mathf.randomSeed(e.id, 180f, 480f));
                }
            }),
            cloudGradient = new Effect(45, e -> {
                Draw.color(e.color, 0f);

                Draw.z(Layer.flyingUnit + 1);
                Draws.gradientCircle(e.x, e.y, 14 * e.fout(), 0.6f);
            }),
            shootRecoilWave = new Effect(40, e -> {
                Draw.color(e.color);
                for (int i : Mathf.signs) {
                    Drawf.tri(e.x, e.y, 15f * e.fout(), 50f, e.rotation + 40f * i);
                }
            }),
            impactWaveSmall = new Effect(18, e -> {
                Draw.color(e.color);
                Lines.stroke(5 * e.fout());
                Lines.circle(e.x, e.y, 36 * e.fin(Interp.pow3Out));
            }),
            impactWave = new Effect(24, e -> {
                Draw.color(e.color);
                Lines.stroke(6 * e.fout());
                Lines.circle(e.x, e.y, 48 * e.fin(Interp.pow3Out));
            }),
            impactWaveBig = new Effect(30, e -> {
                Draw.color(e.color);
                Lines.stroke(6.5f * e.fout());
                Lines.circle(e.x, e.y, 55 * e.fin(Interp.pow3Out));
            }),
            impactWaveLarge = new Effect(38, e -> {
                Draw.color(e.color);
                Lines.stroke(7.3f * e.fout());
                Lines.circle(e.x, e.y, 80 * e.fin(Interp.pow3Out));
            }),
            polyParticle = new Effect(150, e -> {
                Angles.randLenVectors(e.id, 1, 24, e.rotation + 180, 20, (x, y) -> {
                    int vertices = Mathf.randomSeed((int) (e.id + x), 3, 6);
                    float step = 360f / vertices;

                    Fill.polyBegin();
                    Lines.beginLine();

                    for (int i = 0; i < vertices; i++) {
                        float radius = Mathf.randomSeed(e.id + i, 1.5f, 4f) * e.fout(Interp.pow3Out);
                        float lerp = e.fin(Interp.pow2Out);
                        float rot = Mathf.randomSeed(e.id + i, -360, 360);
                        float off = Mathf.randomSeed(e.id + i + 1, -step / 2, step / 2);
                        float angle = step * i + rot * lerp + off;
                        float dx = Angles.trnsx(angle, radius) + x * lerp;
                        float dy = Angles.trnsy(angle, radius) + y * lerp;

                        Fill.polyPoint(e.x + dx, e.y + dy);
                        Lines.linePoint(e.x + dx, e.y + dy);
                    }

                    Draw.z(Layer.bullet - 5f);
                    Draw.color(e.color, 0.5f);
                    Fill.polyEnd();

                    Draw.z(Layer.effect);
                    Lines.stroke(0.4f * e.fout(), e.color);
                    Lines.endLine(true);
                });
            }),
            impactBubbleSmall = new Effect(40, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 9, 20, (x, y) -> {
                    float s = Mathf.randomSeed((int) (e.id + x), 3f, 6f);
                    Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout());
                });
            }),
            impactBubble = new Effect(60, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 12, 26, (x, y) -> {
                    float s = Mathf.randomSeed((int) (e.id + x), 4f, 8f);
                    Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout());
                });
            }),
            impactBubbleBig = new Effect(79, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 15, 45, (x, y) -> {
                    float s = Mathf.randomSeed((int) (e.id + x), 5f, 10f);
                    Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), s * e.fout());
                });
            }),
            crystalConstructed = new Effect(60, e -> {
                Draw.color(e.color);
                Lines.stroke(4 * e.fout());

                Draw.z(Layer.effect);
                Lines.square(e.x, e.y, 12 * e.fin(), 45);
            }),
            hadronReconstruct = new Effect(60, e -> {
                Draw.color(Pal.reactorPurple);
                Lines.stroke(3f * e.fout());

                Draw.z(Layer.effect);
                Angles.randLenVectors(e.id, 3, 12, (x, y) -> {
                    Lines.square(e.x + x, e.y + y, (14 + Mathf.randomSeed(e.id + (int) (x * y), -2, 2)) * e.fin(), e.fin() * Mathf.randomSeed(e.id + (int) (x * y), -90, 90));
                });
            }),
            polymerConstructed = new Effect(60, e -> {
                Draw.color(Pal.reactorPurple);
                Lines.stroke(6 * e.fout());

                Lines.square(e.x, e.y, 30 * e.fin());
                Lines.square(e.x, e.y, 30 * e.fin(), 45);
            }),
            spreadField = new Effect(60, e -> {
                Draw.color(e.color);
                Lines.stroke(8 * e.fout());

                Lines.square(e.x, e.y, 38 * e.fin(Interp.pow2Out));
                Lines.square(e.x, e.y, 38 * e.fin(Interp.pow2Out), 45);
            }),
            forceField = new Effect(45, e -> {
                Draw.color(e.color);
                if (e.data instanceof Float f) {
                    Draw.alpha(f);
                }
                float endRot = ((int) Math.ceil(e.rotation / 45) + 1) * 45;

                Draw.z(Layer.effect);
                Lines.stroke(Mathf.lerp(1.5f, 0.4f, e.fin()));
                Lines.square(e.x, e.y, Mathf.lerp(35, 3, e.fin()), Mathf.lerp(e.rotation, endRot, e.fin()));
            }),
            FEXsmoke = new Effect(80, e -> {
                float move = Mathf.clamp(e.fin() / 0.35f);
                float size = 1 - Mathf.clamp((e.fin() - 0.65f) / 0.35f);

                Draw.z(Layer.effect);
                Angles.randLenVectors(e.id, 6, 4f + (float) Mathm.lerp(0, 9, 0.1f, move * 40), (x, y) -> {
                    Draw.color(HIPal.fexCrystal, Color.lightGray, Mathf.clamp(e.fin() + Mathf.random(-0.1f, 0.1f)));
                    Fill.square(e.x + x, e.y + y, 0.2f + size * 2f + Mathf.random(-0.15f, 0.15f), 45);
                });
            }),
            shootSmokeMissileSmall = new Effect(130f, 300f, e -> {
                Draw.color(e.color);
                Draw.alpha(0.5f);
                rand.setSeed(e.id);
                for (int i = 0; i < 18; i++) {
                    Tmp.v1.trns(e.rotation + 180f + rand.range(19f), rand.random(e.finpow() * 60f)).add(rand.range(2f), rand.range(2f));
                    e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> {
                        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, b.fout() * 3.5f + 0.3f);
                    });
                }
            }),
            impWave = new Effect(10f, e -> {
                Draw.color(Color.white);

                Lines.stroke(e.fout());
                Lines.circle(e.x, e.y, Mathf.randomSeed(e.id, 8, 10) * e.fin());
            }),
            glowParticle = new Effect(45, e -> {
                Draw.color(e.color, Color.white, e.fin());

                Angles.randLenVectors(e.id, 1, 3.5f, e.rotation, 5, (x, y) -> {
                    Fill.circle(e.x + x * e.fin(Interp.pow2Out), e.y + y * e.fin(Interp.pow2Out), 1.6f * e.fout(Interp.pow2Out));
                });
            }),
            freezingBreakDown = new Effect(180, e -> {
                if (!(e.data instanceof Unit u)) return;
                float size = u.hitSize * 1.2f;

                float intensity = size / 32 - 2.2f;
                float baseLifetime = 25f + intensity * 11f;

                e.scaled(baseLifetime, b -> {
                    Draw.color();
                    b.scaled(5 + intensity * 2f, i -> {
                        Lines.stroke((3.1f + intensity / 5f) * i.fout());
                        Lines.circle(b.x, b.y, (3f + i.fin() * 14f) * intensity);
                        Drawf.light(b.x, b.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * b.fout());
                    });

                    Draw.color(HIPal.winter, HIPal.frost, b.fin());
                    Lines.stroke((2f * b.fout()));

                    Draw.z(Layer.effect + 0.001f);
                    Angles.randLenVectors(b.id + 1, b.finpow() + 0.001f, (int) (8 * intensity), 28f * intensity, (x, y, in, out) -> {
                        Lines.lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                        Drawf.light(b.x + x, b.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                    });
                });

                float rate = e.fout(Interp.pow2In);
                float l = size * rate * 1.2f;
                float w = size * rate * 0.2f;

                float x = e.x;
                float y = e.y;
                float fout = e.fout();
                float fin = e.fin();
                Drawf.light(x, y, fout * size, HIPal.winter, 0.7f);

                float lerp = e.fin(Interp.pow3Out);
                int id = e.id;

                Draws.drawBloomUponFlyUnit(null, n -> {
                    Draw.color(HIPal.winter);
                    Draws.drawLightEdge(x, y, l, w, l, w);
                    Lines.stroke(5f * fout);
                    Lines.circle(x, y, 55 * fout);

                    Draws.gradientCircle(x, y, size * lerp, -size * lerp * fout, 1);
                    Draw.reset();
                });

                Draw.z(Layer.flyingUnit + 1);
                Angles.randLenVectors(id, (int) Mathf.randomSeed(id, size / 6, size / 3), size / 2, size * 2, (dx, dy) -> {
                    float s = Mathf.randomSeed((int) (id + dx), size / 4, size / 2);

                    float le = 1 - Mathf.pow(fin, 4);
                    Draws.drawCrystal(x + dx * lerp, y + dy * lerp, s, s * le * 0.35f, s * le * 0.24f, 0, 0, 0.8f * le, Layer.effect, Layer.bullet - 1, Time.time * Mathf.randomSeed(id, -3.5f, 3.35f) + Mathf.randomSeed((long) (id + dx), 360), Mathf.angle(dx, dy), Tmp.c1.set(HIPal.frost).a(0.65f), HIPal.winter);
                });
            }),
            crossLightMini = new Effect(22, e -> {
                Draw.color(e.color);
                for (int i : Mathf.signs) {
                    Draws.drawDiamond(e.x, e.y, 12 + 64 * e.fin(Interp.pow3Out), 5 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45);
                }
            }),
            crossLightSmall = new Effect(26, e -> {
                Draw.color(e.color);
                for (int i : Mathf.signs) {
                    Draws.drawDiamond(e.x, e.y, 22 + 74 * e.fin(Interp.pow3Out), 8 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45);
                }
            }),
            crossLight = new Effect(30, e -> {
                Draw.color(e.color);
                for (int i : Mathf.signs) {
                    Draws.drawDiamond(e.x, e.y, 32 + 128 * e.fin(Interp.pow3Out), 12 * e.fout(Interp.pow3Out), e.rotation + 45 + i * 45);
                }
            }),
            shootCrossLight = new Effect(120, e -> {
                Draw.color(e.color);

                float l = e.fout(Interp.pow3Out);
                Draws.drawLightEdge(e.x, e.y, 140, 5.5f * l, 140, 5.5f * l, e.rotation + 220 * e.fin(Interp.pow3Out));
            }),
            shootCrossLightLarge = new Effect(140, e -> {
                Draw.color(e.color);

                float l = e.fout(Interp.pow3Out);
                Draws.drawLightEdge(e.x, e.y, 240, 12.5f * l, 240, 12.5f * l, e.rotation + 237 * e.fin(Interp.pow3Out));
            }),
            auroraCoreCharging = new Effect(80, 100, e -> {
                Draw.color(HIPal.matrixNet);
                Lines.stroke(e.fin() * 2f);
                Lines.circle(e.x, e.y, 4f + e.fout() * 100f);

                Fill.circle(e.x, e.y, e.fin() * 10);

                Angles.randLenVectors(e.id, 20, 40f * e.fout(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fin() * 5f);
                    Drawf.light(e.x + x, e.y + y, e.fin() * 15f, Pal.heal, 0.7f);
                });

                Draw.color();

                Fill.circle(e.x, e.y, e.fin() * 8);
                Drawf.light(e.x, e.y, e.fin() * 16f, Pal.heal, 0.7f);
            }).rotWithParent(true).followParent(true),
            explodeImpWaveMini = impactExplode(16, 36f),
            explodeImpWaveSmall = impactExplode(22, 40f),
            explodeImpWave = impactExplode(32, 50f),
            explodeImpWaveBig = impactExplode(40, 65f),
            explodeImpWaveLarge = impactExplode(60, 95f),
            explodeImpWaveLaserBlase = impactExplode(86, 200f),
            reactorExplode = new MultiEffect(Fx.reactorExplosion, new Effect(180, e -> {
                float size = e.data instanceof Float f ? f : 120;

                float fin1 = Mathf.clamp(e.fin() / 0.1f);
                float fin2 = Mathf.clamp((e.fin() - 0.1f) / 0.3f);

                Draw.color(Pal.reactorPurple);
                Lines.stroke(6 * e.fout());
                float radius = size * (1 - Mathf.pow(e.fout(), 3));
                Lines.circle(e.x, e.y, radius);

                Draw.z(Layer.effect + 10);
                Draws.gradientCircle(e.x, e.y, radius - 3 * e.fout(), -(size / 6) * (1 - e.fin(Interp.pow3)), Draw.getColor().cpy().a(0));
                Draw.z(Layer.effect);

                float h, w;
                float rate = e.fin() > 0.1f ? 1 - fin2 : fin1;
                h = size / 2 * rate;
                w = h / 5;

                Lines.stroke(3f * rate);
                Lines.circle(e.x, e.y, h / 2);

                Fill.quad(e.x + h, e.y, e.x, e.y + w, e.x - h, e.y, e.x, e.y - w);
                Fill.quad(e.x + w, e.y, e.x, e.y + h, e.x - w, e.y, e.x, e.y - h);

                float intensity = size / 32 - 2.2f;
                float baseLifetime = 25f + intensity * 11f;

                Draw.color(Pal.reactorPurple2);
                Draw.alpha(0.7f);
                for (int i = 0; i < 4; i++) {
                    rand.setSeed(e.id * 2L + i);
                    float lenScl = rand.random(0.4f, 1f);
                    int fi = i;
                    e.scaled(e.lifetime * lenScl, b -> {
                        Angles.randLenVectors(b.id + fi - 1, b.fin(Interp.pow10Out), (int) (2.9f * intensity), 22f * intensity, (x, y, in, out) -> {
                            float fout = b.fout(Interp.pow5Out) * rand.random(0.5f, 1f);
                            float rad = fout * ((2f + intensity) * 2.35f);

                            Fill.circle(b.x + x, b.y + y, rad);
                            Drawf.light(b.x + x, b.y + y, rad * 2.5f, Pal.reactorPurple, 0.5f);
                        });
                    });
                }

                e.scaled(baseLifetime, b -> {
                    Draw.color();
                    b.scaled(5 + intensity * 2f, i -> {
                        Lines.stroke((3.1f + intensity / 5f) * i.fout());
                        Lines.circle(b.x, b.y, (3f + i.fin() * 14f) * intensity);
                        Drawf.light(b.x, b.y, i.fin() * 14f * 2f * intensity, Color.white, 0.9f * b.fout());
                    });

                    Draw.color(Pal.lighterOrange, Pal.reactorPurple, b.fin());
                    Lines.stroke((2f * b.fout()));

                    Draw.z(Layer.effect + 0.001f);
                    Angles.randLenVectors(b.id + 1, b.finpow() + 0.001f, (int) (8 * intensity), 28f * intensity, (x, y, in, out) -> {
                        Lines.lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out * 4 * (4f + intensity));
                        Drawf.light(b.x + x, b.y + y, (out * 4 * (3f + intensity)) * 3.5f, Draw.getColor(), 0.8f);
                    });
                });
            })),
            weaveTrail = new Effect(12, e -> {
                Draw.color(e.color, Color.white, e.fin());
                Draws.drawDiamond(e.x, e.y, 15 + 45 * e.fin(), 8 * e.fout(), e.rotation + 90);
            }),
            steam = new Effect(90, e -> {
                Vec2 motion = e.data instanceof Vec2 v ? v : new Vec2(0, 0);
                float len = motion.len();
                Draw.color(Color.white);
                Draw.alpha(0.75f * e.fout());

                for (int i = 0; i < 5; i++) {
                    Vec2 curr = motion.cpy().rotate(Mathf.randomSeed(e.id, -20, 20)).setLength(len * e.finpow());
                    Fill.circle(e.x + curr.x, e.y + curr.y, Mathf.randomSeed(e.id, 3.5f, 5) * (0.3f + 0.7f * e.fslope()));
                }
            }),
            steamBreakOut = new Effect(24, e -> {
                float[] data = e.data instanceof float[] f ? f : new float[]{18, 24, 0.3f};

                float leng = Mathf.random(data[0], data[1]);
                for (int i = 0; i < 4; i++) {
                    if (Mathf.chanceDelta(data[2]))
                        steam.at(e.x, e.y, 0, new Vec2(leng * Geometry.d8(i * 2 + 1).x, leng * Geometry.d8(i * 2 + 1).y));
                }
            }),
            lightCone = new Effect(16, e -> {
                Draw.color(e.color);

                Draws.drawDiamond(e.x, e.y, 8, 26 * e.fout(), e.rotation);
            }),
            lightConeHit = new Effect(30, e -> {
                Draw.color(e.color);

                float fout = e.fout(Interp.pow2Out);
                float fin = e.fin(Interp.pow2Out);
                Angles.randLenVectors(e.id, Mathf.randomSeed(e.id + 1, 3, 4), 30, e.rotation, 60, (dx, dy) -> {
                    Drawf.tri(e.x - dx * fin, e.y - dy * fin, 6f * fout, 6 + 15 * fout, Mathf.angle(dx, dy) + 180);
                    Drawf.tri(e.x - dx * fin, e.y - dy * fin, 6f * fout, 6f * fout, Mathf.angle(dx, dy));
                });
            }),
            matrixDrill = new Effect(45, e -> {
                Draw.color(e.color);

                Lines.stroke(1.8f * e.fout());
                Lines.square(e.x, e.y, 3f + 12 * e.fin(), 45);
                Fill.square(e.x, e.y, 3 * e.fout(), 45 + 360 * e.fin(Interp.pow3Out));
            }),
            lightConeTrail = new Effect(20, e -> {
                Draw.color(e.color);

                int i = Mathf.randomSeed(e.id) > 0.5f ? 1 : -1;
                float off = Mathf.randomSeed(e.id, -10, 10);
                float fout = e.fout(Interp.pow2Out);

                float rot = e.rotation + 156f * i + off;
                float dx = Angles.trnsx(rot, 24, 0) * e.fin(Interp.pow2Out);
                float dy = Angles.trnsy(rot, 24, 0) * e.fin(Interp.pow2Out);

                Drawf.tri(e.x + dx, e.y + dy, 8f * fout, 8 + 24 * fout, rot);
                Drawf.tri(e.x + dx, e.y + dy, 8f * fout, 8f * fout, rot + 180);
            }),
            trailLine = new Effect(24, e -> {
                Draw.color(e.color);

                Drawf.tri(e.x, e.y, 2f * e.fout(), 2 + 6 * e.fout(), e.rotation);
                Drawf.tri(e.x, e.y, 2f * e.fout(), 8 + 10 * e.fout(), e.rotation + 180);
            }),
            trailLineLong = new Effect(30, e -> {
                Draw.color(e.color);

                Drawf.tri(e.x, e.y, 4 * e.fout(), 6 + 8 * e.fout(), e.rotation);
                Drawf.tri(e.x, e.y, 4 * e.fout(), 10 + 16 * e.fout(), e.rotation + 180);
            }),
            spreadSparkLarge = new Effect(28, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.2f + 0.5f);

                Angles.randLenVectors(e.id, 20, 10f * e.fin(), 27f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5f + 0.5f);
                });
            }),
            circleSparkMini = new Effect(32, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 0.8f + 0.2f);

                Angles.randLenVectors(e.id, 22, 4f * e.fin(), 12f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 2.2f);
                });
            }),
            constructSpark = new Effect(24, e -> {
                Color c = e.color;
                float fin = e.fin();
                float fs = e.fslope();
                float ex = e.x, ey = e.y;
                int id = e.id;

                Draws.drawBloomUponFlyUnit(() -> {
                    Draw.color(Color.white, c, fin);
                    Lines.stroke((1 - fin) * 0.8f + 0.2f);

                    Angles.randLenVectors(id, 22, 4f * fin, 12f, (x, y) -> {
                        Lines.lineAngle(ex + x, ey + y, Mathf.angle(x, y), fs * 2.2f);
                    });
                    Draw.reset();
                });
            }),
            circleSparkLarge = new Effect(65, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.4f + 0.5f);

                Angles.randLenVectors(e.id, 37, 28f * e.fin(), 49f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 9f);
                });
            }),
            diamondSpark = new Effect(30, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.2f + 0.5f);

                Angles.randLenVectors(e.id, 7, 6f * e.fin(), 20f, (x, y) -> {
                    Draws.drawDiamond(e.x + x, e.y + y, 10, e.fout(Interp.pow2Out) * 4f, Mathf.angle(x, y));
                });
            }),
            diamondSparkLarge = new Effect(30, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.2f + 0.5f);

                Angles.randLenVectors(e.id, 9, 8f * e.fin(), 24f, (x, y) -> {
                    Draws.drawDiamond(e.x + x, e.y + y, 12, e.fout(Interp.pow2Out) * 5f, Mathf.angle(x, y));
                });
            }),
            railShootRecoil = new Effect(12, e -> {
                Draw.color(e.color);

                Angles.randLenVectors(e.id, Mathf.randomSeed(e.id, 2, 4), 24, e.rotation + 180, 60, (x, y) -> {
                    float size = Mathf.randomSeed((int) (e.id + x), 6, 12);
                    float lerp = e.fin(Interp.pow2Out);
                    Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size / 2f * e.fout(), Mathf.angle(x, y));
                });
            }),
            trailParticle = new Effect(95, e -> {
                Draw.color(e.color);

                Angles.randLenVectors(e.id, 3, 35, (x, y) -> {
                    Fill.circle(e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 1.2f * e.fout());
                });
            }),
            iceParticle = new Effect(124, e -> {
                Draw.color(e.color);

                int amo = Mathf.randomSeed(e.id, 2, 5);
                for (int i = 0; i < amo; i++) {
                    float len = Mathf.randomSeed(e.id + i * 2l, 40) * e.fin();
                    float off = Mathf.randomSeed(e.id + i, -8, 8);
                    float x = Angles.trnsx(e.rotation, len) + Angles.trnsx(e.rotation + 90, off);
                    float y = Angles.trnsy(e.rotation, len) + Angles.trnsy(e.rotation + 90, off);
                    Fill.circle(e.x + x, e.y + y, 0.9f * e.fout(Interp.pow2Out));
                }
            }),
            iceExplode = new Effect(128, e -> {
                float rate = e.fout(Interp.pow2In);
                float l = 176 * rate;
                float w = 38 * rate;

                float x = e.x;
                float y = e.y;
                float fout = e.fout();
                float fin = e.fin();
                Drawf.light(x, y, fout * 192, HIPal.winter, 0.7f);

                Draw.z(Layer.flyingUnit + 1);

                float lerp = e.fin(Interp.pow3Out);
                int id = e.id;

                Draws.drawBloomUponFlyUnit(null, n -> {
                    Draw.color(HIPal.winter);
                    Draws.drawLightEdge(x, y, l, w, l, w);
                    Lines.stroke(5f * fout);
                    Lines.circle(x, y, 55 * fout);
                    Lines.stroke(8f * fout);
                    Lines.circle(x, y, 116 * lerp);

                    Angles.randLenVectors(id, Mathf.randomSeed(id, 16, 22), 128, (dx, dy) -> {
                        float size = Mathf.randomSeed((int) (id + dx), 14, 24);

                        Draws.drawDiamond(x + dx * lerp, y + dy * lerp, size, size * (1 - Mathf.pow(fin, 2)) * 0.35f, Mathf.angle(dx, dy));
                    });
                    Draw.reset();
                });
            }),
            particleSpread = new Effect(125, e -> {
                Draw.color(e.color);

                Angles.randLenVectors(e.id, 3, 32, (x, y) -> {
                    Fill.circle(e.x + x * e.fin(), e.y + y * e.fin(), 0.9f * e.fout(Interp.pow2Out));
                });
            }),
            movingCrystalFrag = new Effect(45, e -> {
                float size = Mathf.randomSeed(e.id, 4, 6) * e.fout();
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 1, 3, 6, e.rotation, 20, (x, y) -> {
                    Draws.drawDiamond(e.x + x * e.fin(Interp.pow2Out), e.y + y * e.fin(Interp.pow2Out), size * 2.5f, size, Angles.angle(x, y));
                });
            }),
            crystalFrag = new Effect(26, e -> {
                float size = Mathf.randomSeed(e.id, 2, 4) * e.fout();
                Draw.color(e.color);
                Draws.drawDiamond(e.x, e.y, size * 2.5f, size, Mathf.randomSeed(e.id, 0f, 360f));
            }),
            crystalFragFex = new Effect(26, e -> {
                float size = Mathf.randomSeed(e.id, 2, 4) * e.fout();
                Draw.color(HIPal.fexCrystal, 0.7f);
                Draws.drawDiamond(e.x, e.y, size * 2.5f, size, Mathf.randomSeed(e.id, 0f, 360f));
            }),
            iceCrystal = new Effect(120, e -> {
                float size = Mathf.randomSeed(e.id, 2, 6) * e.fslope();
                Draw.color(e.color);
                float rot = Mathf.randomSeed(e.id + 1, 360);
                float blingX = Angles.trnsx(rot, size * 2), blingY = Angles.trnsy(rot, size * 2);
                Draws.drawDiamond(e.x, e.y, size * 2, size / 2, rot);
                e.scaled(45, ec -> {
                    Draws.drawDiamond(ec.x + blingX, ec.y + blingY, 85 * ec.fslope(), 1.2f * ec.fslope(), Mathf.randomSeed(ec.id + 2, 360) + Mathf.randomSeed(ec.id + 3, -15, 15) * ec.fin());
                });
            }),
            shootRail = new Effect(60, e -> {
                e.scaled(12f, b -> {
                    Lines.stroke(b.fout() * 4f + 0.2f, e.color);
                    Lines.circle(b.x, b.y, b.fin() * 70f);
                    Lines.stroke(b.fout() * 2.3f + 0.15f);
                    Lines.circle(b.x, b.y, b.fin() * 62f);
                });

                float lerp = e.fout(Interp.pow2Out);
                Draw.color(e.color);
                Draws.drawLightEdge(e.x, e.y, 64 + 64 * lerp, 10 * lerp, 60 + 80 * lerp, 6 * lerp, e.rotation + 90);
            }),
            winterShooting = new Effect(60, e -> {
                e.scaled(12f, b -> {
                    Lines.stroke(b.fout() * 4f + 0.2f, HIPal.winter);
                    Lines.circle(b.x, b.y, b.fin() * 75f);
                });

                float lerp = e.fout(Interp.pow2Out);
                Draw.color(HIPal.winter);
                Draws.drawLightEdge(e.x, e.y, 64 + 64 * lerp, 12 * lerp, 60 + 80 * lerp, 6 * lerp, e.rotation + 90);

                float l = e.fin(Interp.pow2Out);
                Angles.randLenVectors(e.id, Mathf.randomSeed(e.id, 8, 16), 48, e.rotation + 180, 60, (x, y) -> {
                    float size = Mathf.randomSeed((int) (e.id + x), 12, 20);
                    Draws.drawDiamond(e.x + x * l, e.y + y * l, size, size / 2f * e.fout(), Mathf.angle(x, y));
                });
            }),
            laserBlastWeaveLarge = new Effect(280, 200, e -> {
                float size = 140;

                float fin1 = Mathf.clamp(e.fin() / 0.1f);
                float fin2 = Mathf.clamp((e.fin() - 0.1f) / 0.3f);

                Draw.color(e.color);
                float radius = size * e.fin(Interp.pow4Out);

                Draw.alpha(0.6f);
                Draws.gradientCircle(e.x, e.y, radius, -radius * e.fout(Interp.pow2Out), 0);
                Draw.alpha(1);
                Lines.stroke(6 * e.fout(Interp.pow2Out));
                Lines.circle(e.x, e.y, radius);

                float h, w;
                float rate = e.fin() > 0.1f ? 1 - fin2 : fin1;
                h = size * 1.26f * rate;
                w = h / 4;

                Lines.stroke(3f * rate);
                Lines.circle(e.x, e.y, h / 2);

                Draws.drawLightEdge(e.x, e.y, h, w, h, w);

                rand.setSeed(e.id);
                for (int i = 0; i < 14; i++) {
                    float rot = rand.random(0, 360f);
                    float wi = rand.random(12, 18);
                    float le = rand.random(wi * 2f, wi * 4f);

                    Draws.drawTransform(e.x, e.y, radius, 0, rot, (x, y, ro) -> {
                        Drawf.tri(x, y, wi * e.fout(), le, ro - 180);
                    });
                }

                e.scaled(100, ef -> {
                    Angles.randLenVectors(e.id, 9, 45, 164, (x, y) -> {
                        float lerp = ef.fin(Interp.pow4Out);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.6f, 0.8f);
                        Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });
                });

                e.scaled(120, ef -> {
                    Angles.randLenVectors(e.id * 2l, 9, 40, 154, (x, y) -> {
                        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.2f) / 0.8f);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
                        Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });
                });

                e.scaled(140, ef -> {
                    Angles.randLenVectors(e.id * 2l, 10, 36, 150, (x, y) -> {
                        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.4f) / 0.6f);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
                        Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });
                });

                e.scaled(160, ef -> {
                    Angles.randLenVectors(e.id * 3l, 12, 32, 144, (x, y) -> {
                        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.5f) / 0.5f);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.9f, 1f);
                        Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });

                    Lines.stroke(4 * ef.fout());
                    Angles.randLenVectors(e.id * 4l, ef.finpow() + 0.001f, 58, size * 1.2f, (dx, dy, in, out) -> {
                        Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 8 + out * 64f);
                        Drawf.light(e.x + dx, e.y + dy, out * size / 2, Draw.getColor(), 0.8f);
                    });
                });
            }),
            shrinkIceParticleSmall = new Effect(120, e -> {
                Draw.color(HIPal.winter);

                Angles.randLenVectors(e.id, Mathf.randomSeed(e.id, 6, 12), 32, (x, y) -> {
                    float size = Mathf.randomSeed((int) (e.id + x), 8, 16);
                    float lerp = e.fout(Interp.pow3Out);
                    Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size / 2f * e.fin(), Mathf.angle(x, y));
                });
            }),
            shrinkParticleSmall = shrinkParticle(12, 2, 120, null),
            blingSmall = new Effect(320, e -> {
                Draw.z(Layer.effect);
                Draw.color(e.color);
                float size = Mathf.randomSeed(e.id, 6, 10);
                size *= e.fout(Interp.pow4In);
                size += Mathf.absin(Time.time + Mathf.randomSeed(e.id, 2 * Mathf.pi), 3.5f, 2f);
                float i = e.fin(Interp.pow3Out);
                float dx = Mathf.randomSeed(e.id, 16), dy = Mathf.randomSeed(e.id + 1, 16);
                Draws.drawLightEdge(e.x + dx * i, e.y + dy * i, size, size * 0.15f, size, size * 0.15f);
            }),
            staticBlingSmall = new Effect(320, e -> {
                Draw.z(Layer.effect);
                Draw.color(e.color);
                float size = Mathf.randomSeed(e.id, 6, 10);
                size *= e.fout(Interp.pow4In);
                size += Mathf.absin(Time.time + Mathf.randomSeed(e.id, 2 * Mathf.pi), 3.5f, 2f);
                Draws.drawLightEdge(e.x, e.y, size, size * 0.15f, size, size * 0.15f);
            }),
            lightningBoltWave = new Effect(90, e -> {
                Draw.color(e.color);
                float rate = e.fout(Interp.pow2In);
                float l = 168 * rate;
                float w = 36 * rate;

                Drawf.light(e.x, e.y, e.fout() * 96, e.color, 0.7f);

                float lerp = e.fin(Interp.pow3Out);
                Draws.drawLightEdge(e.x, e.y, l, w, l, w);
                Lines.stroke(5f * e.fout());
                Lines.circle(e.x, e.y, 45 * e.fout());
                Lines.stroke(8f * e.fout());
                Lines.circle(e.x, e.y, 84 * lerp);

                Angles.randLenVectors(e.id, Mathf.randomSeed(e.id, 15, 20), 92, (x, y) -> {
                    float size = Mathf.randomSeed((int) (e.id + x), 18, 26);
                    Draws.drawDiamond(e.x + x * lerp, e.y + y * lerp, size, size * 0.23f * e.fout(), Mathf.angle(x, y));
                });

                e.scaled(45, ef -> {
                    Angles.randLenVectors(e.id, 8, 25, 94, (x, y) -> {
                        float le = ef.fin(Interp.pow4Out);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.6f, 0.8f);
                        Draws.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });
                });

                e.scaled(56, ef -> {
                    Angles.randLenVectors(e.id * 2l, 8, 20, 82, (x, y) -> {
                        float le = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.3f) / 0.7f);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
                        Draws.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });
                });

                e.scaled(75, ef -> {
                    Angles.randLenVectors(e.id * 3l, 9, 14, 69, (x, y) -> {
                        float le = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.5f) / 0.5f);
                        float si = Mathf.len(x, y) * Mathf.randomSeed((long) (x + y), 0.9f, 1f);
                        Draws.drawDiamond(e.x + x * le, e.y + y * le, si, si / 10 * ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
                    });

                    Lines.stroke(3 * ef.fout());
                    Angles.randLenVectors(e.id * 4l, ef.finpow() + 0.001f, 48, 102, (dx, dy, in, out) -> {
                        Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 4 + out * 34f);
                        Drawf.light(e.x + dx, e.y + dy, out * 96, Draw.getColor(), 0.8f);
                    });
                });
            }),
            neutronWeaveMicro = new Effect(45, e -> {
                Draw.color(e.color);

                Lines.stroke(e.fout());
                Lines.square(e.x, e.y, 2.2f + 6.8f * e.fin(), 45);
                Fill.square(e.x, e.y, 2.2f * e.fout(), 45);
            }),
            neutronWeaveMini = new Effect(45, e -> {
                Draw.color(e.color);

                Lines.stroke(1.5f * e.fout());
                Lines.square(e.x, e.y, 3f + 9f * e.fin(), 45);
                Fill.square(e.x, e.y, 3f * e.fout(), 45);
            }),
            neutronWeave = new Effect(45, e -> {
                Draw.color(e.color);

                Lines.stroke(1.8f * e.fout());
                Lines.square(e.x, e.y, 4f + 12 * e.fin(), 45);
                Fill.square(e.x, e.y, 4 * e.fout(), 45);
            }),
            neutronWeaveBig = new Effect(45, e -> {
                Draw.color(e.color);

                Lines.stroke(2f * e.fout());
                Lines.square(e.x, e.y, 5f + 18 * e.fin(), 45);
                Fill.square(e.x, e.y, 5 * e.fout(), 45);
            }),
            spreadSizedDiamond = new Effect(42, e -> {
                Draw.color(e.color);

                Lines.stroke(12f * e.fout());
                Lines.square(e.x, e.y, e.rotation * e.fin(Interp.pow2Out), 45);
            }),
            spreadDiamond = new Effect(35, e -> {
                Draw.color(e.color);

                Lines.stroke(12f * e.fout());
                Lines.square(e.x, e.y, 32 * e.fin(Interp.pow2Out), 45);
            }),
            spreadDiamondSmall = new Effect(25, e -> {
                Draw.color(e.color);

                Lines.stroke(8f * e.fout());
                Lines.square(e.x, e.y, 18 * e.fin(Interp.pow2Out), 45);
            }),
            colorLaserCharge = new Effect(38f, e -> {
                Draw.color(e.color);

                Angles.randLenVectors(e.id, 14, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f);
                });
            }),
            colorLaserChargeBegin = new Effect(60f, e -> {
                float margin = 1f - Mathf.curve(e.fin(), 0.9f);
                float fin = Math.min(margin, e.fin());

                Draw.color(e.color);
                Fill.circle(e.x, e.y, fin * 3f);

                Draw.color();
                Fill.circle(e.x, e.y, fin * 2f);
            }),
            dust = new Effect(70, e -> {
                if (!(e.data instanceof Vec2 v)) return;
                Draw.color(e.color);
                Fill.circle(e.x + e.finpow() * v.x, e.y + e.finpow() * v.y, (7f - e.fin() * 7f) * 0.5f);
            }).layer(Layer.debris),
            smokePoof = new Effect(30, e -> {
                Draw.color(Color.white);
                Angles.randLenVectors(e.id, 6, 4f + 30f * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 3f);
                    Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout());
                });
            }).layer(Layer.blockOver - 1),
            steamSlow = new Effect(200, e -> {
                Draw.color(Color.white);
                Draw.alpha(Mathf.sqrt(e.fslope()));
                float ef = e.finpow() * 10f;
                Angles.randLenVectors(e.id, 1, 4f + 10f * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x + ef, e.y + y+ ef, e.fout() * 8f);
                    Fill.circle(e.x + x / 2f + ef, e.y + y / 2f + ef, e.fout() * 4f);
                });
                Draw.alpha(1);
            }).layer(Layer.blockOver - 1),
            weldSpark = new Effect(12, e-> {
                if (e.fin() < 0.5) {
                    Draw.color(Color.white, e.color, e.fin() * 0.5f);
                } else {
                    Draw.color(e.color, Tmp.c1.set(e.color).mul(0.5f), e.fin() * 0.5f + 0.5f);
                }
                Lines.stroke(e.fout() * 0.6f + 0.6f);

                Angles.randLenVectors(e.id, 3, 15 * e.finpow(), e.rotation, 3, (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 5 + 0.5f);
                });
            }),
            greenLaserChargeParent = new ParentEffect(80f, 100f, e -> {
                Draw.color(Pal.heal);
                Lines.stroke(e.fin() * 2f);
                Lines.circle(e.x, e.y, 4f + e.fout() * 100f);

                Fill.circle(e.x, e.y, e.fin() * 20);

                Angles.randLenVectors(e.id, 20, 40f * e.fout(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fin() * 5f);
                    Drawf.light(e.x + x, e.y + y, e.fin() * 15f, Pal.heal, 0.7f);
                });

                Draw.color();

                Fill.circle(e.x, e.y, e.fin() * 10);
                Drawf.light(e.x, e.y, e.fin() * 20f, Pal.heal, 0.7f);
            }),
            laserChargeShoot = new Effect(21f, e -> {
                Draw.color(e.color, Color.white, e.fout());

                for (int i = 0; i < 4; i++) {
                    Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
                }
            }),
            laserChargeShootShort = new Effect(15f, e -> {
                Draw.color(e.color, Color.white, e.fout());
                Lines.stroke(2f * e.fout());
                Lines.square(e.x, e.y, 0.1f + 20f * e.finpow(), 45f);
            }),
            laserFractalShoot = new Effect(40f, e -> {
                Draw.color(Tmp.c1.set(e.color).lerp(Color.white, e.fout()));

                for (int i = 0; i < 4; i++) {
                    Drawf.tri(e.x, e.y, 4f * e.fout(), 29f, e.rotation + 90f * i + e.finpow() * 112f);
                }

                for (int h = 1; h <= 5; h++) {
                    //float rot = h * 180f + Mathf.randomSeedRange(e.id, 360) + e.finpow() * 262;
                    float mul = h % 2;
                    float rm = 1 + mul * 0.5f;
                    float rot = 90 + (1 - e.finpow()) * Mathf.randomSeed(e.id + (long)(mul * 2f), 210 * rm, 360 * rm);
                    for (int i = 0; i < 2; i++) {
                        float m = i == 0 ? 1 : 0.5f;
                        float w = 8 * e.fout() * m;
                        float length = 8 * 3 / (2 - mul);
                        Vec2 fxPos = Tmp.v1.trns(rot, length - 4);
                        length *= HIInterp.pow25Out.apply(e.fout());

                        Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w, length * m, rot + 180);
                        Drawf.tri(fxPos.x + e.x, fxPos.y + e.y, w , length / 3f * m, rot);

                        Draw.alpha(0.5f);
                        Drawf.tri(e.x, e.y, w, length * m,  rot + 360);
                        Drawf.tri(e.x, e.y, w, length/3 * m, rot);
                        Fill.square(fxPos.x + e.x, fxPos.y + e.y, 3 * e.fout(), rot + 45);
                    }
                }
            }),
            laserBreakthroughShoot = new Effect(40f, e -> {
                Draw.color(e.color);

                Lines.stroke(e.fout() * 2.5f);
                Lines.circle(e.x, e.y, e.finpow() * 100f);

                Lines.stroke(e.fout() * 5f);
                Lines.circle(e.x, e.y, e.fin() * 100f);

                Draw.color(e.color, Color.white, e.fout());

                Angles.randLenVectors(e.id, 20, 80f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 5f));

                for (int i = 0; i < 4; i++) {
                    Drawf.tri(e.x, e.y, 9f * e.fout(), 170f, e.rotation + Mathf.randomSeed(e.id, 360f) + 90f * i + e.finpow() * (0.5f - Mathf.randomSeed(e.id)) * 150f);
                }
            }),
            shootSmallBlaze = new Effect(22f, e -> {
                Draw.color(Pal.lightFlame, Pal.darkFlame, Pal.gray, e.fin());
                Angles.randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
            }),
            shootPyraBlaze = new Effect(32f, e -> {
                Draw.color(Pal.lightPyraFlame, Pal.darkPyraFlame, Pal.gray, e.fin());
                Angles.randLenVectors(e.id, 16, e.finpow() * 60f, e.rotation, 18f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.85f + e.fout() * 3.5f));
            }),
            sapPlasmaShoot = new Effect(25f, e -> {
                Draw.color(Color.white, Pal.sapBullet, e.fin());
                Angles.randLenVectors(e.id, 13, e.finpow() * 20f, e.rotation, 23f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 5f);
                    Fill.circle(e.x + x / 1.2f, e.y + y / 1.2f, e.fout() * 3f);
                });
            }),
            tonkCannon = new Effect(35f, e -> {
                Draw.color(Pal.accent);
                for (int sus : Mathf.signs) {
                    Drawf.tri(e.x, e.y, 8 * e.fout(Interp.pow3Out), 80, e.rotation + 20 * sus);
                    Drawf.tri(e.x, e.y, 4 * e.fout(Interp.pow3Out), 30, e.rotation + 60 * sus);
                }
            }),
            tonkCannonSmoke = new Effect(45f, e -> {
                Draw.color(Pal.lighterOrange, Color.lightGray, Color.gray, e.fin());
                Angles.randLenVectors(e.id, 14, 0f + 55f * e.finpow(), e.rotation, 25f, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout(Interp.pow5Out) * 4.5f);
                });
            }),
            coloredHitSmall = new Effect(14f, e -> {
                Draw.color(Color.white, e.color, e.fin());
                e.scaled(7f, s -> {
                    Lines.stroke(0.5f + s.fout());
                    Fill.circle(e.x, e.y, s.fin() * 5f);
                });

                Lines.stroke(0.5f + e.fout());
                Angles.randLenVectors(e.id, 5, e.fin() * 15f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f));
            }),
            empHit = new Effect(50f, 100f, e -> {
                float rad = 70f;
                e.scaled(7f, b -> {
                    Draw.color(Pal.heal, b.fout());
                    Fill.circle(e.x, e.y, rad);
                });

                Draw.color(Pal.heal);
                Lines.stroke(e.fout() * 3f);
                Lines.circle(e.x, e.y, rad);

                int points = 10;
                float offset = Mathf.randomSeed(e.id, 360f);
                for (int i = 0; i < points; i++) {
                    float angle = i* 360f / points + offset;
                    Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 50f * e.fout(), angle);
                }

                Fill.circle(e.x, e.y, 12f * e.fout());
                Draw.color();
                Fill.circle(e.x, e.y, 6f * e.fout());
                Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
            }),
            coloredHitLarge = new Effect(21f, e -> {
                Draw.color(Color.white, e.color, e.fin());
                e.scaled(8f, s -> {
                    Lines.stroke(0.5f + s.fout());
                    Fill.circle(e.x, e.y, s.fin() * 11f);
                });

                Lines.stroke(0.5f + e.fout());
                Angles.randLenVectors(e.id, 6, e.fin() * 35f, e.rotation + 180f, 45f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 7f + 1f));
            }),
            hitExplosionLarge = new Effect(30f, 200f, e -> {
                Draw.color(Pal.missileYellow);
                e.scaled(12f, s -> {
                    Lines.stroke(s.fout() * 2f + 0.5f);
                    Lines.circle(e.x, e.y, s.fin() * 60f);
                });

                Draw.color(Color.gray);
                Angles.randLenVectors(e.id, 8, 2f + 42f * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 5f + 0.5f);
                });

                Draw.color(Pal.missileYellowBack);
                Lines.stroke(e.fout() * 1.5f);

                Angles.randLenVectors(e.id + 1, 5, 1f + 56f * e.finpow(), (x, y) -> {
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 5f);
                });

                Drawf.light(e.x, e.y, 60f, Pal.missileYellowBack, 0.8f * e.fout());
            }),
            hitExplosionMassive = new Effect(70f, 370f, e -> {
                e.scaled(17f, s -> {
                    Draw.color(Color.white, Color.lightGray, e.fin());
                    Lines.stroke(s.fout() + 0.5f);
                    Fill.circle(e.x, e.y, e.fin() * 185f);
                });

                Draw.color(Color.gray);

                Angles.randLenVectors(e.id, 12, 5f + 135f * e.finpow(), (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, e.fout() * 22f + 0.5f);
                    Fill.circle(e.x + x / 2f, e.y + y / 2f, e.fout() * 9f);
                });

                Draw.color(Pal.lighterOrange, Pal.lightOrange, Color.gray, e.fin());
                Lines.stroke(1.5f * e.fout());

                Angles.randLenVectors(e.id + 1, 14, 1f + 160f * e.finpow(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 1f + e.fout() * 3f));
            }),
            branchFragHit = new Effect(8f, e -> {
                Draw.color(Color.white, Pal.lancerLaser, e.fin());

                Lines.stroke(0.5f + e.fout());
                Lines.circle(e.x, e.y, e.fin() * 5f);

                Lines.stroke(e.fout());
                Lines.circle(e.x, e.y, e.fin() * 6f);
            }),
            coloredRailgunTrail = new Effect(30f, e -> {
                for (int i = 0; i < 2; i++) {
                    int sign = Mathf.signs[i];
                    Draw.color(e.color);
                    Drawf.tri(e.x, e.y, 10f * e.fout(), 24f, e.rotation + 90f + 90f * sign);
                }
            }),
            coloredRailgunSmallTrail = new Effect(30f, e -> {
                for (int i = 0; i < 2; i++) {
                    int sign = Mathf.signs[i];
                    Draw.color(e.color);
                    Drawf.tri(e.x, e.y, 5f * e.fout(), 12f, e.rotation + 90f + 90f * sign);
                }
            }),
            coloredArrowTrail = new Effect(40f, 80f, e -> {
                Tmp.v1.trns(e.rotation, 5f * e.fout());
                Draw.color(e.color);
                for (int s : Mathf.signs) {
                    Tmp.v2.trns(e.rotation - 90f, 9f * s * ((e.fout() + 2f) / 3f), -20f);
                    Fill.tri(Tmp.v1.x + e.x, Tmp.v1.y + e.y, -Tmp.v1.x + e.x, -Tmp.v1.y + e.y, Tmp.v2.x + e.x, Tmp.v2.y + e.y);
                }
            }),
            sparkle = new Effect(55f, e -> {
                Draw.color(e.color);
                integer = 0;
                Angles.randLenVectors(e.id, e.id % 3 + 1, 8f, (x, y) -> {
                    integer++;
                    Drawn.spark(e.x + x, e.y + y, e.fout() * 2.5f, 0.5f + e.fout(), e.id * integer);
                });
            }),
            orbDies = new Effect(20f, e -> {
                Draw.color(HIPal.exp);
                integer = 0;
                Angles.randLenVectors(e.id, 4, 2f + 6f * e.finpow(), (x, y) -> {
                    integer++;
                    Drawn.spark(e.x + x, e.y + y, e.fout() * 3f, 0.7f + e.fout(), e.id * integer);
                });
            }),
            expGain = new Effect(75f, 400f, e -> {
                if (!(e.data instanceof Position pos)) return;

                float fin = Mathf.curve(e.fin(), 0, Mathf.randomSeed(e.id, 0.25f, 1f));
                if (fin >= 1) return;

                float a = Angles.angle(e.x, e.y, pos.getX(), pos.getY()) - 90;
                float d = Mathf.dst(e.x, e.y, pos.getX(), pos.getY());
                float fslope = fin * (1f - fin) * 4f;
                float sfin = Interp.pow2In.apply(fin);
                float spread = d / 4f;
                Tmp.v1.trns(a, Mathf.randomSeed(e.id * 2L, -spread, spread) * fslope, d * sfin);
                Tmp.v1.add(e.x, e.y);

                Draw.color(HIPal.exp, Color.white, 0.1f + 0.1f * Mathf.sin(Time.time * 0.03f + e.id * 3f));
                Fill.circle(Tmp.v1.x, Tmp.v1.y, 1.5f);
                Lines.stroke(0.5f);
                for (int i = 0; i < 4; i++)
                    Drawf.tri(Tmp.v1.x, Tmp.v1.y, 4f, 4 + 1.5f * Mathf.sin(Time.time * 0.12f + e.id * 4f), i * 90f + Mathf.sin(Time.time * 0.04f + e.id * 5f) * 28f);
            }),
            expPoof = new Effect(60f, e -> {
                Draw.color(Pal.accent, HIPal.exp, e.fin());
                integer = 0;
                Angles.randLenVectors(e.id, 9, 1f + 30f * e.finpow(), (x, y) -> {
                    integer++;
                    Fill.circle(e.x + x, e.y + y, 1.7f * e.fout());
                    Drawn.spark(e.x + x, e.y + y, 5f, (5 + 1.5f * Mathf.sin(Time.time * 0.12f + integer * 4f)) * e.fout(), e.finpow() * 90f + integer * 69f);
                });
            }),
            expShineRegion = new Effect(25f, e -> {
                Draw.color();
                Tmp.c1.set(Pal.accent).lerp(HIPal.exp, e.fin());
                Draw.mixcol(Tmp.c1, 1f);
                Draw.alpha(1f - e.fin() * e.fin());

                if (e.data instanceof TextureRegion region) {
                    Draw.rect(region, e.x, e.y, e.rotation);
                }
            }),
            orbDespawn = new Effect(15f, e -> {
                Draw.color(HIPal.exp);
                Lines.stroke(e.fout() * 1.2f + 0.01f);
                Lines.circle(e.x, e.y, 4f * e.finpow());
            }),
            expLaser = new Effect(15f, e -> {
                if (e.data instanceof Building b && !b.dead) {
                    Tmp.v2.set(b);
                    Tmp.v1.set(Tmp.v2).sub(e.x, e.y).nor().scl(tilesize / 2f);
                    Tmp.v2.sub(Tmp.v1);
                    Tmp.v1.add(e.x, e.y);
                    Drawf.laser(Core.atlas.find("unity-exp-laser"), Core.atlas.find("unity-exp-laser-end"), Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, 0.4f * e.fout());
                }
            }),
            placeShine = new Effect(30f, e -> {
                Draw.color(e.color);
                Lines.stroke(e.fout());
                Lines.square(e.x, e.y, e.rotation / 2f + e.fin() * 3f);
                Drawn.spark(e.x, e.y, 25f, 15f * e.fout(), e.finpow() * 90f);
            }),
            laserCharge = new Effect(38f, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, e.id % 3 + 1, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) ->
                        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
                );
            }),
            laserChargeShort = new Effect(18f, e -> {
                Draw.color(e.color);
                Angles.randLenVectors(e.id, 1, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) ->
                        Fill.square(e.x + x, e.y + y, e.fslope() * 1.5f + 0.1f, 45f)
                );
            }),
            laserChargeBegin = new Effect(60f, e -> {
                Draw.color(e.color);
                Fill.square(e.x, e.y, e.fin() * 3f, 45f);

                Draw.color();
                Fill.square(e.x, e.y, e.fin() * 2f, 45f);
            }),
            freezeEffect = new Effect(30f, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 2);
                Lines.poly(e.x, e.y, 6, 4f + e.rotation * 1.5f * e.finpow(), Mathf.randomSeed(e.id) * 360f);
                Draw.color();
                integer = 0;
                Angles.randLenVectors(e.id, 5, e.rotation * 1.6f * e.fin() + 16f, e.fin() * 33f, 360f, (x, y) -> {
                    Drawn.snowFlake(e.x + x, e.y + y, e.finpow() * 60f, Mathf.randomSeed(e.id + (long) integer) * 2 + 2);
                    integer++;
                });
                Angles.randLenVectors(e.id + 1, 3, e.rotation * 2.1f * e.fin() + 7f, e.fin() * -19f, 360f, (x, y) -> {
                    Drawn.snowFlake(e.x + x, e.y + y, e.finpow() * 60f, Mathf.randomSeed(e.id + (long) integer) * 2 + 2);
                    integer++;
                });
            }),
            giantSplash = new Effect(30f, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(2f * e.fout());
                Lines.circle(e.x, e.y, e.finpow() * 20f);
                integer = 0;
                Angles.randLenVectors(e.id, 11, 4f + 40f * e.finpow(), (x, y) -> {
                    integer++;
                    Fill.circle(e.x + x, e.y + y, e.fslope() * Mathf.randomSeed(e.id + integer, 5f, 9f) + 0.1f);
                });
            }),
            hotSteam = new Effect(150f, e -> {
                Draw.color(e.color, e.fout() * 0.9f);
                integer = 0;
                Angles.randLenVectors(e.id, 2, 10f + 20f * e.fin(), (x, y) -> {
                    integer++;
                    Fill.circle(e.x + x, e.y + y, e.fin() * Mathf.randomSeed(e.id + integer, 15f, 19f) + 0.1f);
                });
            }).layer(Layer.flyingUnit + 1f),
            iceSheet = new Effect(540f, e -> {
                Draw.color(Color.white, e.color, 0.3f);
                integer = 0;
                float fin2 = Mathf.clamp(e.fin() * 5f);
                Angles.randLenVectors(e.id, 1, 16f + 2f * fin2, (x, y) -> {
                    integer++;
                    Fill.poly(e.x + x, e.y + y, 6, fin2 * Mathf.randomSeed(e.id + integer, 6f, 13f) * Mathf.clamp(9f * e.fout()) + 0.1f);
                });
            }).layer(Layer.debris - 1.1f),
            shootFlake = new Effect(21f, e -> {
                Draw.color(e.color, Color.white, e.fout());

                for (int i = 0; i < 6; i++) {
                    Drawf.tri(e.x, e.y, 3f * e.fout(), 12f, e.rotation + Mathf.randomSeed(e.id, 360f) + 60f * i);
                }
            }),
            plasmaedEffect = new Effect(50f, e -> {
                Draw.color(Liquids.cryofluid.color, Color.white.cpy().mul(0.25f, 0.25f, 1f, e.fout()), e.fout() / 6f + Mathf.randomSeedRange(e.id, 0.1f));

                Fill.square(e.x, e.y, e.fslope() * 2f, 45f);
            }),
            laserBreakthroughChargeBegin = new Effect(100f, 100f, e -> {
                Draw.color(Pal.lancerLaser);
                Lines.stroke(e.fin() * 3f);

                Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
                Fill.circle(e.x, e.y, e.fin() * 23.5f);

                Angles.randLenVectors(e.id, 20, 50f * e.fout(), (x, y) ->
                        Fill.circle(e.x + x, e.y + y, e.fin() * 6f)
                );

                Draw.color();
                Fill.circle(e.x, e.y, e.fin() * 13);
            }),
            laserBreakthroughChargeBegin2 = new Effect(100f, 100f, e -> {
                Draw.color(Pal.lancerLaser);
                Lines.stroke(e.fin() * 3f);

                Lines.circle(e.x, e.y, 4f + e.fout() * 120f);
                Fill.circle(e.x, e.y, e.fin() * 23.5f);

                Angles.randLenVectors(e.id, 20, 50f * e.fout(), (x, y) ->
                        Fill.circle(e.x + x, e.y + y, e.fin() * 6f)
                );

                Draw.color();
                Fill.circle(e.x, e.y, e.fin() * 13);
            }),
            distSplashFx = new Effect(80, e -> {
                if (!(e.data instanceof Float[] dat)) return;
                Draw.color(Pal.lancerLaser, Pal.place, e.fin());
                Lines.stroke(2 * e.fout());
                Lines.circle(e.x, e.y, dat[0] * e.fin());
            }) {
                @Override
                public void at(float x, float y, float rotation, Object data) {
                    Effect effect = this;
                    if ((data instanceof Float[] dat)) effect.lifetime = dat[1];

                    create(x, y, rotation, Color.white, data);
                }
            },
            distStart = new Effect(45, e -> {
                if (!(e.data instanceof Float data)) return;

                float centerf = Color.clear.toFloatBits();
                float edgef = Tmp.c2.set(Pal.lancerLaser).a(e.fout()).toFloatBits();
                float sides = Mathf.ceil(Lines.circleVertices(data) / 2f) * 2;
                float space = 360f / sides;

                for (int i = 0; i < sides; i += 2) {
                    float px = Angles.trnsx(space * i, data);
                    float py = Angles.trnsy(space * i, data);
                    float px2 = Angles.trnsx(space * (i + 1), data);
                    float py2 = Angles.trnsy(space * (i + 1), data);
                    float px3 = Angles.trnsx(space * (i + 2), data);
                    float py3 = Angles.trnsy(space * (i + 2), data);
                    Fill.quad(e.x, e.y, centerf, e.x + px, e.y + py, edgef, e.x + px2, e.y + py2, edgef, e.x + px3, e.y + py3, edgef);
                }
            }),
            laserFractalCharge = new Effect(120f, e -> {
                float radius = 10 * 8;
                float[] p = {0, 0};

                Angles.randLenVectors(e.id, 3, radius / 2 + Interp.pow3Out.apply(1 - e.fout(0.5f)) * radius * 1.25f, (x, y) -> {
                    e.scaled(60, ee -> {
                        ee.scaled(30, e1 -> {
                            p[0] = Mathf.lerp(x, 0, e1.fin(Interp.pow2));
                            p[1] = Mathf.lerp(y, 0, e1.fin(Interp.pow2));
                        });

                        Lines.stroke(ee.fout(0.5f), Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f).a(ee.fout(0.5f)));
                        Lines.line(e.x + x, e.y + y, e.x + p[0], e.y + p[1]);
                    });
                });
            }),
            laserFractalChargeBegin = new Effect(90f, e -> {
                int[] r = {9, 10, 11, 12};

                e.scaled(60, ee -> r[0] *= (int) ee.fin());
                e.scaled(40, ee -> r[1] *= (int) ee.fin());
                e.scaled(40, ee -> r[2] *= (int) ee.fin());
                e.scaled(60, ee -> r[3] *= (int) ee.fin());

                Draw.color(HIPal.lancerSap3.cpy().a(0.1f + 0.55f * e.fslope()));
                Lines.arc(e.x, e.y, r[0], 0.6f, Time.time * 8 - 60);
                Lines.arc(e.x, e.y, r[1], 0.6f, Time.time * 5);
                Draw.color(Pal.lancerLaser.cpy().lerp(Pal.sapBullet, 0.5f + 0.5f * Mathf.sin(16 * e.fin())).a(0.25f + 0.8f * e.fslope()));
                Lines.arc(e.x, e.y, r[2], 0.4f, Time.time * -6 + 121);
                Lines.arc(e.x, e.y, r[3], 0.4f, Time.time * -4 + 91);
            }),
            smallChainLightning = new Effect(40f, 300f, e -> {
                if (!(e.data instanceof Position p)) return;

                float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
                Tmp.v1.set(p).sub(e.x, e.y).nor();

                float normx = Tmp.v1.x, normy = Tmp.v1.y;
                float range = 6f;
                int links = Mathf.ceil(dst / range);
                float spacing = dst / links;

                Lines.stroke(2.5f * e.fout());
                Draw.color(Color.white, e.color, e.fin());

                Lines.beginLine();

                Lines.linePoint(e.x, e.y);

                rand.setSeed(e.id);

                for (int i = 0; i < links; i++) {
                    float nx, ny;
                    if (i == links - 1) {
                        nx = tx;
                        ny = ty;
                    } else {
                        float len = (i + 1) * spacing;
                        Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                        nx = e.x + normx * len + Tmp.v1.x;
                        ny = e.y + normy * len + Tmp.v1.y;
                    }

                    Lines.linePoint(nx, ny);
                }

                Lines.endLine();
            }),
            chainLightning = new Effect(30f, 300f, e -> {
                if (!(e.data instanceof Position p)) return;

                float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
                Tmp.v1.set(p).sub(e.x, e.y).nor();

                float normx = Tmp.v1.x, normy = Tmp.v1.y;
                float range = 6f;
                int links = Mathf.ceil(dst / range);
                float spacing = dst / links;

                Lines.stroke(4f * e.fout());
                Draw.color(Color.white, e.color, e.fin());

                Lines.beginLine();

                Lines.linePoint(e.x, e.y);

                rand.setSeed(e.id);

                for (int i = 0; i < links; i++) {
                    float nx, ny;
                    if (i == links - 1) {
                        nx = tx;
                        ny = ty;
                    } else {
                        float len = (i + 1) * spacing;
                        Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                        nx = e.x + normx * len + Tmp.v1.x;
                        ny = e.y + normy * len + Tmp.v1.y;
                    }

                    Lines.linePoint(nx, ny);
                }

                Lines.endLine();
            }),
            craft = new Effect(10, e -> {
                Draw.color(Pal.accent, Color.gray, e.fin());
                Lines.stroke(1);
                Lines.spikes(e.x, e.y, e.fin() * 4, 1.5f, 6);
            }),
            denseCraft = new Effect(10, e -> {
                Draw.color(HIPal.dense, Color.gray, e.fin());
                Lines.stroke(1);
                Lines.spikes(e.x, e.y, e.finpow() * 4.5f, 1f, 6);
            }),
            diriumCraft = new Effect(10, e -> {
                Draw.color(Color.white, HIPal.dirium, e.fin());
                Lines.stroke(1);
                Lines.spikes(e.x, e.y, e.fin() * 4, 1.5f, 6);
            }),
            rockFx = new Effect(10f, e -> {
                Draw.color(Color.orange, Color.gray, e.fin());
                Lines.stroke(1f);
                Lines.spikes(e.x, e.y, e.fin() * 4f, 1.5f, 6);
            }),
            craftFx = new Effect(10f, e -> {
                Draw.color(Pal.accent, Color.gray, e.fin());
                Lines.stroke(1f);
                Lines.spikes(e.x, e.y, e.fin() * 4f, 1.5f, 6);
            }),
            distortFx = new Effect(18, e -> {
                if (!(e.data instanceof Float dat)) return;
                Draw.color(Pal.lancerLaser, Pal.place, e.fin());
                Fill.square(e.x, e.y, 0.1f + e.fout() * 2.5f, dat);
            }),
            longSmoke = new Effect(80f, e -> {
                Draw.color(Color.gray, Color.clear, e.fin());
                Angles.randLenVectors(e.id, 2, 4 + e.fin() * 4, (x, y) -> {
                    Fill.circle(e.x + x, e.y + y, 0.2f + e.fin() * 4);
                });
            }).layer(Layer.flyingUnit - 1f),
            blockMelt = new Effect(400f, e -> {
                Draw.color(Color.coral, Color.orange, Mathf.absin(9f, 1f));
                integer = 0;
                float f = Mathf.clamp(e.finpow() * 5f);
                Angles.randLenVectors(e.id, 15, 2 + f * f * 16f, (x, y) -> {
                    integer++;
                    Fill.circle(e.x + x, e.y + y, 0.01f + e.fout() * Mathf.randomSeed(e.id + integer, 2f, 6f));
                });
            }),
            absorb = new Effect(12, e -> {
                Draw.color(e.color);
                Lines.stroke(2f * e.fout());
                Lines.circle(e.x, e.y, 5f * e.fout());
            }),
            deflect = new Effect(12, e -> {
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(2f * e.fout());
                Angles.randLenVectors(e.id, 4, 0.1f + 8f * e.fout(), e.rotation, 60f, (x, y) ->
                        Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 3f + 1f)
                );
            }),
            forceShrink = new Effect(20, e -> {
                Draw.color(e.color, e.fout());
                if (Vars.renderer.animateShields) {
                    Fill.poly(e.x, e.y, Lines.circleVertices(e.rotation * e.fout()), e.rotation * e.fout());
                } else {
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f);
                    Fill.circle(e.x, e.y, e.rotation * e.fout());
                    Draw.alpha(1f);
                    Lines.circle(e.x, e.y, e.rotation * e.fout());
                }
            }).layer(Layer.shields),
            shieldBreak = new Effect(40, e -> {
                Draw.color(e.color);
                Lines.stroke(3f * e.fout());
                Lines.circle(e.x, e.y, e.rotation + e.fin());
            }).followParent(true),
            whirl = new Effect(65f, e -> {
                for (int i = 0; i < 2; i++) {
                    int h = i * 2;
                    float r1 = Interp.exp5In.apply((Mathf.randomSeedRange(e.id + h, 1f) + 1f) / 2f);
                    float r2 = (Mathf.randomSeedRange(e.id * 2L + h, 360) + 360f) / 2f;
                    float r3 = (Mathf.randomSeedRange(e.id * 4L + h, 5) + 5f) / 2f;
                    float a = r2 + ((180f + r3) * e.fin());

                    Tmp.v1.trns(a, r1 * 70f * e.fout());

                    Draw.color(Pal.lancerLaser);
                    Lines.stroke(e.fout() + 0.25f);
                    Lines.lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, a + 270f + 15f, e.fout() * 8f);
                }
            }),
            orbTrail = new Effect(43f, e -> {
                Tmp.v1.trns(Mathf.randomSeed(e.id) * 360f, Mathf.randomSeed(e.id * 341L) * 12f * e.fin());

                Drawf.light(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 4.7f * e.fout() + 3f, Pal.surge, 0.6f);

                Draw.color(Pal.surge);
                Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fout() * 2.7f);
            }).layer(Layer.bullet - 0.01f),
            orbCharge = new Effect(38f, e -> {
                Draw.color(Pal.surge);
                Angles.randLenVectors(e.id, 2, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f));
            }),
            orbChargeBegin = new Effect(71f, e -> {
                Draw.color(Pal.surge);
                Fill.circle(e.x, e.y, e.fin() * 3f);

                Draw.color();
                Fill.circle(e.x, e.y, e.fin() * 2f);
            }),
            currentCharge = new Effect(32f, e -> {
                Draw.color(Pal.surge, Color.white, e.fin());
                Angles.randLenVectors(e.id, 8, 420f + Mathf.random(24f, 28f) * e.fout(), e.rotation, 4f, (x, y) -> {
                    Lines.stroke(0.3f + e.fout() * 2f);
                    Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 14f + 0.5f);
                });

                Lines.stroke(e.fin() * 1.5f);
                Lines.circle(e.x, e.y, e.fout() * 60f);
            }),
            currentChargeBegin = new Effect(260f, e -> {
                Draw.color(Pal.surge);
                Fill.circle(e.x, e.y, e.fin() * 7f);

                Draw.color();
                Fill.circle(e.x, e.y, e.fin() * 3f);
            }),
            plasmaFragAppear = new Effect(12f, e -> {
                Draw.color(Color.white);
                Drawf.tri(e.x, e.y, e.fin() * 12f, e.fin() * 13f, e.rotation);
            }).layer(Layer.bullet - 0.01f),
            plasmaFragDisappear = new Effect(12f, e -> {
                Draw.color(Pal.surge, Color.white, e.fin());
                Drawf.tri(e.x, e.y, e.fout() * 10f, e.fout() * 11f, e.rotation);
            }).layer(Layer.bullet - 0.01f),
            surgeSplash = new Effect(40f, 100f, e -> {
                Draw.color(Pal.surge);
                Lines.stroke(e.fout() * 2);
                Lines.circle(e.x, e.y, 4 + e.finpow() * 65);

                Draw.color(Pal.surge);

                for (int i = 0; i < 4; i++) {
                    Drawf.tri(e.x, e.y, 6, 100 * e.fout(), i * 90);
                }

                Draw.color();

                for (int i = 0; i < 4; i++) {
                    Drawf.tri(e.x, e.y, 3, 35 * e.fout(), i * 90);
                }
            }),
            oracleCharge = new Effect(30f, e -> {
                Draw.color(Pal.lancerLaser);
                Tmp.v1.trns(Mathf.randomSeed(e.id, 360f) + Time.time, (1 - e.finpow()) * 20f);
                Fill.square(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin() * 4.5f, 45f);
            }),
            oracleChargeBegin = new Effect(40, e -> {
                Draw.color(Pal.lancerLaser);
                Fill.circle(e.x, e.y, e.fin() * 6f);
            }),
            monolithRingEffect = new Effect(60f, e -> {
                if (e.data instanceof Float data) {
                    Draw.color(Pal.lancerLaser);

                    Lines.stroke(e.fout() * 3f * data);
                    Lines.circle(e.x, e.y, e.finpow() * 24f * data);
                }
            }),
            falseLightning = new Effect(10f, 500f, e -> {
                if (!(e.data instanceof Float length)) return;
                int lenInt = Mathf.round(length / 8f);
                Lines.stroke(3f * e.fout());
                Draw.color(e.color, Color.white, e.fin());
                //unity.Unity.print(lenInt,"  ",length);
                for (int i = 0; i < lenInt; i++) {
                    float offsetXA = i == 0 ? 0 : Mathf.randomSeed(e.id + i * 6413L, -4.5f, 4.5f);
                    float offsetYA = length / lenInt * i;
                    int j = i + 1;
                    float offsetXB = j == lenInt ? 0 : Mathf.randomSeed(e.id + j * 6413L, -4.5f, 4.5f);
                    float offsetYB = length / lenInt * j;
                    Tmp.v1.trns(e.rotation, offsetYA, offsetXA);
                    Tmp.v1.add(e.x, e.y);
                    Tmp.v2.trns(e.rotation, offsetYB, offsetXB);
                    Tmp.v2.add(e.x, e.y);
                    Lines.line(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, false);
                    Fill.circle(Tmp.v1.x, Tmp.v1.y, Lines.getStroke() / 2f);
                }
            }),
            healLaser = new Effect(60f, e -> {
                if (!(e.data instanceof Position[] temp)) return;
                float[] reduction = new float[]{0f, 1.5f};
                Position a = temp[0], b = temp[1];
                for (int i = 0; i < 2; i++) {
                    Draw.color(i == 0 ? Pal.heal : Color.white);
                    Lines.stroke((3f - reduction[i]) * e.fout());
                    Lines.line(a.getX(), a.getY(), b.getX(), b.getY());
                    Fill.circle(a.getX(), a.getY(), (2.5f - reduction[i]) * e.fout());
                    Fill.circle(b.getX(), b.getY(), (2.5f - reduction[i]) * e.fout());
                }
            }),
            pylonLaserCharge = new Effect(200f, 180f, e -> {
                e.scaled(100f, c -> {
                    float slope = Interp.pow3Out.apply(Mathf.mod(c.fout() * 3f, 1f));
                    float rot = Mathf.round(c.fout() * 4f);

                    Draw.color(HIPal.monolithLight);
                    Fill.circle(c.x, c.y, 15f * c.fin());

                    Draw.z(Layer.effect + 1f);
                    Draw.blend(Blending.additive);

                    Tmp.c1.set(HIPal.monolithLight).a(c.fin(Interp.pow3Out));
                    Fill.light(c.x, c.y, 27, 40f * c.fout(Interp.pow10Out), Tmp.c1, Color.clear);

                    Tmp.c1.a((1f - slope) * 0.5f);
                    Fill.light(c.x, c.y, 4, 80f * slope, Color.clear, Tmp.c1);

                    Draw.blend();
                });

                shoot:
                {
                    if (e.fin() < 0.5f) break shoot;

                    float fin = Mathf.curve(e.fin(), 0.5f, 1f);
                    float finscaled = Mathf.curve(fin, 0f, 0.8f);
                    float fin5 = Interp.pow5Out.apply(fin);
                    float fin3 = Interp.pow3Out.apply(fin);
                    float fin2 = Interp.pow2Out.apply(fin);
                    float fout = 1f - fin;

                    float rot = 370f * fin5;
                    float rad = 160f * Interp.pow5Out.apply(finscaled);

                    Lines.stroke(3 * fout);
                    for (int i = 0; i < 2; i++) {
                        Draw.color(HIPal.monolithLight, HIPal.monolith, fin);
                        Lines.square(e.x, e.y, 200f * fin3, rot * Mathf.signs[i]);

                        Draw.color(HIPal.monolith);
                        Lines.square(e.x, e.y, 100f * fin5, rot * Mathf.signs[i] + 45f);
                    }

                    Draw.color(HIPal.monolithLight, HIPal.monolithDark, fin);
                    Angles.randLenVectors(e.id, 48, fin3 * 180f, (x, y) -> {
                        Fill.circle(e.x + x, e.y + y, 5f * fout);
                    });

                    Draw.z(Layer.effect + 1f);
                    Draw.blend(Blending.additive);

                    Tmp.c1.set(HIPal.monolithLight).a(1f - fin3);
                    Fill.light(e.x, e.y, 27, 40f, Tmp.c1, Color.clear);

                    Tmp.c1.set(HIPal.monolithDark).a((1f - fin2) * 0.8f);
                    Fill.light(e.x, e.y, 4, rad, Color.clear, Tmp.c1);
                    Draw.blend();
                }
            }),
            vaporation = new Effect(23f, e -> {
                if (!(e.data instanceof Position[] temp)) return;
                Tmp.v1.set(temp[0]);
                Tmp.v1.lerp(temp[1], e.fin());
                Draw.color(Pal.darkFlame, Pal.darkerGray, e.fin());
                Fill.circle(Tmp.v1.x + temp[2].getX(), Tmp.v1.y + temp[2].getY(), e.fout() * 5f);
            }).layer(Layer.flyingUnit + 0.012f),
            sparkleFx = new Effect(15f, e -> {
                Draw.color(Color.white, e.color, e.fin());
                integer = 1;
                Angles.randLenVectors(e.id, e.id % 3 + 1, e.rotation * 4f + 4f, (x, y) -> {
                    Drawn.spark(e.x + x, e.y + y, e.fout() * 4f, 0.5f + e.fout() * 2.2f, e.id * integer);
                    integer++;
                });
            }),
            upgradeBlockFx = new Effect(90f, e -> {
                Draw.color(Color.white, Color.green, e.fin());
                Lines.stroke(e.fout() * 6f * e.rotation);
                Lines.square(e.x, e.y, (e.fin() * 4f + 2f) * e.rotation, 0f);
                integer = 1;
                Angles.randLenVectors(e.id, e.id % 3 + 7, e.rotation * 4f + 4f + 8f * e.finpow(), (x, y) -> {
                    Drawn.spark(e.x + x, e.y + y, e.fout() * 5f, e.fout() * 3.5f, e.id * integer);
                    integer++;
                });
            }),
            imberCircleSparkCraftingEffect = new Effect(30f, e -> {
                Draw.color(Pal.surge);
                Lines.stroke(e.fslope());
                Lines.circle(e.x, e.y, e.fin() * 20f);
            }),
            ringFx = new Effect(25f, e -> {
                if (!(e.data instanceof Unit u)) return;
                if (!u.isValid() || u.dead) return;
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 1.5f);
                Lines.circle(u.x, u.y, 8f);
            }),
            ringEffect2 = new Effect(25f, e -> {
                if (e.data instanceof Unit unit) {
                    if (!unit.isValid() || unit.dead) return;

                    Draw.color(Color.white, e.color, e.fin());
                    Lines.stroke(e.fout() * 1.5f);
                    Lines.circle(unit.x, unit.y, 12f);
                }
            }),
            smallRingFx = new Effect(20f, e -> {
                if (!(e.data instanceof Unit u)) return;
                if (!u.isValid() || u.dead) return;
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fin());
                Lines.circle(u.x, u.y, e.fin() * 5f);
            }),
            smallRingEffect2 = new Effect(20f, e -> {
                if (e.data instanceof Unit unit) {
                    if (!unit.isValid() || unit.dead) return;

                    Draw.color(Color.white, e.color, e.fin());
                    Lines.stroke(e.fin());
                    Lines.circle(unit.x, unit.y, e.fin() * 7.5f);
                }
            }),
            squareFx = new Effect(25f, e -> {
                if (!(e.data instanceof Unit u)) return;
                if (!u.isValid() || u.dead) return;
                Draw.color(Color.white, e.color, e.fin());
                Lines.stroke(e.fout() * 2.5f);
                Lines.square(u.x, u.y, e.fin() * 18f, 45f);
            }),
            expAbsorb = new Effect(15f, e -> {
                Lines.stroke(e.fout() * 1.5f);
                Draw.color(HIPal.exp);
                Lines.circle(e.x, e.y, e.fin() * 2.5f + 1f);
            }),
            expDespawn = new Effect(15f, e -> {
                Draw.color(HIPal.exp);
                Angles.randLenVectors(e.id, 7, 2f + 5 * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout()));
            }),
            maxDamageFx = new Effect(16f, e -> {
                Draw.color(Color.orange);
                Lines.stroke(2.5f * e.fin());
                Lines.square(e.x, e.y, e.rotation * 4f);
            }),
            withstandFx = new Effect(16f, e -> {
                Draw.color(Color.orange);
                Lines.stroke(1.2f * e.rotation * e.fout());
                Lines.square(e.x, e.y, e.rotation * 4f);
            }),
            ahhimaLiquidNow = new Effect(45f, e -> {
                Draw.color(Color.gray, Color.clear, e.fin());
                Angles.randLenVectors(e.id, 3, 2.5f + e.fin() * 6f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2f + e.fin() * 3f));
                Draw.color(HIPal.lava, HIPal.lava2, e.fout());
                Angles.randLenVectors(e.id + 1, 4, 1 + e.fin() * 4f, (x, y) -> Fill.circle(e.x + x, e.y + y, 0.2f + e.fout() * 1.3f));
            }),
            blinkFx = new Effect(30f, e -> {
                Draw.color(Color.white, HIPal.dirium, e.fin());
                Lines.stroke(3f * e.rotation * e.fout());
                Lines.square(e.x, e.y, e.rotation * 4f * e.finpow());
            }),
            tpOut = new Effect(30f, e -> {
                Draw.color(HIPal.dirium);
                Lines.stroke(3f * e.fout());
                Lines.square(e.x, e.y, e.finpow() * e.rotation, 45f);
                Lines.stroke(5f * e.fout());
                Lines.square(e.x, e.y, e.fin() * e.rotation, 45f);
                Angles.randLenVectors(e.id, 10, e.fin() * (e.rotation + 10f), (x, y) -> Fill.square(e.x + x, e.y + y, e.fout() * 4f, 100f * Mathf.randomSeed(e.id + 1) * e.fin()));
            }),
            tpIn = new Effect(50f, e -> {
                if (!(e.data instanceof UnitType type)) return;
                TextureRegion region = type.fullIcon;
                Draw.color();
                Draw.mixcol(HIPal.dirium, 1f);
                Draw.rect(region, e.x, e.y, region.width * Draw.scl * e.fout(), region.height * Draw.scl * e.fout(), e.rotation);
                Draw.mixcol();
            }),
            tpFlash = new Effect(30f, e -> {
                if (!(e.data instanceof Unit unit) || !unit.isValid()) return;
                TextureRegion region = unit.type.fullIcon;
                Draw.mixcol(HIPal.diriumLight, 1f);
                Draw.alpha(e.fout());
                Draw.rect(region, unit.x, unit.y, unit.rotation - 90f);
                Draw.mixcol();
                Draw.color();
            }).layer(Layer.flyingUnit + 1f),
            empShockwave = new Effect(30f, 800f, e -> {
                Draw.color(Pal.lancerLaser);
                Lines.stroke(e.fout() + 0.5f);
                Lines.circle(e.x, e.y, e.rotation * Mathf.curve(e.fin(), 0f, 0.23f));
            }),
            empCharge = new Effect(70f, e -> {
                Draw.color(Pal.lancerLaser);
                Drawn.shiningCircle(e.id * 63, Time.time, e.x, e.y, 4f * e.fin(), 7, 15f, 24f * e.fin(), 2f * e.fin());
                Draw.color(Color.white);
                Drawn.shiningCircle(e.id * 63, Time.time, e.x, e.y, 2f * e.fin(), 7, 15f, 38f * e.fin(), e.fin());
                Draw.color();
            }),
            blueTriangleTrail = new Effect(50f, e -> {
                Draw.color(Color.white, Pal.lancerLaser, e.fin());
                Fill.poly(e.x, e.y, 3, 4f * e.fout(), e.rotation + 180f);
            }),
            arcCharge = new Effect(27f, e -> {
                Draw.color(Color.valueOf("606571"), Color.valueOf("6c8fc7"), e.fin());

                Angles.randLenVectors(e.id, 2, e.fout() * 40f, e.rotation, 135f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 6, 1f + Mathf.sin(e.fin() * 3f, 1f, 2f) * 5f, e.rotation);
                });
            }),
            arcSmoke = new Effect(27f, e -> {
                Draw.color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());

                Angles.randLenVectors(e.id, 3, e.finpow() * 20f, e.rotation, 180f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 6, e.fout() * 9f, e.rotation);
                });
            }),
            arcSmoke2 = new Effect(27f, e -> {
                Draw.color(Color.valueOf("6c8fc7"), Color.valueOf("606571"), e.fin());

                Tmp.v1.trns(e.rotation, e.fin() * 4.6f * 15f);
                Fill.poly(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 6, e.fout() * 16f, e.rotation);
            }),
            sparkBoi = new Effect(15f, e -> {
                Draw.color(e.color);
                for (int j = 0; j < 4; j++) {
                    Drawf.tri(e.x, e.y, (float) e.data - e.fin(), (float) e.data + 1 - e.fin() * ((float) e.data + 1), 90 * j + e.rotation);
                }
                Draw.color();
            });

    public static final LightningEffect
            groundCrack = new LightningEffect(20f, 500f, 1.5f).layer(Layer.debris - 0.01f).extend(true).width(10f),
            staticLightning = new LightningEffect(10f, 500f, 2f).colorFrom(Color.white).layer(Layer.bullet + 0.01f).width(5f),
            teslaLightning = new LightningEffect(10f, 500f, 3.5f).colorFrom(Color.white).layer(Layer.bullet + 0.01f).shrink(true),
            flameBeam = new LightningEffect(10f, 500f, 3f).colorFrom(Color.white).layer(Layer.bullet + 0.01f).width(16f).shrink(true),
            blazeBeam = new LightningEffect(10f, 500f, 4f).colorFrom(Color.white).layer(Layer.bullet + 0.01f).width(20f).shrink(true),
            empLightning = new LightningEffect(60f, 500f, 2f).colorFrom(Color.white).width(12f).extend(true);

    public static Effect impactExplode(float size, float lifeTime) {
        return impactExplode(size, lifeTime, false);
    }

    public static Effect impactExplode(float size, float lifeTime, boolean heightBloom) {
        return new Effect(lifeTime, e -> {
            float rate = e.fout(Interp.pow2In);
            float l = size * 1.16f * rate;
            float w = size * 0.1f * rate;

            float fout = e.fout();
            float fin = e.fin();
            Drawf.light(e.x, e.y, fout * size * 1.15f, e.color, 0.7f);

            float x = e.x, y = e.y;
            int id = e.id;
            DrawAcceptor<Bloom> draw = n -> {
                Draw.color(e.color);
                Draws.drawLightEdge(x, y, l, w, l, w);
                Lines.stroke(size * 0.08f * fout);
                Lines.circle(x, y, size * 0.55f * fout);
                Lines.stroke(size * 0.175f * fout);
                Lines.circle(x, y, size * 1.25f * (1 - Mathf.pow(fout, 3)));

                Angles.randLenVectors(id, 12, 26, (dx, dy) -> {
                    float s = Mathf.randomSeed((int) (id + dx), 4f, 8f);
                    Fill.circle(x + dx * fin, y + dy * fin, s * fout);
                });
                Draw.reset();
            };

            if (heightBloom) {
                Draw.z(Layer.flyingUnit + 1);
                Draws.drawBloomUponFlyUnit(null, draw);
            } else {
                draw.draw(null);
            }

            Draw.z(Layer.effect + 0.001f);
            Lines.stroke((size * 0.065f * fout));
            Angles.randLenVectors(e.id + 1, e.finpow() + 0.001f, (int) (size / 2.25f), size * 1.2f, (dx, dy, in, out) -> {
                Lines.lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 3 + out * size * 0.7f);
                Drawf.light(e.x + dx, e.y + dy, out * size / 2, Draw.getColor(), 0.8f);
            });
        });
    }

    public static Effect shrinkParticle(float radius, float maxSize, float lifeTime, Color color) {
        return new Effect(lifeTime, e -> {
            Draw.z(Layer.effect);
            Draw.color(color == null ? e.color : color);
            Draw.alpha(1 - Mathf.clamp((e.fin() - 0.75f) / 0.25f));

            Angles.randLenVectors(e.id, 2, radius, (x, y) -> {
                float size = Mathf.randomSeed(e.id, maxSize);

                float le = e.fout(Interp.pow3Out);
                Fill.square(e.x + x * le, e.y + y * le, size * e.fin(), Mathf.lerp(Mathf.randomSeed(e.id, 360), Mathf.randomSeed(e.id, 360), e.fin()));
            });
        });
    }

    public static Effect graphiteCloud(float radius, int density) {
        return new Effect(360f, e -> {
            Draw.z(Layer.bullet - 5);
            Draw.color(Pal.stoneGray);
            Draw.alpha(0.6f);
            Angles.randLenVectors(e.id, density, radius, (x, y) -> {
                float size = Mathf.randomSeed((int) (e.id + x), 14, 18);
                float i = e.fin(Interp.pow3Out);
                Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out));
            });
            Draw.z(Layer.effect);
            Draw.color(Items.graphite.color);
            Angles.randLenVectors(e.id + 1, (int) (density * 0.65f), radius, (x, y) -> {
                float size = Mathf.randomSeed((int) (e.id + x), 7, 10);
                size *= e.fout(Interp.pow4In);
                size += Mathf.absin(Time.time + Mathf.randomSeed((int) (e.id + x), 2 * Mathf.pi), 3.5f, 2f);
                float i = e.fin(Interp.pow3Out);
                Draws.drawLightEdge(e.x + x * i, e.y + y * i, size, size * 0.15f, size, size * 0.15f);
            });
        });
    }

    public interface EffectParam {
        void draw(long id, float x, float y, float rot, float fin);
    }
}
