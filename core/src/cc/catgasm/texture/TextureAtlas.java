package cc.catgasm.texture;

@SuppressWarnings("unused")
public class TextureAtlas {
    private final int rows;
    private final int cols;

    private final float[][] ret = new float[4][2]; //Caching
    private final float stepU;
    private final float stepV;

    public TextureAtlas(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        stepU = 1f/cols;
        stepV = 1f/rows;
    }

    /*
     * Gibt die UV-Koordinaten im TextureAtlas eines bestimmten Blocks zurück
     */
    public float[][] getUVCoordinate(int index) {
        --index;

        //TODO Spinnt noch etwas
        float tlU = (index * stepU) % (cols / 4f);
        float tlV = ((index / rows) / 4f);


        ret[0][0] = tlU;
        ret[0][1] = tlV;

        ret[1][0] = tlU + stepU;
        ret[1][1] = tlV;

        ret[2][0] = tlU + stepU;
        ret[2][1] = tlV + stepV;

        ret[3][0] = tlU;
        ret[3][1] = tlV + stepV;

        return ret;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
