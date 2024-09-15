package heavyindustry.graphics;

import arc.files.Fi;
import arc.graphics.Mesh;
import gltfrenzy.loader.GlbReader;
import gltfrenzy.loader.GltfReader;
import gltfrenzy.loader.MeshSetLoader;
import gltfrenzy.loader.NodeLoader;
import gltfrenzy.loader.Scenes3DLoader;
import gltfrenzy.model.MeshSet;
import gltfrenzy.model.Node;
import gltfrenzy.model.Scenes3D;

import static heavyindustry.util.StructUtils.each;
import static arc.Core.app;
import static arc.Core.assets;
import static mindustry.Vars.tree;

/** Defines the {@linkplain Scenes3D 3D model}s this mod offers. */
public class HIModels {
    public static Scenes3D spires;
    public static Mesh spireSmall1, spireSmall2;

    /** Loads the 3D models. Client-side and main thread only! */
    public static void load(){
        assets.setLoader(Scenes3D.class, ".gltf", new Scenes3DLoader(tree, new GltfReader()));
        assets.setLoader(Scenes3D.class, ".glb", new Scenes3DLoader(tree, new GlbReader()));

        assets.setLoader(MeshSet.class, new MeshSetLoader(tree));
        assets.setLoader(Node.class, new NodeLoader(tree));

        Runnable[] loadSync = {(spires = new Scenes3D()).load(file("spires"), tree, null)};

        app.post(() -> {
            each(loadSync, Runnable::run);
            spireSmall1 = spires.mesh("SpireSmall1").containers.first().mesh;
            spireSmall2 = spires.mesh("SpireSmall2").containers.first().mesh;
        });
    }

    public static Fi file(String name){
        return tree.get("models/" + name + ".gltf");
    }
}