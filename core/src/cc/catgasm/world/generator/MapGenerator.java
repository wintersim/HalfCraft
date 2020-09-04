package cc.catgasm.world.generator;

import cc.catgasm.util.SimpleVector2;
import cc.catgasm.world.biome.Biome;
import cc.catgasm.world.block.Block;
import cc.catgasm.world.block.BlockFormation;
import cc.catgasm.world.block.Blocks;
import it.unimi.dsi.util.XoRoShiRo128PlusPlusRandom;

import static cc.catgasm.world.chunk.Chunk.CHUNK_HEIGHT;
import static cc.catgasm.world.chunk.Chunk.CHUNK_SIZE;

public class MapGenerator {
    private int seed;
    private final BiomeGenerator biomeGenerator;
    private final XoRoShiRo128PlusPlusRandom random;

    private static final int WATER_HEIGHT = 58;

    private final TreeGenerator treeGen;

    public MapGenerator(int seed) {
        biomeGenerator = new BiomeGenerator(seed);
        random = new XoRoShiRo128PlusPlusRandom();
        treeGen = new TreeGenerator(random);
    }

    public void setSeed(int seed) {
        this.seed = seed;
        biomeGenerator.setSeed(seed);
        random.setSeed(seed);
    }

    public int getSeed() {
        return seed;
    }

    private Biome currentBiome;
    private final int[] interp = new int[25];

