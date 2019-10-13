package scooter4ewd;

/**
 * Подбор оптимальной геометрии рулевой системы.
 * Поворотные кулаки направлены вперед.
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
 * <p>Конструкция упирается в ограничение выноса поворотного кулака в стороны 20 мм, больше нельзя будет тереться о колесо.</p>
 * <p>Результат:</p>
 * <p>Вынос поворотного кулака вперед:76.0 мм (относительно центра вращения колеса)</p>
 * <p>Вынос поворотного кулака в стороны:20.0 мм (относительно центра вращения колеса в ширь)</p>
 * <p>Погрешность:153.22931097132113</p>
 */
public class BruteForceAkkerman10 {

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
        // Перебор выноса поворотного кулака в сторону
        for (double r1r2_x = 0; r1r2_x <= 20; r1r2_x += 1) {
            // Перебор выноса поворотного кулака вперед
            for (double r1r2_y = 50; r1r2_y <= 150; r1r2_y += 1) {
                double e = test(r1r2_y, r1r2_x);
                if (e < min) {
                    min = e;
                    min_r1r2_y = r1r2_y;
                    min_r1r2_x = r1r2_x;
                }
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("Вынос поворотного кулака вперед:" + min_r1r2_y + " мм (относительно центра вращения колеса)");
        System.out.println("Вынос поворотного кулака в стороны:" + min_r1r2_x + " мм (относительно центра вращения колеса в ширь)");
        System.out.println("Погрешность:" + min);
        System.out.println("Time:" + time + "ms");
    }

    private static double test(
        double r1r2_y,
        double r1r2_x
    ) {
        // Длина рейки
        final double l1r1 = l2r2 + 2 * r1r2_x;
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
            double r1_x = r1r2_x * StrictMath.cos(StrictMath.toRadians(r_a)) + r1r2_y * StrictMath.sin(StrictMath.toRadians(r_a));
            double r1_y = -r1r2_x * StrictMath.sin(StrictMath.toRadians(r_a)) + r1r2_y * StrictMath.cos(StrictMath.toRadians(r_a));
            double l2r1 = Trigonometry.length(l2r2 + r1_x, r1_y);
            double l1l2r1 = Trigonometry.angle(r1r2, l2r1, l1r1);
            double r1l2r2 = Trigonometry.angle(l2r1, l2r2, r1r2);
            double l1l2r2 = 90 - l1l2r1 - r1l2r2 + a11;
            e += StrictMath.pow(l_a - l1l2r2, 2);
        }
        return e;
    }
}
