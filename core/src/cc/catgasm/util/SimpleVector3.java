package cc.catgasm.util;

public class SimpleVector3 {
    public int x;
    public int y;
    public int z;

    @SuppressWarnings("unused")
    public SimpleVector3() {
    }

    public SimpleVector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleVector3 that = (SimpleVector3) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "SimpleVector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(SimpleVector3 o) {
        set(o.x, o.y, o.z);
    }
}
