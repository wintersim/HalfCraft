package cc.catgasm.world.block;


import cc.catgasm.util.SimpleVector3;

@SuppressWarnings("unused")
public class Block {

    private SimpleVector3 pos;
    private int type;

    public Block(int x, int y, int z, int type) {
        pos = new SimpleVector3(x, y, z);
        this.type = type;
    }

    public SimpleVector3 getPos() {
        return pos;
    }

    public void setPos(SimpleVector3 pos) {
        this.pos = pos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTransparent() {
        return Blocks.isTransparent(type);
    }

    @Override
    public String toString() {
        return "Block{" +
                "pos=" + pos +
                ", type=" + type +
                '}';
    }
}
