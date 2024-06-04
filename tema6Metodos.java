import java.util.Scanner;

public class tema6Metodos {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Solicitar valores iniciales al usuario
        System.out.print("Ingrese el valor inicial de x (x0): ");
        double x0 = scanner.nextDouble();

        System.out.print("Ingrese el valor inicial de y (y0): ");
        double y0 = scanner.nextDouble();

        System.out.print("Ingrese el tamaño de paso (h): ");
        double h = scanner.nextDouble();

        System.out.print("Ingrese el número de pasos: ");
        int n = scanner.nextInt();

        double[] x = new double[n + 1];
        double[] y = new double[n + 1];

        // Valores iniciales
        x[0] = x0;
        y[0] = y0;
        // Calcular los primeros valores con Runge-Kutta de orden 4
        for (int i = 0; i < 3 && i < n; i++) {
            double k1 = f(x[i], y[i]);
            double k2 = f(x[i] + h / 2, y[i] + h / 2 * k1);
            double k3 = f(x[i] + h / 2, y[i] + h / 2 * k2);
            double k4 = f(x[i] + h, y[i] + h * k3);

            y[i + 1] = y[i] + (h / 6) * (k1 + 2 * k2 + 2 * k3 + k4);
            x[i + 1] = x[i] + h;

            System.out.println("Paso " + (i + 1) + ":");
            System.out.println("k1 = " + k1);
            System.out.println("k2 = " + k2);
            System.out.println("k3 = " + k3);
            System.out.println("k4 = " + k4);
            System.out.println("x[" + (i + 1) + "] = " + x[i + 1] + ", y[" + (i + 1) + "] = " + y[i + 1]);
        }
        // Calcular y[n] usando Adams-Bashforth de orden 4 si hay suficientes puntos
        if (n >= 4) {
            for (int i = 3; i < n; i++) {
                y[i + 1] = y[i] + (h / 24) * (55 * f(x[i], y[i]) - 59 * f(x[i - 1], y[i - 1])
                        + 37 * f(x[i - 2], y[i - 2]) - 9 * f(x[i - 3], y[i - 3]));
                x[i + 1] = x[i] + h;

                System.out.println("Paso " + (i + 1) + ":");
                System.out.println("x[" + (i + 1) + "] = " + x[i + 1] + ", y[" + (i + 1) + "] = " + y[i + 1]);
            }
        } else {
            System.out.println("Se necesitan al menos 4 puntos para usar el método de Adams-Bashforth.");
        }

        scanner.close();
    }

    public static double f(double x, double y) {
        return x + y;
    }
}
