package cc.catgasm.world.biome;

import cc.catgasm.util.FastNoise;
import cc.catgasm.world.block.Blocks;

public class HillsBiome extends Biome {
    public HillsBiome(int seed) {
        super(seed);
    }

    @Override
    protected void initNoiseGenerator() {
        noise.SetNoiseType(FastNoise.NoiseType.Simplex);
        noise.SetInterp(FastNoise.Interp.Linear);
        noise.SetFrequency(0.023f);
    }

    @Override
    public int getSurfaceBlockType(int y) {
        return y < 70 ? Blocks.GRASS : Blocks.STONE;
    }

    @Override
    public int getSecondLayerBlockType(int y) {
        return y < 70 ? Blocks.DIRT : Blocks.STONE;
    }

    @Override
    public int getTreeProbability() {
        return 190;
    }

    @Override
    public int getTreeType() {
        return 1;
    }

    @Override
    public int getSurfaceHeightModifier() {
        return 45;
    }

    @Override
    public int getL1Flatness() {
        return 4;
    }

    @Override
    public int getL2Flatness() {
        return 1;
    }

    @Override
    public int getL3Flatness() {
        return 1;
    }

    @Override
    public float getNoise(int x, int y) {
        return super.getNoise(x, y) / 1.4f + 0.6f;
    }
}
