package HeavyIndustry.maps;

import arc.graphics.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.noise.*;

public abstract class ColorPass {
    public abstract @Nullable Color color(Vec3 pos, float height);

    public static class SphereColorPass extends ColorPass {
        public Vec3 pos;
        public float radius;
        public Color out;

        public SphereColorPass(Vec3 pos, float radius, Color out) {
            this.pos = pos;
            this.radius = radius;
            this.out = out;
        }

        @Override
        public Color color(Vec3 pos, float height) {
            if (pos.dst(this.pos) < radius) return out;
            return null;
        }
    }
    public static class NoiseColorPass extends ColorPass {
        public Vec3 offset = new Vec3();
        public int seed;
        public double octaves = 1.0;
        public double persistence = 1.0;
        public double scale = 1.0;
        public float magnitude = 1;
        public float min = 0f, max = 1f;
        public Color out = Color.white;

        @Override
        public Color color(Vec3 pos, float height) {
            float noise = Simplex.noise3d(seed, octaves, persistence, scale, pos.x + offset.x, pos.y + offset.y, pos.z + offset.z) * magnitude;
            if (min <= noise && noise <= max) return out;
            return null;
        }
    }
    public static class FlatColorPass extends ColorPass {
        public float min = 0f, max = 1f;
        public Color out = Color.white;

        @Override
        public Color color(Vec3 pos, float height) {
            if (min <= height && height <= max) return out;
            return null;
        }
    }
}
