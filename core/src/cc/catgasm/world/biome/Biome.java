package cc.catgasm.world.biome;

import cc.catgasm.util.FastNoise;

public abstract class Biome {
    protected final FastNoise noise;

    public Biome(int seed) {
        noise = new FastNoise();
        noise.SetSeed(seed);

        initNoiseGenerator();
    }

    protected abstract void initNoiseGenerator();

    public abstract int getSurfaceBlockType(int y);

    public abstract int getSecondLayerBlockType(int y);

    /*Größere Zahl --> Kleinere Warscheinlichkeit*/
    public abstract int getTreeProbability();

    public int getTreeType() {
        return -1;
    }

    public abstract int getSurfaceHeightModifier();

    public int getL1Flatness() {
        return 1;
    }

    public int getL2Flatness() {
        return 1;
    }

    public int getL3Flatness() {
        return 1;
    }

    public float getNoise(int x, int y) {
        return noise.GetNoise(x,y);
    }

    public void setSeed(int seed) {
        noise.SetSeed(seed);
    }
}
