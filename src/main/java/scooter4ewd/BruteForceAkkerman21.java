package scooter4ewd;

/**
 * Подбор оптимальной геометрии рулевой системы.
 * Поворотные кулаки направлены назад.
 * Рулевая сошка на шарнире соеденена с двумя тягами идущими к поворотным кулакам.
 * Точка крепления правой и левой тяги на сошке совпадает.
 * <p>Условные обозначения
 * <ul>
 *     <li>L1 - точка крепления левого рычага</li>
 *     <li>C1 - точка крепления рулевой сошки</li>
 *     <li>R1 - точка крепления правого рычага</li>
 *     <li>L2 - центр вращения левого колеса</li>
 *     <li>C2 - центр передней оси, совпадает с осью вращения руля</li>
 *     <li>R2 - центр вращения правого колеса</li>
 *     <li>C3 - центр задней оси</li>
 * </p>
 * <p>
 * <blockquote><pre>
 *     L2--------C2--------R2
 *     |         |         |
 *     L1--------C1--------R1
 *
 *
 *
 *
 *
 *
 *               C3
 * </blockquote></pre>
 * TODO перенести в трехмерное пространство - ось вращения руля не совпадает с осью вращения колес
 * </p>
 * <p>Конструкция упирается в ограничение выноса поворотного кулака в стороны 20 мм, больше нельзя будет тереться о колесо.</p>
 * <p>Результат:</p>
 * <p>Вынос поворотного кулака назад:55.0 мм (относительно центра вращения колеса)</p>
 * <p>Вынос поворотного кулака во внутрь:11.0 мм (относительно центра вращения колеса к центру)</p>
 * <p>Длина рулевой сошки:50.0</p>
 * <p>Погрешность:231.52892121140263</p>
 * <p>Time:16699ms</p>
 */
public class BruteForceAkkerman21 {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        double min = Integer.MAX_VALUE;
        double min_r1r2_y = 0;
        double min_c1c2 = 0;
        double min_r1r2_x = 0;
        // Перебор выноса поворотного кулака во внутрь
        for (double r1r2_x = 0; r1r2_x <= 50; r1r2_x += 1) {
            // Перебор выноса поворотного кулака назад
            for (double r1r2_y = 50; r1r2_y <= 100; r1r2_y += 1) {
                // Перебор длины рулевой сошки
                for (double c1c2 = 50; c1c2 <= 150; c1c2 += 1) {
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
        time = System.currentTimeMillis() - time;
        System.out.println("Вынос поворотного кулака назад:" + min_r1r2_y + " мм (относительно центра вращения колеса)");
        System.out.println("Вынос поворотного кулака во внутрь:" + min_r1r2_x + " мм (относительно центра вращения колеса к центру)");
        System.out.println("Длина рулевой сошки:" + min_c1c2);
        System.out.println("Погрешность:" + min);
        System.out.println("Time:" + time + "ms");
    }

    private static double test(
        double r1r2_y,
        double c1c2,
        double r1r2_x
    ) {
        final double c2r2 = BruteForceConst.L2_R2 / 2;
        // Длина рычага
        final double c1r1 = Trigonometry.length(c2r2 - r1r2_x, c1c2 - r1r2_y);
        // Длина поворотного кулака
        final double r1r2 = Trigonometry.length(r1r2_x, r1r2_y);
        // Угол поворотного кулака
        final double a11 = StrictMath.toDegrees(StrictMath.atan2(r1r2_x, r1r2_y));

        double e = 0;
        // Подсчет ошибки для углов правого колеса
        for (double r_a = BruteForceConst.MIN_ANGLE; r_a <= BruteForceConst.MAX_ANGLE; r_a += BruteForceConst.STEP_ANGLE) {
            // Идеальное значение угла левого колеса (линии перпендикулярные передним колесам пересекаются и точка пересечения лежит на линии задней оси)
            double l_a = BruteForceConst.getLeftAngle(r_a);
            // расчет угла левого колеса, согласно указанных параметров кулаков и сошки
            double r1_x = -r1r2_x * StrictMath.cos(StrictMath.toRadians(r_a)) - r1r2_y * StrictMath.sin(StrictMath.toRadians(r_a));
            double r1_y = r1r2_x * StrictMath.sin(StrictMath.toRadians(r_a)) - r1r2_y * StrictMath.cos(StrictMath.toRadians(r_a));
            double c2r1 = Trigonometry.length(c2r2 + r1_x, r1_y);
            double c1c2r1 = Trigonometry.angle(c1c2, c2r1, c1r1);
            double r1c2r2 = Trigonometry.angle(c2r1, c2r2, r1r2);
            double l2c2c1 = 180 - c1c2r1 - r1c2r2;
            double l2c1 = Trigonometry.length(c2r2, c1c2, l2c2c1);
            double l1l2c1 = Trigonometry.angle(r1r2, l2c1, c1r1);
            double c1l1c2 = Trigonometry.angle(l2c1, c2r2, c1c2);

            double l_a_fact = l1l2c1 + c1l1c2 + a11 - 90;
            e += StrictMath.pow(l_a - l_a_fact, 2);
        }
        return e;
    }
}
