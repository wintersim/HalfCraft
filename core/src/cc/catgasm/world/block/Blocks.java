package cc.catgasm.world.block;

public class Blocks {
    public static class Orientation {


        private static final float[][][] FACES = new float[][][] {
                {{0, 1f, 0}, {1f, 1f, 0}, {1f, 0, 0}, {0, 0, 0}}, //N
                {{1f, 1f, 1f}, {0, 1f, 1f}, {0, 0, 1f}, {1f, 0, 1f}}, //S
                {{1f, 1f, 0}, {1f, 1f, 1f}, {1f, 0, 1f}, {1f, 0, 0}}, //E
                {{0, 1f, 1f}, {0, 1f, 0}, {0, 0, 0}, {0, 0, 1f}}, //W
                {{0, 1f, 0}, {0, 1f, 1f}, {1f, 1f, 1f}, {1f, 1f, 0}}, //U
                {{0, 0, 1f}, {0, 0, 0}, {1f, 0, 0}, {1f, 0, 1f}}, //D
        };

        private static final float[][] LIQUID_VERTS = {
                {0, 0.85f, 0}, {0, 0.85f, 1f}, {1f, 0.85f, 1f}, {1f, 0.85f, 0}
        };

        private static final float[][][] FACES_CENTER_0 = new float[][][] {
                {{0.5f, 0.5f, -0.5f}, {0.5f, -0.5f, -0.5f}, {-0.5f, -0.5f, -0.5f}, {-0.5f, 0.5f, -0.5f}}, //N
                {{-0.5f, 0.5f, 0.5f}, {-0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, 0.5f}, {0.5f, 0.5f, 0.5f}}, //S
                {{0.5f, 0.5f, -0.5f}, {0.5f, 0.5f, 0.5f}, {0.5f, -0.5f, 0.5f}, {0.5f, -0.5f, -0.5f}}, //E
                {{-0.5f, 0.5f, -0.5f}, {-0.5f, -0.5f, -0.5f}, {-0.5f, -0.5f, 0.5f}, {-0.5f, 0.5f, 0.5f}}, //W
                {{-0.5f, 0.5f, -0.5f}, {-0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, 0.5f}, {0.5f, 0.5f, -0.5f}}, //U
                {{-0.5f, -0.5f, 0.5f}, {-0.5f, -0.5f, -0.5f}, {0.5f, -0.5f, -0.5f}, {0.5f, -0.5f, 0.5f}}, //D
        };

        private static final float[][] FACE_NORMALS = new float[][] {
                {1,0,0},
                {-1,0,0},
                {0,0,1},
                {0,0,-1},
                {0,1,0},
                {0,-1,0},
        };

        public static final int NORTH = 0; // -Z
        public static final int SOUTH = 1; //+Z
        public static final int EAST = 2; //+X
        public static final int WEST = 3; //-X
        public static final int UP = 4; //+Y
        public static final int DOWN = 5; //-Y
    }

    /*
     * Returns an array of vertices.
     * A Vertex is a array of 3 floats (xyz)
     */
    public static float[][] getBlockFaceVertices(int side) {
        return Orientation.FACES[side];
    }

    public static float[] getBlockFaceNormals(int face) {
        return Orientation.FACE_NORMALS[face];
    }

    public static float[][] getLiquidUpVertices() {
        return Orientation.LIQUID_VERTS;
    }

    public static float[][][] getBlockVertices() {
        return Orientation.FACES;
    }

    public static final int GRASS = 1;
    public static final int DIRT = 3;
    public static final int STONE = 4;
    public static final int WOOD = 5;
    public static final int SAND = 7;
    public static final int LEAF = 8;
    public static final int CACTUS = 9;
    public static final int WATER = 11;

    public static boolean isTransparent(int block) {
        return block == WATER;
    }

    public static boolean hasUpTexture(int block) {
        return block == GRASS || block == WOOD || block == CACTUS;
    }

    public static boolean hasDownTexture(int block) {
        return block == GRASS;
    }
}