    /*
     * Generiert einen Chunk
     * Intern werden die Blöcke jeweils mit 0 < x,z < 16 und 0 < y < 128 abgespeichert
     * Die realen koordinaten müssen anhand des jeweiligen Chunks berechnet werden
     * bsp. realX = chunkX * CHUNK_SIZE + chunk_intern
     */
    public Block[][][] generateChunk(int chunkX, int chunkZ) {
        Block[][][] blocks = new Block[CHUNK_SIZE][CHUNK_HEIGHT][CHUNK_SIZE];
        int y;

        //Real-World Chunk offset
        int blockX = chunkX * CHUNK_SIZE;
        int blockZ = chunkZ * CHUNK_SIZE;
        int realX, realZ;

        boolean placedWater;

        //Generiere Oberfläche
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                placedWater = false;

                //Koordinate des Blocks in World-Space
                realX = x + blockX;
                realZ = z + blockZ;

                //Abflachung
                y = smooth(realX, realZ);

                //Zweite Schicht 2 Blöcke hoch
                int tmp = currentBiome.getSurfaceBlockType(y);
                if(tmp != 0)
                    blocks[x][y][z] = new Block(realX, y, realZ, tmp);
                tmp = currentBiome.getSecondLayerBlockType(y - 1);

                if(tmp != 0)
                    blocks[x][y - 1][z] = new Block(realX, y - 1, realZ, currentBiome.getSecondLayerBlockType(y - 1));
                tmp = currentBiome.getSecondLayerBlockType(y - 2);

                if(tmp != 0)
                    blocks[x][y - 2][z] = new Block(realX, y - 2, realZ, currentBiome.getSecondLayerBlockType(y - 2));

                //Alle Blöcke nach unten ausfüllen
                for (int height = y - 3; height >= 0; height--) {
                    blocks[x][height][z] = new Block(realX, height, realZ, Blocks.STONE);
                }

                //Wasser
                int tmpY = WATER_HEIGHT;
                while (blocks[x][tmpY][z] == null) {
                    blocks[x][tmpY][z] = new Block(realX, tmpY, realZ, Blocks.WATER);
                    tmpY--;
                    placedWater = true;
                }

                //Bäume
                long rnd = random.nextLong();
                if(rnd % currentBiome.getTreeProbability() == 0 && !placedWater){
                    BlockFormation bf = treeGen.getTree(currentBiome.getTreeType());
                    if(bf != null) {
                        int[][][] treeFormation = bf.getFormation();
                        SimpleVector2 adj = bf.getPosAdjustment();
                        placeFormation(treeFormation, blocks, realX, y, realZ, x, z, adj.x, adj.y);
                    }
                }
            }
        }

        return blocks;
    }

    private int smooth(int realX, int realZ) {
        interp[0] = getHeight(realX, realZ) / currentBiome.getL1Flatness();
        interp[1] = getHeight(realX + 1, realZ) / currentBiome.getL2Flatness();
        interp[3] = getHeight(realX, realZ + 1) / currentBiome.getL2Flatness();
        interp[5] = getHeight(realX - 1, realZ) / currentBiome.getL2Flatness();
        interp[7] = getHeight(realX, realZ - 1) / currentBiome.getL2Flatness();

        interp[2] = getHeight(realX + 1, realZ + 1) / currentBiome.getL2Flatness();
        interp[4] = getHeight(realX - 1, realZ + 1) / currentBiome.getL2Flatness();
        interp[6] = getHeight(realX - 1, realZ - 1) / currentBiome.getL2Flatness();
        interp[8] = getHeight(realX + 1, realZ - 1) / currentBiome.getL2Flatness();

        interp[9] = getHeight(realX + 2, realZ + 2) / currentBiome.getL3Flatness();
        interp[10] = getHeight(realX + 2, realZ + 1) / currentBiome.getL3Flatness();
        interp[11] = getHeight(realX + 2, realZ) / currentBiome.getL3Flatness();
        interp[12] = getHeight(realX + 2, realZ - 1) / currentBiome.getL3Flatness();
        interp[13] = getHeight(realX + 2, realZ - 2) / currentBiome.getL3Flatness();
        interp[14] = getHeight(realX + 1, realZ - 2) / currentBiome.getL3Flatness();
        interp[15] = getHeight(realX, realZ - 2) / currentBiome.getL3Flatness();
        interp[16] = getHeight(realX - 1, realZ - 2) / currentBiome.getL3Flatness();
        interp[17] = getHeight(realX - 2, realZ - 2) / currentBiome.getL3Flatness();
        interp[18] = getHeight(realX - 2, realZ - 1) / currentBiome.getL3Flatness();
        interp[19] = getHeight(realX - 2, realZ) / currentBiome.getL3Flatness();
        interp[20] = getHeight(realX - 2, realZ + 1) / currentBiome.getL3Flatness();
        interp[21] = getHeight(realX - 2, realZ + 2) / currentBiome.getL3Flatness();
        interp[22] = getHeight(realX - 1, realZ + 2) / currentBiome.getL3Flatness();
        interp[23] = getHeight(realX, realZ + 2) / currentBiome.getL3Flatness();
        interp[24] = getHeight(realX + 1, realZ + 2) / currentBiome.getL3Flatness();

        int y = interp[0] + interp[1] + interp[2] +
                interp[3] + interp[4] + interp[5] +
                interp[6] + interp[7] + interp[8] +
                interp[9] + interp[10] + interp[11] +
                interp[12] + interp[13] + interp[14] +
                interp[15] + interp[16] + interp[17] +
                interp[18] + interp[19] + interp[20] +
                interp[21] + interp[22] + interp[23] +
                interp[24];

        return y / 25;
    }

    private int getHeight(int realX, int realZ) {
        currentBiome = biomeGenerator.getBiome(realX, realZ);
        return (int) ((currentBiome.getNoise(realX, realZ) + 1) * 20) + currentBiome.getSurfaceHeightModifier();
    }

    private void placeFormation(int[][][] formation, Block[][][] blocks, int currentBlockX, int currentBlockY, int currentBlockZ, int currentInternalX, int currentInternalZ, int dx, int dz) {

        //Formation darf nicht über chunk hinaus
        if (formation.length + currentInternalX > CHUNK_SIZE
                || formation[0].length + currentBlockY > CHUNK_HEIGHT
                || formation[0][0].length + currentInternalZ > CHUNK_SIZE)
            return;

        //Verschiebung nach dx, dz
        if (currentInternalX + dx < 0 || currentInternalZ + dz < 0)
            return;

        for (int i = 0; i < formation.length; i++) {
            for (int j = 0; j < formation[i].length; j++) {
                for (int k = 0; k < formation[i][j].length; k++) {
                    int type = formation[i][j][k];
                    if(type != 0)
                        blocks[currentInternalX + i + dx][currentBlockY + j + 1][currentInternalZ + k + dz] = new Block(currentBlockX + i + dx, currentBlockY + j + 1, currentBlockZ + k + dz, type);
                }
            }
        }
    }
}
