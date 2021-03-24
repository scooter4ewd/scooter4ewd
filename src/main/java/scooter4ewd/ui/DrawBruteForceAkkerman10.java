package scooter4ewd.ui;

import org.math.plot.Plot3DPanel;
import scooter4ewd.BruteForceAkkerman10;

import javax.swing.*;
import java.awt.*;

/**
 * График ошибки для BruteForceAkkerman10
 */
public class DrawBruteForceAkkerman10 {
    public static void main(String[] args) {
        JFrame frame=new JFrame("DrawBruteForceAkkerman10");
        final Plot3DPanel plot = new Plot3DPanel();
        frame.setContentPane(plot);
        double x_array[]=new double[31];
        double y_array[]=new double[101];
        double z_array[][]=new double[y_array.length][x_array.length];
        double x_min[] = new double[1];
        double y_min[] = new double[1];
        double z_min[] = new double[]{Integer.MAX_VALUE};

        int x_index=0;
        // Перебор выноса поворотного кулака в сторону
        for (double r1r2_x = 0; r1r2_x <= 30; r1r2_x += 1, x_index++) {
            x_array[x_index]=r1r2_x;

            int y_index=0;
            // Перебор выноса поворотного кулака вперед
            for (double r1r2_y = 50; r1r2_y <= 150; r1r2_y += 1, y_index++) {
                y_array[y_index]=r1r2_y;
                final double e = BruteForceAkkerman10.test(r1r2_y, r1r2_x);
                z_array[y_index][x_index]= e;
                if (e < z_min[0]) {
                    x_min[0] = r1r2_x;
                    y_min[0] = r1r2_y;
                    z_min[0] = e;
                }
            }
        }

        plot.addGridPlot("BruteForce", x_array,y_array,z_array);
        plot.addScatterPlot("Min", Color.RED, x_min, y_min, z_min);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setVisible(true);
    }
}
