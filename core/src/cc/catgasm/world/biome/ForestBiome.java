package cc.catgasm.world.biome;

import cc.catgasm.util.FastNoise;
import cc.catgasm.world.block.Blocks;

public class ForestBiome extends Biome{
    private FastNoise extendedNoise;


    public ForestBiome(int seed) {
        super(seed);
    }

    @Override
    protected void initNoiseGenerator() {
        noise.SetNoiseType(FastNoise.NoiseType.SimplexFractal);
        noise.SetInterp(FastNoise.Interp.Hermite);
        noise.SetFractalOctaves(5);

        extendedNoise = new FastNoise();
        extendedNoise.SetSeed(noise.GetSeed());
        extendedNoise.SetFrequency(0.02f);
        extendedNoise.SetNoiseType(FastNoise.NoiseType.Perlin);
        extendedNoise.SetInterp(FastNoise.Interp.Hermite);
        extendedNoise.SetFractalOctaves(5);
    }

    @Override
    public int getSurfaceBlockType(int y) {
        return Blocks.GRASS;
    }

    @Override
    public int getSecondLayerBlockType(int y) {
        return Blocks.DIRT;
    }

    @Override
    public int getTreeProbability() {
        return 80;
    }

    @Override
    public int getTreeType() {
        return 1;
    }

    @Override
    public int getSurfaceHeightModifier() {
        return 46;
    }

    @Override
    public int getL1Flatness() {
        return 5;
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
    public void setSeed(int seed) {
        super.setSeed(seed);
        extendedNoise.SetSeed(seed);
    }

    @Override
    public float getNoise(int x, int y) {
        return (super.getNoise(x,y) + extendedNoise.GetNoise(x,y) * 0.6f) / 1.2f;
    }
}
