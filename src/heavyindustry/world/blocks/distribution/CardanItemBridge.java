package heavyindustry.world.blocks.distribution;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.input.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.meta.*;

import static arc.util.Tmp.*;
import static mindustry.Vars.*;

/**
 * A bridge with the same connection method as the power node.
 * @author _stabu_
 */
public class CardanItemBridge extends ItemBridge {
    public Prov<Seq<Block>> connectBlocksGetter = Seq::new;
    Seq<Block> connectibleBlocks = new Seq<>();
    public Boolf<Building> connectFilter = (building) -> connectibleBlocks.contains(building.block());
    public byte maxConnections = 3;

    public final int timerAccept;
    public int bufferCapacity;

    public CardanItemBridge(String name){
        super(name);
        hasItems = true;
        timerAccept = this.timers++;
        bufferCapacity = 50;
        hasPower = false;
        canOverdrive = true;
        swapDiagonalPlacement = true;
        configClear((CardanItemBridgeBuild b) -> b.link = -1);
    }

    public CardanItemBridgeBuild cast(Building b){
        return (CardanItemBridgeBuild) b;
    }

    @Override
    public void init(){
        super.init();
        Seq<Block> connectibleBlocks = connectBlocksGetter.get();
        if(connectibleBlocks == null) connectibleBlocks = new Seq<>();
        connectibleBlocks.add(this);
        this.connectibleBlocks = connectibleBlocks;
        maxConnections++;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.range, this.range, StatUnit.blocks);
        stats.add(Stat.powerConnections, this.maxConnections - 1, StatUnit.none);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("connections", entity -> new Bar(() ->
                Core.bundle.format("bar.powerlines", cast(entity).realConnections(), maxConnections - 1),
                () -> Pal.items,
                () -> (float) cast(entity).realConnections() / (float)(maxConnections - 1)
        ));
    }

    @Override
    public void drawBridge(BuildPlan req, float ox, float oy, float flip) {
        drawBridge(bridgeRegion, endRegion, new Vec2(req.drawx(), req.drawy()), new Vec2(ox, oy));
        Draw.rect(arrowRegion,
                (req.drawx() + ox) / 2f,
                (req.drawy() + oy) / 2f,
                Angles.angle(req.drawx(), req.drawy(), ox, oy)
        );
    }

    public void drawBridge(TextureRegion bridgeRegion, TextureRegion endRegion, Vec2 pos1, Vec2 pos2) {
        float angle = pos1.angleTo(pos2) - 90;

        if(angle >= 0f && angle < 180f) Draw.yscl = -1f;

        Tmp.v1.set(pos2.x, pos2.y).sub(pos1.x, pos1.y).setLength(tilesize/2f);

        Lines.stroke(8 * Draw.yscl);
        Lines.line(bridgeRegion, pos1.x + Tmp.v1.x, pos1.y + Tmp.v1.y, pos2.x - Tmp.v1.x, pos2.y - Tmp.v1.y, false);

        Draw.rect(endRegion, pos1.x, pos1.y, angle + 90f);
        Draw.xscl = -1f;
        Draw.rect(endRegion, pos2.x, pos2.y, angle + 90f);
        Draw.xscl = Draw.yscl = 1f;
    }

    public Tile findLink(int x, int y){
        return findLinkTile(x, y, true);
    }

    public Tile findLinkTile(int x, int y, boolean checkBlock){
        Tile tile = world.tile(x, y);
        if(tile != null && lastBuild != null && lastBuild.tile != tile){
            boolean validLink = checkBlock ? linkValid(tile, lastBuild.tile) && lastBuild.link == -1 :
                    linkValid(tile, lastBuild.tile, false, true);
            if(validLink) return lastBuild.tile;
        }
        return null;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Tile link = findLinkTile(x, y, false);
        Lines.stroke(1f);
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.placing);

        Draw.reset();
        Draw.color(Pal.placing);
        if(link != null && Vars.world.build(link.x, link.y) instanceof CardanItemBridgeBuild && Math.abs(link.x - x) + Math.abs(link.y - y) > 1){
            Vec2 end = new Vec2(x, y), start = new Vec2(link.x, link.y);
            float angle = Tmp.v1.set(start).sub(end).angle() + 90;
            float layer = Draw.z();
            Draw.z(Layer.blockUnder - 0.3f);

            Lines.poly(new Vec2[]{
                    start.cpy().add(Tmp.v1.trns(angle, -0.4f)),
                    end.cpy().add(Tmp.v1.trns(angle, -0.4f)),
                    end.cpy().add(Tmp.v1.trns(angle, 0.4f)),
                    start.cpy().add(Tmp.v1.trns(angle, 0.4f)),
            }, 0, 0, 8);

            Tmp.v1.set(start).sub(end).setLength(4);
            Vec2 arrowOffset = Tmp.v1.cpy().setLength(1);
            Draw.rect("bridge-arrow", start.x * 8 - arrowOffset.x * 8, start.y * 8 - arrowOffset.y * 8, angle + 90);
            Draw.z(layer);
        }

        Draw.reset();
    }

    @Override
    public TextureRegion[] getGeneratedIcons(){
        return super.getGeneratedIcons();
    }

    /** Change its connection method to range connection. */
    @Override
    public boolean linkValid(Tile tile, Tile other){
        return linkValid(tile, other, true);
    }

    @Override
    public boolean linkValid(Tile tile, Tile other, boolean checkDouble){
        return linkValid(tile, other, checkDouble, false);
    }

    public boolean linkValid(Tile tile, Tile other, boolean checkDouble, boolean old){
        if(old){
            if(other != null && tile != null && this.positionsValid(tile.x, tile.y, other.x, other.y)){
                return (other.block() == tile.block() && tile.block() == this || !(tile.block() instanceof ItemBridge) && other.block() == this) && (other.team() == tile.team() || tile.block() != this) && (!checkDouble || ((ItemBridgeBuild) other.build).link != tile.pos());
            }else{
                return false;
            }
        }else{
            check:{
                if(!(other != null && tile != null) || other.build == null || tile.build == null) break check;
                other = other.build.tile;
                tile = tile.build.tile;
                int offset = other.block().isMultiblock() ? Mathf.floor(other.block().size / 2f) : 0;
                boolean b2 = tile.pos() != other.pos();
                if(tile.block() == this){
                    Vec2 offVec = v1.trns(tile.angleTo(other) + 90f, offset, offset);
                    if(!positionsValid(tile.x, tile.y, Mathf.ceil(other.x + offVec.x), Mathf.ceil(other.y + offVec.y))) break check;
                    CardanItemBridge block = (CardanItemBridge) tile.block();
                    boolean connected = false;
                    if(other.build instanceof ItemBridgeBuild){
                        connected = other.build.<ItemBridgeBuild>as().link == tile.pos();
                    }
                    return ((block.connectFilter.get(other.build)) || !(tile.block() instanceof ItemBridge) && other.block() == this) &&
                            b2 &&
                            (other.team() == tile.team() || other.block() != this) &&

                            (!checkDouble || !connected);
                }else{
                    if(!positionsValid(tile.x, tile.y, other.x, other.y)) break check;
                    boolean b3 = other.team() == tile.team() || tile.block() != this;
                    if(other.block() == this){
                        other.block();
                        boolean b4 = !checkDouble || !(other.build instanceof ItemBridgeBuild && ((ItemBridgeBuild) other.build).link == tile.pos());
                        return b2 && b3 && b4;
                    }else{
                        return (other.block() == tile.block() && tile.block() == this || !(tile.block() instanceof ItemBridge) && other.block() == this)
                                && b3 &&
                                (!checkDouble || ((ItemBridgeBuild) other.build).link != tile.pos());
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean positionsValid(int x1, int y1, int x2, int y2){
        return Mathf.within(x1, y1, x2, y2, range + 0.5f);
    }

    public boolean positionsValid(Point2 pos, Point2 other){
        return positionsValid(pos.x, pos.y, other.x, other.y);
    }

    public void changePlacementPath(Seq<Point2> points, int rotation){
        Placement.calculateNodes(points, this, rotation, this::positionsValid);
    }

    public class CardanItemBridgeBuild extends ItemBridgeBuild{
        ItemBuffer buffer = new ItemBuffer(bufferCapacity);

        public void drawBase(){
            Draw.rect(this.block.region, this.x, this.y, this.block.rotate ? this.rotdeg() : 0.0F);
            this.drawTeamTop();
        }

        @Override
        public void checkIncoming(){
            Tile other;
            for(int i : incoming.toArray()){
                other = Vars.world.tile(i);
                boolean valid = linkValid(this.tile, other, false) && (other.build instanceof ItemBridgeBuild && ((ItemBridgeBuild) other.build).link == this.tile.pos());
                if(!valid){
                    incoming.removeValue(i);
                }
            }
        }

        public int realConnections(){
            return incoming.size + (Vars.world.build(link) instanceof CardanItemBridgeBuild ? 1 : 0);
        }

        public boolean canLinked(){
            return (realConnections() < maxConnections);
        }

        public boolean canReLink(){
            return (realConnections() <= maxConnections && link != -1);
        }

        @Override
        public void updateTile() {
            Building other = world.build(link);
            if(other != null && !linkValid(tile, other.tile)){
                link = -1;
            }
            super.updateTile();
        }

        public void draw(){
            drawBase();

            Draw.z(Layer.power);
            Tile other = Vars.world.tile(link);
            Building build = Vars.world.build(link);
            if(build == this) build = null;
            if(build != null) other = build.tile;
            if(!linkValid(this.tile, other) || build == null || Mathf.zero(Renderer.bridgeOpacity)) return;
            Vec2 pos1 = new Vec2(x, y), pos2 = new Vec2(other.drawx(), other.drawy());

            if(pulse) Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f));

            Draw.alpha(Renderer.bridgeOpacity);

            drawBridge(bridgeRegion, endRegion, pos1, pos2);

            Draw.color();
            int arrows = Mathf.round(pos1.dst(pos2)/arrowSpacing);
            float angle = pos1.angleTo(pos2);
            v2.trns(angle - 45f, 1f, 1f);
            for(float a = 0; a < arrows - 2; ++a) {
                Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity);
                float arrowX, arrowY;
                arrowX = x - v1.x + v2.x * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
                arrowY = y - v1.y + v2.y * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
                Draw.rect(arrowRegion, arrowX, arrowY, angle);
            }
            Draw.reset();
        }

        public void drawSelect(){
            if(linkValid(tile, Vars.world.tile(link))){
                drawInput(Vars.world.tile(link));
            }

            for(int pos : incoming.items){
                drawInput(Vars.world.tile(pos));
            }
            Draw.reset();
        }

        protected void drawInput(Tile other){
            if(linkValid(this.tile, other, false)){
                boolean linked = other.pos() == this.link;
                final float angle = tile.angleTo(other);
                v2.trns(angle, 2f);
                float tx = tile.drawx();
                float ty = tile.drawy();
                float ox = other.drawx();
                float oy = other.drawy();
                float alpha = Math.abs((float) (linked ? 100 : 0) - Time.time * 2f % 100f) / 100f;
                float x = Mathf.lerp(ox, tx, alpha);
                float y = Mathf.lerp(oy, ty, alpha);
                Tile otherLink = linked ? other : tile;
                float rel = (linked ? tile : other).angleTo(otherLink);
                Draw.color(Pal.gray);
                Lines.stroke(2.5f);
                Lines.square(ox, oy, 2f, 45f);
                Lines.stroke(2.5f);
                Lines.line(tx + v2.x, ty + v2.y, ox - v2.x, oy - v2.y);
                Draw.color(linked ? Pal.place : Pal.accent);
                Lines.stroke(1f);
                Lines.line(tx + v2.x, ty + v2.y, ox - v2.x, oy - v2.y);
                Lines.square(ox, oy, 2f, 45f);
                Draw.mixcol(Draw.getColor(), 1f);
                Draw.color();
                Draw.rect(arrowRegion, x, y, rel);
                Draw.mixcol();
            }
        }

        public void drawConfigure(){
            Drawf.select(this.x, this.y, (float) (this.tile.block().size * 8) / 2.0F + 2.0F, Pal.accent);
            Drawf.dashCircle(x, y, (range) * 8f, Pal.accent);
            Draw.color();
            if(!canReLink() && !canLinked() && realConnections() >= maxConnections - 1) return;
            OrderedMap<Building, Boolean> orderedMap = new OrderedMap<>();
            for(int x = -range; x <= range; ++x){
                for(int y = -range; y <= range; ++y){
                    Tile other = this.tile.nearby(x, y);
                    if (linkValid(this.tile, other) && !(tile == other)) {
                        if(!orderedMap.containsKey(other.build)) orderedMap.put(other.build, false);
                    }
                }
            }
            Building linkBuilding = Vars.world.build(link);
            if(linkBuilding != null){
                configure(linkBuilding.pos());
                orderedMap.remove(linkBuilding);
                orderedMap.put(linkBuilding, true);
            }else{
                configure(-1);
            }
            if(orderedMap.containsKey(this)) orderedMap.remove(this);
            orderedMap.each((other, linked) ->
                    Drawf.select(other.x, other.y, (float) (other.block().size * 8) / 2f + 2f + (linked ? 0f : Mathf.absin(Time.time, 4f, 1f)), linked ? Pal.place : Pal.breakInvalid)
            );
        }

        public void write(Writes write) {
            super.write(write);
            buffer.write(write);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            buffer.read(read);
        }
    }
}