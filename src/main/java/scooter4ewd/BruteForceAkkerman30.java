package scooter4ewd;

/**
 * Подбор оптимальной геометрии рулевой системы.
 * <p>Условные обозначения
 * <ul>
 *     <li>L1 - точка крепления левого рычага к поворотному кулаку</li>
 *     <li>CL1 - точка крепления левого рычага к рулевой сошки</li>
 *     <li>CR1 - точка крепления правого рычага к рулевой сошки</li>
 *     <li>R1 - точка крепления правого рычага к поворотному кулаку</li>
 *     <li>L2 - центр вращения левого колеса</li>
 *     <li>C2 - центр передней оси, совпадает с осью вращения руля</li>
 *     <li>R2 - центр вращения правого колеса</li>
 *     <li>C3 - центр задней оси</li>
 * </p>
 * <p>
 * <blockquote><pre>
 *     L1-----CL1-С1-CR1-----R1
 *     |          |          |
 *     L2---------C2---------R2
 *
 *
 *
 *
 *
 *
 *                C3
 * </blockquote></pre>
 * TODO перенести в трехмерное пространство - ось вращения руля не совпадает с осью вращения колес
 * </p>
 * <p>Результат:</p>
 * <p>Вынос поворотного кулака вперед:55.0 мм (относительно центра вращения колеса)</p>
 * <p>Вынос поворотного кулака в стороны:20.0 мм (относительно центра вращения колеса в ширь)</p>
 * <p>Длина рулевой сошки:93.0</p>
 * <p>Ширина рулевой сошки:0.0</p>
 * <p>Погрешность:108.70467597872509</p>
 * <p>Time:353892ms</p>
 * <p>Вывод:</p>
 * <p>Решение сводится к BruteForceAkkerman20</p>
 */
public class BruteForceAkkerman30 {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        double min = Integer.MAX_VALUE;
        double min_r1r2_y = 0;
        double min_c1c2 = 0;
        double min_cl1cr1 = 0;
        double min_r1r2_x = 0;
        // Перебор выноса поворотного кулака в сторону
        for (double r1r2_x = 0; r1r2_x <= 20; r1r2_x += 1) {
            // Перебор выноса поворотного кулака вперед
            for (double r1r2_y = 50; r1r2_y <= 100; r1r2_y += 1) {
                // Перебор длины рулевой сошки
                for (double c1c2 = 50; c1c2 <= 130; c1c2 += 1) {
                    // Перебор ширины рулевой сошки
                    for (double cl1cr1 = 0; cl1cr1 <= 50; cl1cr1+=1){
                        double e = test(r1r2_y, c1c2, cl1cr1, r1r2_x);
                        if (e < min) {
                            min = e;
                            min_r1r2_y = r1r2_y;
                            min_c1c2 = c1c2;
                            min_cl1cr1 = cl1cr1;
                            min_r1r2_x = r1r2_x;
                        }
                    }
                }
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Вынос поворотного кулака вперед:" + min_r1r2_y + " мм (относительно центра вращения колеса)");
        System.out.println("Вынос поворотного кулака в стороны:" + min_r1r2_x + " мм (относительно центра вращения колеса в ширь)");
        System.out.println("Длина рулевой сошки:" + min_c1c2);
        System.out.println("Ширина рулевой сошки:" + min_cl1cr1);
        System.out.println("Погрешность:" + min);
        System.out.println("Time:" + time + "ms");
    }

    /**
     * Получить погрешность поворота колес в зависимости от указанных параметров
     * @param r1r2_y вынос поворотного кулака вперед
     * @param c1c2 длина рулевой сошки
     * @param cl1cr1 ширина рулевой сошки
     * @param r1r2_x вынос поворотного кулака в сторону
     * @return погрешность
     */
    private static double test(
        double r1r2_y,
        double c1c2,
        double cl1cr1,
        double r1r2_x
    ) {
        final double c2r2 = BruteForceConst.L2_R2 / 2;
        // Длина рычага
        final double cr1r1 = Trigonometry.length(c2r2 + r1r2_x - cl1cr1/2, c1c2 - r1r2_y);
        // Длина сошки с учетом смешения
        final double cr1c2 = Trigonometry.length(cl1cr1/2, c1c2);
        // Угловая ширина сошки
        final double cl1c2cr1 = StrictMath.toDegrees(StrictMath.atan2(cl1cr1/2, c1c2))*2;
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
            double r1_x = r1r2_x * StrictMath.cos(StrictMath.toRadians(r_a)) + r1r2_y * StrictMath.sin(StrictMath.toRadians(r_a));
            double r1_y = -r1r2_x * StrictMath.sin(StrictMath.toRadians(r_a)) + r1r2_y * StrictMath.cos(StrictMath.toRadians(r_a));
            double c2r1 = Trigonometry.length(c2r2 + r1_x, r1_y);
            double r1c2r2 = Trigonometry.angle(c2r1, c2r2, r1r2);
            double cr1c2r1 = Trigonometry.angle(cr1c2, c2r1, cr1r1);
            double l2c2cl1 = 180 - cl1c2cr1 - cr1c2r1 - r1c2r2;
            double l2cl1 = Trigonometry.length(c2r2, cr1c2, l2c2cl1);
            double l1l2cl1 = Trigonometry.angle(r1r2, l2cl1, cr1r1);
            double cl1l1c2 = Trigonometry.angle(l2cl1, c2r2, cr1c2);

            double l_a_fact = 90 - l1l2cl1 - cl1l1c2 + a11;
            e += StrictMath.pow(l_a - l_a_fact, 2);
        }
        return e;
    }
}
