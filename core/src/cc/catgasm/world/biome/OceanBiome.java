package cc.catgasm.world.biome;

import cc.catgasm.util.FastNoise;
import cc.catgasm.world.block.Blocks;

public class OceanBiome extends Biome{
    public OceanBiome(int seed) {
        super(seed);
    }

    @Override
    protected void initNoiseGenerator() {
        noise.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
        noise.SetInterp(FastNoise.Interp.Quintic);
        noise.SetFractalOctaves(2);
    }

    @Override
    public int getSurfaceBlockType(int y) {
        return Blocks.STONE;
    }

    @Override
    public int getSecondLayerBlockType(int y) {
        return Blocks.STONE;
    }

    @Override
    public int getTreeProbability() {
        return -1;
    }

    @Override
    public int getSurfaceHeightModifier() {
        return 35;
    }


    @Override
    public int getL1Flatness() {
        return 10;
    }

    @Override
    public float getNoise(int x, int y) {
        return super.getNoise(x, y) * 0.3f;
    }
}
