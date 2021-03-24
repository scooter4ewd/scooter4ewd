package scooter4ewd;

public class Trigonometry {
    /**
     * Найти угол треугольника у которого известны длины сторон.
     *
     * @param ab ребро
     * @param ac ребро
     * @param bc ребро
     * @return значение угла bac
     */
    public static double angle(double ab, double ac, double bc) {
        return StrictMath.toDegrees(StrictMath.acos((ab * ab + ac * ac - bc * bc) / (2 * ab * ac)));
    }

    /**
     * Найти третью сторону треугольника, зная две стороны и угол между ними
     *
     * @param ab  ребро
     * @param ac  ребро
     * @param bac угол между ребрами
     * @return длина третьей стороны bc
     */
    public static double length(double ab, double ac, double bac) {
        return StrictMath.sqrt(ab * ab + ac * ac - 2 * ab * ac * StrictMath.cos(StrictMath.toRadians(bac)));
    }

    /**
     * Найти гипотенузу прямоугольного треугольника, зная два катета
     *
     * @param ab ребро
     * @param ac ребро
     * @return длина гипотенузы bc
     */
    public static double length(double ab, double ac) {
        return StrictMath.sqrt(ab * ab + ac * ac);
    }
}
