package cc.catgasm.world.generator;

import cc.catgasm.util.FastNoise;
import cc.catgasm.world.biome.*;

import java.util.ArrayList;
import java.util.List;

public class BiomeGenerator {
    private final FastNoise noise;
    private int seed;
    private final List<Biome> biomes;

    public BiomeGenerator(int seed) {
        this.seed = seed;
        biomes = new ArrayList<>();
        noise = new FastNoise();
        noise.SetSeed(seed);
        noise.SetNoiseType(FastNoise.NoiseType.Cellular);
        noise.SetFrequency(0.005f);
        noise.SetCellularReturnType(FastNoise.CellularReturnType.CellValue);

        setupBiomes();
    }

    private void setupBiomes() {
        biomes.add(new HillsBiome(seed));
        biomes.add(new ForestBiome(seed));
        biomes.add(new DesertBiome(seed));
        biomes.add(new OceanBiome(seed));
    }

    //TODO cellular noise
    public Biome getBiome(int x, int z) {
        float n = (noise.GetNoise(x, z) + 1) / 2f; //zahl zwischen 0 und 1;
        n *= 16384;

        if(n > 14000) {
            return biomes.get(0);
        } else if(n > 8000) {
            return biomes.get(1);
        } else if(n > 3000) {
            return biomes.get(2);
        } else {
            return biomes.get(3);
        }
    }

    public void setSeed(int seed) {
        this.seed = seed;
        noise.SetSeed(seed);
        for(Biome b : biomes) {
            b.setSeed(seed);
        }
    }
}
