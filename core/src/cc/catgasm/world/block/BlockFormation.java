package cc.catgasm.world.block;

import cc.catgasm.util.SimpleVector2;
import cc.catgasm.util.SimpleVector3;
import cc.catgasm.world.World;

import java.util.LinkedList;
import java.util.List;

/*
 * Die Klasse beschreibt eine Sammlung an BlöckenTypen im 3D-Koordinatensystem mit relativen Positionen
 */
@SuppressWarnings("unused")
public class BlockFormation {
    public enum MergeMode {
        ADD, SUBTRACT, XOR
    }

    private final int[][][] formation;
    private MergeMode mergeMode;
    private final SimpleVector2 posAdjustment;

    public BlockFormation(int[][][] formation, MergeMode mergeMode) {
        this.formation = formation;
        this.mergeMode = mergeMode;
        posAdjustment = new SimpleVector2();
    }

    public BlockFormation(int[][][] formation, MergeMode mergeMode, SimpleVector2 posAdjustment) {
        this.formation = formation;
        this.mergeMode = mergeMode;
        this.posAdjustment = posAdjustment;
    }

    public int[][][] getFormation() {
        return formation;
    }

    public MergeMode getMergeMode() {
        return mergeMode;
    }

    public void setMergeMode(MergeMode mergeMode) {
        this.mergeMode = mergeMode;
    }

    public SimpleVector2 getPosAdjustment() {
        return posAdjustment;
    }

    /*
     * Konvertiert die relative Blockformation in eine Absolute anhand der übergebenen basis Position
     */
    public List<Block> getBlocks(int x, int y ,int z) {
        List<Block> blocks = new LinkedList<>();

        for (int i = 0; i < formation.length; i++) {
            for (int j = 0; j < formation[i].length; j++) {
                for (int k = 0; k < formation[i][j].length; k++) {
                    int blockType = formation[i][j][k];
                    if (blockType != 0) {
                        blocks.add(new Block(i + x, j + y, k + z, blockType));
                    }
                }
            }
        }

        return blocks;
    }

    public List<Block> getBlocks(SimpleVector3 pos) {
        return getBlocks(pos.x,pos.y,pos.z);
    }

    public void placeAt(int x, int y, int z){
        World w = World.getInstance();
        List<Block> blocks = getBlocks(x,y,z);
        w.setBlocks(blocks);
    }
}
