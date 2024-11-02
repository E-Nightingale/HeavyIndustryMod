package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

import static arc.Core.*;

/** @author LaoHuaJi */
public class TiledFloor extends Floor {
    public TextureRegion full;
    public TextureRegion[][] largeSpilt;

    public int splitTileSize = 4;
    public int splitVariants = 12;

    public TiledFloor(String name) {
        super(name);
    }

    public TiledFloor(String name, int sSize, int sVar) {
        super(name);
        splitTileSize = sSize;
        splitVariants = sVar;
    }

    @Override
    public void load(){
        super.load();

        full = atlas.find(name + "-full");
        largeSpilt = new TextureRegion[splitTileSize * splitVariants][splitTileSize];
        for (int i = 0; i < splitVariants; i++){
            largeSpilt = full.split(32, 32);
        }
    }

    private void drawTile(Tile tile){
        int tx = tile.x / splitTileSize * splitTileSize;
        int ty = tile.y / splitTileSize * splitTileSize;


        int index = Mathf.randomSeed(Point2.pack(tx, ty), 0, splitVariants - 1);
        int ix = index * splitTileSize + tile.x - tx;
        int iy = splitTileSize - (tile.y - ty) - 1;
        Draw.rect(largeSpilt[ix][iy], tile.worldx(), tile.worldy());
    }

    @Override
    public void drawBase(Tile tile) {
        drawTile(tile);
        Draw.alpha(1f);
        //drawEdges(tile);
        drawOverlay(tile);
    }
}
