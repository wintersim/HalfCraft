package cc.catgasm.world.biome;

import cc.catgasm.util.FastNoise;
import cc.catgasm.world.block.Blocks;

public class DesertBiome extends Biome{
    public DesertBiome(int seed) {
        super(seed);
    }

    @Override
    protected void initNoiseGenerator() {
        noise.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
        noise.SetInterp(FastNoise.Interp.Hermite);
        noise.SetFractalOctaves(8);
    }

    @Override
    public int getSurfaceBlockType(int y) {
        return Blocks.SAND;
    }

    @Override
    public int getSecondLayerBlockType(int y) {
        return Blocks.STONE;
    }

    @Override
    public int getTreeProbability() {
        return 400;
    }

    @Override
    public int getTreeType() {
        return 10;
    }

    @Override
    public int getSurfaceHeightModifier() {
        return 50;
    }

    @Override
    public int getL1Flatness() {
        return 10;
    }
}
