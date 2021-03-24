package scooter4ewd;

import java.util.HashMap;
import java.util.Map;

public class BruteForceConst {
    /**
     * Ширина передней оси (расстояние между центрами вращения)
     */
    public static final double L2_R2 = 448;
    /**
     * База (расстояние между передней и задней осью)
     */
    public static final double C2_C3 = 995;
    public static final double MIN_ANGLE = 20;
    public static final double MAX_ANGLE = 70;
    public static final double STEP_ANGLE = 1;


    private static Map<Double, Double> cache = new HashMap<>();

    /**
     * Идеальное значение угла левого колеса (линии перпендикулярные передним колесам пересекаются и точка пересечения лежит на линии задней оси)
     *
     * @param r_a угол правого колеса
     * @return угол левого колеса
     */
    public static double getLeftAngle(double r_a) {
        Double l_a = cache.get(r_a);
        if (l_a == null) {
            l_a = StrictMath.toDegrees(StrictMath.atan2(BruteForceConst.C2_C3, BruteForceConst.L2_R2 + BruteForceConst.C2_C3 * StrictMath.tan(StrictMath.toRadians(90 - r_a))));
            cache.put(r_a, l_a);
        }
        return l_a;
    }
}
