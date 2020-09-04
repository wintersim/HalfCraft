package cc.catgasm.util;

public class MathHelper {
    //https://stackoverflow.com/questions/55205437/whats-a-method-that-works-exactly-like-math-floormod-but-with-floats-instead
    public static double floatMod(double x, double y){
        // x mod y behaving the same way as Math.floorMod but with doubles
        return (x - Math.floor(x/y) * y);
    }

    /*
     * Passt die Math.floorDiv() Methode so an, dass alle negativen Zahlen um 1 verschoben sind
     */
    public static int floatDiv(float x, int y){
        if(x < 0) x--;
        return Math.floorDiv((int)x,y);
    }
}
