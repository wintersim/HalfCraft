package cc.catgasm.util;

@SuppressWarnings("unused")
public class SimpleVector2 {
    public int x;
    public int y;

    public SimpleVector2() {
    }

    public SimpleVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /*Addiert dx und dy
    * Gibt sich selbst zurück (chaining)
    */
    public SimpleVector2 add(int dx, int dy){
        x += dx;
        y += dy;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleVector2)) return false;

        SimpleVector2 that = (SimpleVector2) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(SimpleVector2 o) {
        x = o.x;
        y = o.y;
    }
}
