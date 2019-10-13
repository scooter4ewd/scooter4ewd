package scooter4ewd;

/**
 * Подбор оптимальной геометрии рулевой системы
 * Поворотные кулаки направлены назад.
 * Поворотные кулаки соединены цельной рейкой.
 * Рулевая сошка скользит по петле на рейке.
 * <p>Условные обозначения
 * <ul>
 *     <li>L1 - точка крепления левого рычага</li>
 *     <li>R1 - точка крепления правого рычага</li>
 *     <li>L2 - центр вращения левого колеса</li>
 *     <li>C2 - центр передней оси, совпадает с осью вращения руля</li>
 *     <li>R2 - центр вращения правого колеса</li>
 *     <li>C3 - центр задней оси</li>
 * </ul>
 * </p>
 * <p>Конструкция должна была исбавить от ограничений, в которые упирается BruteForceAkkerman10, но
 * по факту требует сделать очень короткий поворотный кулак ~ 14 мм, что невозможно и при этом погрешность больше чем у
 * BruteForceAkkerman10 (207 против 153, чем меньше погрешность, тем лучше)</p>
 * <p>Результат:</p>
 * <p>Вынос поворотного кулака назад:14.0 мм (относительно центра вращения колеса)</p>
 * <p>Вынос поворотного кулака во внутрь:3.0 мм (относительно центра вращения колеса к центру)</p>
 * <p>Погрешность:207.57431686149474</p>
 */
public class BruteForceAkkerman11 {

    /**
     * Ширина передней оси (расстояние между центрами вращения)
     */
    private static final double l2r2 = 448;
    /**
     * База (расстояние между передней и задней осью)
     */
    private static final double c2c3 = 995;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        double min = Integer.MAX_VALUE;
        double min_r1r2_y = 0;
        double min_r1r2_x = 0;
        // Перебор выноса поворотного кулака во внутрь
        for (double r1r2_x = 0; r1r2_x <= 50; r1r2_x += 1) {
            // Перебор выноса поворотного кулака назад
            for (double r1r2_y = 10; r1r2_y <= 150; r1r2_y += 1) {
                double e = test(r1r2_y, r1r2_x);
                if (e < min) {
                    min = e;
                    min_r1r2_y = r1r2_y;
                    min_r1r2_x = r1r2_x;
                }
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Вынос поворотного кулака назад:" + min_r1r2_y + " мм (относительно центра вращения колеса)");
        System.out.println("Вынос поворотного кулака во внутрь:" + min_r1r2_x + " мм (относительно центра вращения колеса к центру)");
        System.out.println("Погрешность:" + min);
        System.out.println("Time:" + time + "ms");
    }

    private static double test(
        double r1r2_y,
        double r1r2_x
    ) {
        // Длина рейки
        final double l1r1 = l2r2 - 2 * r1r2_x;
        // Длина поворотного кулака
        final double r1r2 = Trigonometry.length(r1r2_x, r1r2_y);
        // Угол поворотного кулака
        final double a11 = StrictMath.toDegrees(StrictMath.atan2(r1r2_x, r1r2_y));

        double e = 0;
        // Подсчет ошибки для углов правого колеса
        for (double r_a = 20; r_a <= 70; r_a += 1) {
            // Идеальное значение угла левого колеса (линии перпендикулярные передним колесам пересекаются и точка пересечения лежит на линии задней оси)
            double l_a = StrictMath.toDegrees(StrictMath.atan2(c2c3, l2r2 + c2c3 * StrictMath.tan(StrictMath.toRadians(90 - r_a))));
            // расчет угла левого колеса, согласно указаных параметров кулаков
            double r1_x = -r1r2_x * StrictMath.cos(StrictMath.toRadians(r_a)) - r1r2_y * StrictMath.sin(StrictMath.toRadians(r_a));
            double r1_y = r1r2_x * StrictMath.sin(StrictMath.toRadians(r_a)) - r1r2_y * StrictMath.cos(StrictMath.toRadians(r_a));
            double l2r1 = Trigonometry.length(l2r2 + r1_x, r1_y);
            double l1l2r1 = Trigonometry.angle(r1r2, l2r1, l1r1);
            double r1l2r2 = Trigonometry.angle(l2r1, l2r2, r1r2);
            double l1l2r2 = l1l2r1 + r1l2r2 + a11 - 90;
            e += StrictMath.pow(l_a - l1l2r2, 2);
        }
        return e;
    }
}
