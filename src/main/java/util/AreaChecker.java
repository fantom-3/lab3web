package util;

public final class AreaChecker {

    private AreaChecker() {}

    public static boolean isHit(double x, double y, double r) {
        if (!isFinite(x) || !isFinite(y) || !isFinite(r)) return false;
        if (r <= 0) return false;

        return inCircleQuarter(x, y, r) || inTriangle(x, y, r) || inRectangle(x, y, r);
    }

    private static boolean inCircleQuarter(double x, double y, double r) {
        if (x > 0 || y < 0) return false;
        double radius = r / 2.0;
        return (x * x + y * y) <= (radius * radius);
    }

    private static boolean inTriangle(double x, double y, double r) {
        if (x < 0 || y < 0) return false;
        // y <= -x/2 + R/2
        return y <= (-x / 2.0 + r / 2.0);
    }

    private static boolean inRectangle(double x, double y, double r) {
        return x >= 0 && x <= r && y <= 0 && y >= -r;
    }

    private static boolean isFinite(double v) {
        return !Double.isNaN(v) && !Double.isInfinite(v);
    }
}