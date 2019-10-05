package scooter4ewd;

/**
 * Подбор оптимальной геометрии рулевой системы
 * Условные обозначения
 * L1 - точка крепления левого рычага
 * C1 - точка крепления рулевой сошки
 * R1 - точка крепления правого рычага
 * L2 - центр вращения левого колеса
 * C2 - центр передней оси, совпадает с осью вращения руля
 * R2 - центр вращения правого колеса
 * C3 - центр задней оси
 *
 * TODO перенести в трехмерное пространство - ось вращения руля не совпадает с осью вращения колес
 */
public class BruteForceAnkerman {

    /**
     * Ширина передней оси (расстояние между центрами вращения)
     */
    private static final double l2r2 = 448;
    /**
     * База (расстояние между передней и задней осью)
     */
    private static final double c2c3 = 995;

    public static void main(String[] args) {
        double min = Integer.MAX_VALUE;
        double min_r1r2_y = 0;
        double min_c1c2 = 0;
        double min_r1r2_x = 0;
        // Перебор выноса поворотного кулака в сторону
        for (double r1r2_x = 1; r1r2_x <= 20; r1r2_x += 1) {
            // Перебор выноса поворотного кулака вперед
            for (double r1r2_y = 30; r1r2_y <= 80; r1r2_y += 1) {
                // Перебор длины рулевой сошки
                for (double c1c2 = 30; c1c2 <= 130; c1c2 += 1) {
                    double e = test(r1r2_y, c1c2, r1r2_x);
                    if (e < min) {
                        min = e;
                        min_r1r2_y = r1r2_y;
                        min_c1c2 = c1c2;
                        min_r1r2_x = r1r2_x;
                    }
                }
            }
        }
        System.out.println("Вынос поворотного кулака вперед:" + min_r1r2_y + " мм (относительно центра вращения колеса)");
        System.out.println("Вынос поворотного кулака в стороны:" + min_r1r2_x + " мм (относительно центра вращения колеса в ширь)");
        System.out.println("Длина рулевой сошки:" + min_c1c2);
    }

    private static double test(
        double r1r2_y,
        double c1c2,
        double r1r2_x
    ) {
        final double c2r2 = l2r2 / 2;
        // Длина рычага
        final double c1r1 = Math.sqrt(Math.pow(c2r2 + r1r2_x, 2) + Math.pow(c1c2 - r1r2_y, 2));
        // Длина поворотного кулака
        final double r1r2 = Math.sqrt(Math.pow(r1r2_x, 2) + Math.pow(r1r2_y, 2));
        // Угол поворотного кулака
        final double a11 = Math.toDegrees(Math.atan2(r1r2_x, r1r2_y));

        double e = 0;
        // Подсчет ошибки для углов правого колеса
        for (double r_a = 20; r_a <= 70; r_a += 1) {
            // Идеальное значение угла левого колеса (линии перпендикулярные передним колесам пересекаются и точка пересечения лежит на линии задней оси)
            double l_a = Math.toDegrees(Math.atan2(c2c3, l2r2 + c2c3 * Math.tan(Math.toRadians(90 - r_a))));
            // расчет угла левого колеса, согласно указаных параметрок кулаков и сошки
            double r1_x = r1r2_x * Math.cos(Math.toRadians(r_a)) + r1r2_y * Math.sin(Math.toRadians(r_a));
            double r1_y = -r1r2_x * Math.sin(Math.toRadians(r_a)) + r1r2_y * Math.cos(Math.toRadians(r_a));
            double c2r1 = Math.sqrt(Math.pow(c2r2 + r1_x, 2) + Math.pow(r1_y, 2));
            double c1c2r1 = angle(c1c2, c2r1, c1r1);
            double r1c2r2 = angle(c2r1, c2r2, r1r2);
            double l2c2c1 = 180 - c1c2r1 - r1c2r2;
            double l2c1 = length(c2r2, c1c2, l2c2c1);
            double l1l2c1 = angle(r1r2, l2c1, c1r1);
            double c1l1c2 = angle(l2c1, c2r2, c1c2);

            double l1l2c2 = 90 - l1l2c1 - c1l1c2 + a11;
            e += Math.pow(l_a - l1l2c2, 2);
        }
        return e;
    }

    /**
     * Найти угол треугольника у которого известны длины сторон.
     *
     * @param ab ребро
     * @param ac ребро
     * @param bc ребро
     * @return значение угла bac
     */
    private static double angle(double ab, double ac, double bc) {
        return Math.toDegrees(Math.acos((Math.pow(ab, 2) + Math.pow(ac, 2) - Math.pow(bc, 2)) / (2 * ab * ac)));
    }

    /**
     * Найти третью сторону треугольника, зная две строны и угол между ними
     * @param ab ребро
     * @param ac ребро
     * @param bac угол бежду ребрами
     * @return длина третьей стороны bc
     */
    private static double length(double ab, double ac, double bac) {
        return Math.sqrt(Math.pow(ab, 2) + Math.pow(ac, 2) - 2 * ab * ac * Math.cos(Math.toRadians(bac)));
    }

}
