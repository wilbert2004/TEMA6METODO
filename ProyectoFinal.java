import java.util.Scanner;
import java.util.function.Function;
import java.beans.Expression;
import java.io.IOError;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;


public class ProyectoFinal {

   public static int contarCifrasSignificativas(double numero) {
      // Convertir el número a una cadena para facilitar el conteo de cifras
      // significativas
      String numeroStr = Double.toString(numero);

      // Contar las cifras significativas
      int cifrasSignificativas = 0;
      boolean inicioCifrasSignificativas = false;
      boolean puntoEncontrado = false;
      boolean cerosContados = false; // Variable para rastrear si ya hemos contado los ceros

      for (char c : numeroStr.toCharArray()) {
         if (Character.isDigit(c)) {
            if (c != '0' || inicioCifrasSignificativas) {
               cifrasSignificativas++;
               inicioCifrasSignificativas = true;
            }
         } else if (c == '.') {
            puntoEncontrado = true;
         } else if (puntoEncontrado && !cerosContados && c == '0') {
            cifrasSignificativas++; // Contar el cero solo si está a la derecha del punto decimal
         }
      }

      return cifrasSignificativas;
   }

   public static double calcularSesgo(double[] datos) {
      int n = datos.length;
      double suma = 0;
      double media = calcularMedia(datos);

      // Cifra significativa

      // Calcular la suma de las desviaciones al cubo
      for (double dato : datos) {
         suma += Math.pow(dato - media, 3);
      }

      // Calcular el sesgo
      double sesgo = suma / (n * Math.pow(calcularDesviacionEstandar(datos), 3));
      return sesgo;
   }

   // Método para calcular la media de un conjunto de datos
   public static double calcularMedia(double[] datos) {
      double suma = 0;
      for (double dato : datos) {
         suma += dato;
      }
      return suma / datos.length;
   }

   // Método para calcular la desviación estándar de un conjunto de datos
   public static double calcularDesviacionEstandar(double[] datos) {
      double media = calcularMedia(datos);
      double suma = 0;
      for (double dato : datos) {
         suma += Math.pow(dato - media, 2);
      }
      return Math.sqrt(suma / datos.length);
   }

   static double errorAbs(double valorExacto, double valorAproximado) {
      return Math.abs(valorAproximado - valorExacto);
   }

   static double errorRel(double errorAbs, double valorExacto) {
      return Math.abs(errorAbs / valorExacto);
   }

   // Truncar
   public static double truncar(double numero, int decimales) {
      // Multiplicamos el número por 10 elevado al número de decimales deseados
      // para pasar ese número de decimales a la parte "entera"
      // 123.4567 * 10^2 = 12345.67
      // ^^ ^^
      numero = numero * Math.pow(10, decimales);
      // Usamos el m. floor para reducir los decimales restantes a 0
      numero = Math.floor(numero);
      // Dividimos el número por el mismo poder de 10 para regresar los
      // decimales a su lugar
      numero = numero / Math.pow(10, decimales);
      return numero;
   }

   // Redondeo
   public static double redondear(double numero, int decimales) {
      numero = numero * Math.pow(10, decimales);
      // Lo mismo que con el m. truncar pero usamos el m.rint para redondear
      // la parte entera hacía arriba si el siguiente decimal es >= 5
      numero = Math.rint(numero);
      numero = numero / Math.pow(10, decimales);
      return numero;
   }
   //Erro absoluto


   // Bisección

   // Metodo punto fijo
   public static double g(double x) {
      return Math.exp(-x);
   }

   public static double f(double x) {
      return Math.exp(-x) - x;
   }

   // Interpolación de Newton
   public static double[][] calcularDiferenciasDivididas(List<Double> x, List<Double> y) {
      int n = y.size();
      double[][] dd = new double[n][n];

      // La primera columna es simplemente los valores de y
      for (int i = 0; i < n; i++) {
         dd[i][0] = y.get(i);
      }

      // Calcular las diferencias divididas
      for (int j = 1; j < n; j++) {
         for (int i = 0; i < n - j; i++) {
            dd[i][j] = (dd[i + 1][j - 1] - dd[i][j - 1]) / (x.get(i + j) - x.get(i));
         }
      }

      return dd;
   }

   // Método para construir el polinomio interpolante
   public static String interpolacionNewton(List<Double> x, List<Double> y) {
      int n = y.size();
      StringBuilder polinomio = new StringBuilder();

      double[][] dd = calcularDiferenciasDivididas(x, y);

      // Construir el polinomio
      polinomio.append(dd[0][0]);
      for (int j = 1; j < n; j++) {
         polinomio.append(" + ");
         polinomio.append(dd[0][j]);
         for (int i = 0; i < j; i++) {
            polinomio.append(" * (x - ").append(x.get(i)).append(")");
         }
      }

      return polinomio.toString();
   }

   // Método para imprimir la tabla con la tabulación
   public static void imprimirTabla(List<Double> x, List<Double> y, double[][] dd) {
      int n = x.size();
      System.out.println("Tabla de Diferencias Divididas:");
      System.out.println("J\tX_j\tf(x)\tDiferencias Divididas");

      for (int i = 0; i < n; i++) {
         System.out.print(i + "\t" + x.get(i) + "\t" + y.get(i) + "\t");
         for (int j = 0; j < n - i; j++) {
            System.out.print(dd[i][j] + "\t");
         }
         System.out.println();
      }
   }

   // Jacobi
   // M. RECURSIVO DE JACOBI
   /*
    * Retorna un arreglo con los valores de las variables (x1, x2, ..., xn)
    * Pide la matriz de coeficientes (c11, c12, ..., cnn), el vector de resultados
    * (b1, b2, ..., bn) y el vector de solución inicial (x0)
    * También la tolerancia (o error) aceptado y un límite máximo de iteraciones
    */
   public static double[] metodoJacobi(double[][] matrizCoeficientes, double[] resultados, double[] valoresIniciales,
         double tolerancia, int limiteIteraciones) {

      limiteIteraciones -= 1;
      if (limiteIteraciones == 0) { // Primer caso base, retorna los valores de entrada si el límite de iteraciones
                                    // llega a cero
         return valoresIniciales;
      }

      double[] valoresSiguientes = new double[valoresIniciales.length]; // Nuevo arreglo para guardar la siguiente
                                                                        // iteración

      // Ciclo para recorrer la matriz de coeficientes
      /*
       * Para cada "línea" de la matriz de coeficientes (que corresponde a una de las
       * ecuaciones del sistema), se usa otro
       * ciclo "for" para hacer la sumatoria del producto de cada coeficiente por el
       * valor actual de su variable asociada,
       * salvo por el término diagonal de esa ecuación (e.g. a11)
       */
      double sum;
      for (int i = 0; i < valoresIniciales.length; i++) {
         sum = 0;
         for (int j = 0; j < matrizCoeficientes.length; j++) {
            if (j != i) {
               sum += matrizCoeficientes[i][j] * valoresIniciales[j];
            }
         }

         // Se calcula el valor de cada variable para la siguiente iteración usando la
         // formula del método:
         // xi(k=n) = (bi-(sumatoria(aij+xj(k=n-1))))/aii, para xj != xi
         valoresSiguientes[i] = (resultados[i] - sum) / matrizCoeficientes[i][i];
         System.out.println("x" + i + ": " + valoresSiguientes[i]); // Se imprimen los resultados de cada iteración
      }
      System.out.println();

      // Calculamos la tolerancia de la iteración actual usando la formúla:
      // T=sqrt((sumatoria(xi(k=n)-xi(k=n-1)))^2)
      sum = 0;
      for (int i = 0; i < valoresIniciales.length; i++) {
         sum += Math.pow(valoresSiguientes[i] - valoresIniciales[i], 2);
      }

      double toleranciaActual = Math.sqrt(sum);
      if (toleranciaActual <= tolerancia) { // Segundo caso base, retorna los valores nuevos si la tolerancia es menor o
                                            // igual a la solicitada
         return valoresSiguientes;
      } else { // Si no, llama recursivamente el método con los nuevos valores
         return metodoJacobi(matrizCoeficientes, resultados, valoresSiguientes, tolerancia, limiteIteraciones);
      }
   }

   // Hacer dominante para jacobi
   // M. HACER DOMINANTE
   /*
    * Recibe una matriz nxn e intenta reordenar sus filas para convertirla en una
    * matriz diagonal dominante, retorna la matriz
    * ordenada o en caso de que no se pueda hacer dominante arroja una excepción
    */
   public static double[][] hacerDominante(double[][] matriz) throws IllegalArgumentException {

      /*
       * Mapa <llave, valor> para relacionar la posición del elemento dominante en una
       * fila con la fila misma,
       * la fila en este caso corresponde a un arreglo unidimensional
       */
      HashMap<Integer, double[]> rankingEcuaciones = new HashMap<>();
      for (double[] coeficientes : matriz) { // Ciclo para recorrer cada fila de la matriz
         double sumaAbsCoeficientes = 0;
         for (int i = 0; i < coeficientes.length; i++) { // Ciclo para sumar el valor absoluto de todos los elementos
                                                         // (coeficientes) de la fila
            sumaAbsCoeficientes += Math.abs(coeficientes[i]);
         }

         boolean diagonalDominante = false; // Variable booleana para indicar si la matriz puede ser diagonal dominante
         for (int i = 0; i < coeficientes.length; i++) { // Ciclo para checar cual elemento de la fila es dominante

            /*
             * Formula para checar dominancia, checamos si la suma absoluta de todos los
             * valores de la fila es menor que el doble
             * del elemento a checar. Esto es equivalente a restar el elemento de la suma y
             * checar si el restante es menor que el
             * elemento mismo, e.g., a0 > (a1 + a2 + ... + an)
             */
            if (sumaAbsCoeficientes < 2 * Math.abs(coeficientes[i])) {
               diagonalDominante = true;

               /*
                * Si existe un elemento dominante se ingresa toda la fila en el mapa con la
                * posición del elemto dominante como su llave.
                * En una matriz dominante diagonal todos los elementos dominantes deben estar
                * en posiciones diferentes (1 por fila:
                * a11, a22, ..., ann), por lo que si se repite la llave indicamos que la matriz
                * no se puede convertir en dominante
                */
               if (rankingEcuaciones.putIfAbsent(i, coeficientes) != null) {
                  diagonalDominante = false;
                  // System.out.println("Condición de error, ranking repetido");
                  // Condición de error, ranking repetido
               }
            }
         }
         if (!diagonalDominante) { // Si la matriz no se puede hacer dominante arrojamos una excepción
            throw new IllegalArgumentException("La matriz no se puede convertir en diagonal estrictamente dominante");
            // System.out.println("Condición de error, no existe dominante");
            // Condición de error, no existe dominante
         }
      }

      for (int i = 0; i < matriz.length; i++) { // Ciclo para ordenar la matriz acorde al mapa
         matriz[i] = rankingEcuaciones.get(i);
         for (int j = 0; j < matriz.length; j++) { // Ciclo para imprimir la matriz
            System.out.print(matriz[i][j] + " ");
         }
         System.out.println();
      }
      return matriz; // Retorno de la matriz ordenada
   }

   //Diferenciación numérica
   
    // Método para calcular la derivada usando diferencias finitas hacia adelante
    public static double derivadaDiferenciasFinitas(Funcionc funcion, double x, double h) {
      return (funcion.evaluar(x + h) - funcion.evaluar(x)) / h;
  }

  // Método para calcular la derivada usando diferencias finitas centradas
  public static double derivadaDiferenciasCentradas(Funcionc funcion, double x, double h) {
      return (funcion.evaluar(x + h) - funcion.evaluar(x - h)) / (2 * h);
  }
  private static Funcionc createFunctionFromInput(String input) {
   // Remover espacios en blanco
   String sanitizedInput = input.replaceAll("\\s+", "");

   // Retornar una lambda que evalúa la expresión para un valor dado de x
   return (double x) -> {
       String expression = sanitizedInput.replaceAll("x", Double.toString(x));
       return eval(expression);
   };
}

// Método para evaluar expresiones matemáticas simples
private static double eval(final String str) {
   return new Object() {
       int pos = -1, ch;

       void nextChar() {
           ch = (++pos < str.length()) ? str.charAt(pos) : -1;
       }

       boolean eat(int charToEat) {
           while (ch == ' ') nextChar();
           if (ch == charToEat) {
               nextChar();
               return true;
           }
           return false;
       }

       double parse() {
           nextChar();
           double x = parseExpression();
           if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
           return x;
       }

       // Gramatica:
       // expression = term | expression `+` term | expression `-` term
       // term = factor | term `*` factor | term `/` factor
       // factor = `+` factor | `-` factor | `(` expression `)` | number
       //        | functionName `(` expression `)` | factor `^` factor

       double parseExpression() {
           double x = parseTerm();
           for (;;) {
               if (eat('+')) x += parseTerm(); // suma
               else if (eat('-')) x -= parseTerm(); // resta
               else return x;
           }
       }

       double parseTerm() {
           double x = parseFactor();
           for (;;) {
               if (eat('*')) x *= parseFactor(); // multiplicacion
               else if (eat('/')) x /= parseFactor(); // division
               else return x;
           }
       }

       double parseFactor() {
           if (eat('+')) return parseFactor(); // + unario
           if (eat('-')) return -parseFactor(); // - unario

           double x;
           int startPos = this.pos;
           if (eat('(')) { // parentesis
               x = parseExpression();
               eat(')');
           } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numeros
               while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
               x = Double.parseDouble(str.substring(startPos, this.pos));
           } else if (ch >= 'a' && ch <= 'z') { // funciones
               while (ch >= 'a' && ch <= 'z') nextChar();
               String func = str.substring(startPos, this.pos);
               x = parseFactor();
               if (func.equals("sqrt")) x = Math.sqrt(x);
               else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
               else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
               else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
               else throw new RuntimeException("Unknown function: " + func);
           } else {
               throw new RuntimeException("Unexpected: " + (char)ch);
           }

           if (eat('^')) x = Math.pow(x, parseFactor()); // exponente

           return x;
       }
   }.parse();
}

private static double getInputDouble(Scanner scanner, String prompt) {
   double result = 0;
   boolean validInput = false;
   while (!validInput) {
       System.out.print(prompt);
       if (scanner.hasNextDouble()) {
           result = scanner.nextDouble();
           validInput = true;
       } else {
           System.out.println("Entrada inválida. Por favor, ingrese un número válido.");
           scanner.next(); // Limpiar entrada no válida
       }
   }
   return result;
}
   
   //metodo de biseccion 

   //metodo de gauss seidel
   private static double[] gaussSeidel(double[][] matriz) {
      int n = matriz.length;
      double[] x = new double[n];
      double[] nx = new double[n];
      double epsilon = 0.0001; // tolerancia para la convergencia
      int iteracionesMax = 1000;

      // Iteración inicial
      for (int i = 0; i < n; i++) {
          x[i] = 0;
      }

      int iteraciones = 0;
      boolean convergencia = false;
      while (!convergencia && iteraciones < iteracionesMax) {
          for (int i = 0; i < n; i++) {
              double suma = 0;
              for (int j = 0; j < n; j++) {
                  if (j != i) {
                      suma += matriz[i][j] * x[j];
                  }
              }
              nx[i] = (matriz[i][n] - suma) / matriz[i][i];
          }

          convergencia = true;
          for (int i = 0; i < n; i++) {
              if (Math.abs(nx[i] - x[i]) > epsilon) {
                  convergencia = false;
                  break;
              }
          }

          // Actualizar x
          for (int i = 0; i < n; i++) {
              x[i] = nx[i];
          }

          iteraciones++;
      }

      return x;
  }
   

   public static void ranking(double[][] m) {
      int posiciónMayor;
      double mayor;
      for (double[] ds : m) {
         posiciónMayor = -1;
         mayor = Double.NEGATIVE_INFINITY;
         for (int i = 0; i < m.length; i++) {
            if (ds[i] > mayor) {
               mayor = ds[i];
               posiciónMayor = i;
            }
         }
         System.out.println(ds + "Mayor: " + mayor + ", Posción: " + posiciónMayor);
      }
      double sumaAbsCoeficientes;
      int posiciónDominante;
      double dominante;
      for (double[] ds : m) {
         sumaAbsCoeficientes = 0;
         posiciónDominante = -1;
         dominante = Double.NaN;
         for (int i = 0; i < ds.length; i++) {
            sumaAbsCoeficientes += Math.abs(ds[i]);
         }
         for (int i = 0; i < ds.length; i++) {
            if (sumaAbsCoeficientes < 2 * Math.abs(ds[i])) {
               posiciónDominante = i;
               dominante = ds[i];
            }
         }
         System.out.println(ds + "Dominante: " + dominante + ", Posción: " + posiciónDominante);
      }
   }
   //Regla del trapecio
   // Método para calcular la aproximación de la integral usando la regla del trapecio
    public static double reglaTrapecio(Function<Double, Double> funcion, double a, double b, int n) {
        // Calculamos el ancho de cada subintervalo
        double h = (b - a) / n;
        // Sumamos el valor de la función en los extremos de los intervalos
        double suma = 0.5 * (funcion.apply(a) + funcion.apply(b));
        // Sumamos el valor de la función en los puntos interiores de los intervalos
        for (int i = 1; i < n; i++) {
            double x = a + i * h;
            suma += funcion.apply(x);
        }
        // Multiplicamos por el ancho de los intervalos y devolvemos el resultado
        return h * suma;
    }

    //Metodo de simpson
    // Función a integrar expresada por medio de simbolos en codigo
    public static double f1(double x) {
      return x * x * x / (1 + Math.sqrt(x));
  }

  // Aplicación de la Regla de Simpson con detalles explicativos
  public static double simpsonRegla(double a, double b, int n) {
      if (n % 2 != 0) {
          throw new IllegalArgumentException("El número de segmentos debe ser par.");
      }
      System.out.println("==========================================================");
      System.out.println("Usando la Regla de Simpson para estimar la integral:");
      System.out.println("Límites de integración: a = " + a + ", b = " + b);
      System.out.println("Número de segmentos: " + n);

      double h = (b - a) / n; // Tamaño de cada segmento
      System.out.println("Tamaño de cada segmento (h): " + h);

      // Evaluación de la función en los extremos
      double integral = f(a) + f(b);
      System.out.println("Evaluación de f(a) = " + f(a));
      System.out.println("Evaluación de f(b) = " + f(b));
      System.out.println("Base de la integral (suma de extremos): " + integral);

      // Suma de los términos impares (coeficiente 4)
      System.out.println("Calculando la suma de los términos impares...");
      for (int i = 1; i < n; i += 2) {
          double x = a + i * h;
          double term = 4 * f(x);
          System.out.println("f(" + x + ") * 4 = " + term);
          integral += term;
      }

      // Suma de los términos pares (coeficiente 2)
      System.out.println("Calculando la suma de los términos pares...");
      for (int i = 2; i < n; i += 2) {
          double x = a + i * h;
          double term = 2 * f(x);
          System.out.println("f(" + x + ") * 2 = " + term);
          integral += term;
      }

      // Multiplicación por h/3 para el resultado final
      integral *= (h / 3);
      System.out.println("Multiplicando por h/3 para obtener el resultado final.");
      System.out.println("Estimación de la integral: " + integral);

      return integral;
  }
  //Interpolacion de Newton
  private static double evaluateNewtonPolynomial(double[] x, double[][] dividedDifferences, int n, double value) {
   double result = dividedDifferences[0][0];
   double product;

   for (int i = 1; i < n; i++) {
       product = dividedDifferences[0][i];
       for (int j = 0; j < i; j++) {
           product *= (value - x[j]);
       }
       result += product;
   }

   return result;
}
//Larange
public static double lagrangeInterpolation(double[] x, double[] y, double xi) {
   int n = x.length;
   double result = 0.0;

   for (int i = 0; i < n; i++) {
       double term = y[i];
       for (int j = 0; j < n; j++) {
           if (j != i) {
               term = term * (xi - x[j]) / (x[i] - x[j]);
           }
       }
       result += term;
   }

   return result;
}

//Metodo de Euler
public static double[] euler(double x0, double y0, double h, int n) {
   double x = x0;
   double y = y0;
   for (int i = 0; i < n; i++) {
       y += h * calcularDerivada(x, y);
       x += h;
   }
   return new double[]{x, y};
}

// Método para calcular la derivada basado en la entrada del usuario
public static double calcularDerivada(double x, double y) {
   // Aquí se debe implementar la lógica para interpretar y calcular la derivada basada en la entrada del usuario
   // Por ejemplo, si el usuario ingresa "2 * x * y", se debe calcular y devolver ese valor
   // Esta es una funcionalidad avanzada y requiere un análisis de la entrada del usuario o una interfaz gráfica
   return 2 * x * y; // Esto es solo un placeholder
}
//Método de Taylor
   // La función original f(x) = x^4 - 3x^2 + 1
   public static double f4(double x) {
      return Math.pow(x, 4) - 3 * Math.pow(x, 2) + 1;
  }
  
  // Primera derivada de f(x)
  public static double fPrime(double x) {
      return 4 * Math.pow(x, 3) - 6 * x;
  }
  
  // Segunda derivada de f(x)
  public static double fDoublePrime(double x) {
      return 12 * Math.pow(x, 2) - 6;
  }
  
  // Tercera derivada de f(x)
  public static double fTriplePrime(double x) {
      return 24 * x;
  }
  
  // Cuarta derivada de f(x)
  public static double fQuadruplePrime(double x) {
      return 24;
  }
  
  // Método para calcular el término de Taylor en torno a x0
  public static double taylorTerm(int n, double x0) {
      double term;
      switch (n) {
          case 0:
              term = f(x0);
              break;
          case 1:
              term = fPrime(x0);
              break;
          case 2:
              term = fDoublePrime(x0) / 2;
              break;
          case 3:
              term = fTriplePrime(x0) / 6;
              break;
          case 4:
              term = fQuadruplePrime(x0) / 24;
              break;
          default:
              term = 0;
              break;
      }
      return term;
  }
  
  // Método para construir la serie de Taylor
  public static String taylorSeries(double x0, int terms) {
      StringBuilder series = new StringBuilder();
      for (int n = 0; n < terms; n++) {
          double coefficient = taylorTerm(n, x0);
          if (n == 0) {
              series.append(coefficient);
          } else if (coefficient != 0) {
              if (coefficient > 0) {
                  series.append(" + ");
              } else {
                  series.append(" - ");
                  coefficient = -coefficient; // Para evitar duplicar el signo negativo
              }
              series.append(coefficient).append("*(x - ").append(x0).append(")^").append(n);
          }
      }
      return series.toString();
  }
  //Metodo 
  public static double f5(double x, double y) {
   return x+y;
      }


    
   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      int opcion, opc = 0;

      do {
         System.out.println("==========================================================");
         System.out.println("Bienvenido a los programas de METODOS NUMÉRICOS");
         System.out.println("Elige el tema que desees Explorar sus Métodos: ");
         System.out.println("1.TEMA 1");
         System.out.println("2.TEMA 2");
         System.out.println("3.TEMA 3");
         System.out.println("4.TEMA 4");
         System.out.println("5.TEMA 5");
         System.out.println("6.TEMA 6");
         System.out.println("7.Salir del programa");
         opcion = scanner.nextInt();
         System.out.println("==========================================================");
         switch (opcion) {
            case 1:
               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 1");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Cifra significativa");
                  System.out.println("2.Presición");
                  System.out.println("3.Exactitud");
                  System.out.println("4.Sesgo");
                  System.out.println("5.Error absoluto");
                  System.out.println("6.Error relativo");
                  System.out.println("7.Truncamiento");
                  System.out.println("8.Redondeo");
                  System.out.println("9.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                        System.out.println("Cifra significativa");
                        boolean continuar = true;

                        while (continuar) {
                           // Solicitar al usuario que ingrese el tipo de número
                           System.out.println("Elija el tipo de número:");
                           System.out.println("1. Número normal");
                           System.out.println("2. Número en notación científica");
                           System.out.print("Ingrese su elección (1 o 2): ");
                           int opcion2 = scanner.nextInt();

                           // Declarar variables para el número y el exponente
                           double numero = 0;
                           int exponente = 0;

                           // Verificar la opción seleccionada por el usuario
                           if (opcion2 == 1) {
                              // Solicitar al usuario que ingrese un número normal
                              System.out.print("Ingrese un número normal: ");
                              numero = scanner.nextDouble();
                           } else if (opcion2 == 2) {
                              // Solicitar al usuario que ingrese un número y un exponente en notación
                              // científica
                              System.out.print("Ingrese el número en notación científica (por ejemplo, 6.022): ");
                              numero = scanner.nextDouble();
                              System.out.print("Ingrese el exponente en notación científica (por ejemplo, 23): ");
                              exponente = scanner.nextInt();
                           } else {
                              System.out.println("Opción no válida. Por favor, ingrese 1 o 2.");
                              scanner.close();
                              return;
                           }

                           // Imprimir el número ingresado
                           if (opcion == 1) {
                              System.out.println("El número ingresado es: " + numero);
                           } else {
                              System.out.println(
                                    "El número ingresado en notación científica es: " + numero + " x 10^" + exponente);
                           }

                           // Contar las cifras significativas
                           int cifrasSignificativas;
                           if (opcion == 1) {
                              cifrasSignificativas = contarCifrasSignificativas(numero);
                           } else {
                              cifrasSignificativas = contarCifrasSignificativas(numero);
                           }

                           // Imprimir el resultado de las cifras significativas
                           System.out.println("El número tiene " + cifrasSignificativas + " cifras significativas");

                           // Preguntar al usuario si desea agregar otro valor
                           System.out.print("¿Desea agregar otro valor? (Sí/No): ");
                           String respuesta = scanner.next();
                           if (!respuesta.equalsIgnoreCase("Si")) {
                              continuar = false;
                           }
                        }

                        // Cerrar el scanner

                        break;

                     case 2:
                        System.out.println("Presición");
                        // Pedir al usuario que ingrese el valor vesadardadero
                        System.out.print("Ingrese el valor verdadero: ");
                        double valorVerdadero = scanner.nextDouble();

                        // Pedir al usuario que ingrese cuántos valores desea comparar
                        System.out.print("¿Cuántos valores desea ingresar para comparar? ");
                        int cantidadValores = scanner.nextInt();

                        // Pedir al usuario que ingrese el margen de error
                        System.out.print("Ingrese el margen de error: ");
                        double margenError = scanner.nextDouble();

                        // Inicializar variables para el conteo de valores exactos y precisos
                        int exactos = 0;
                        int precisos = 0;

                        // Pedir al usuario que ingrese los valores para comparar
                        for (int i = 1; i <= cantidadValores; i++) {
                           System.out.print("Ingrese el valor #" + i + ": ");
                           double valor = scanner.nextDouble();

                           // Verificar si el valor es exacto o preciso
                           if (valor >= valorVerdadero - margenError && valor <= valorVerdadero + margenError) {
                              precisos++;
                              if (valor == valorVerdadero) {
                                 exactos++;
                              }
                           }
                        }
                        // Mostrar resultados
                        System.out.println("Exactos: " + exactos);
                        System.out.println("Precisos: " + precisos);

                        break;
                     case 3:
                        System.out.println("Exactitud");
                        System.out.print("Ingrese el valor verdadero: ");
                        double valorVerdadero1 = scanner.nextDouble();

                        // Pedir al usuario que ingrese cuántos valores desea comparar
                        System.out.print("¿Cuántos valores desea ingresar para comparar? ");
                        int cantidadValores1 = scanner.nextInt();

                        // Pedir al usuario que ingrese el margen de error
                        System.out.print("Ingrese el margen de error: ");
                        double margenError1 = scanner.nextDouble();

                        // Inicializar variables para el conteo de valores exactos y precisos
                        int exactos1 = 0;
                        int precisos1 = 0;

                        // Pedir al usuario que ingrese los valores para comparar
                        for (int i = 1; i <= cantidadValores1; i++) {
                           System.out.print("Ingrese el valor #" + i + ": ");
                           double valor = scanner.nextDouble();

                           // Verificar si el valor es exacto o preciso
                           if (valor >= valorVerdadero1 - margenError1 && valor <= valorVerdadero1 + margenError1) {
                              precisos1++;
                              if (valor == valorVerdadero1) {
                                 exactos1++;
                              }
                           }
                        }
                        // Mostrar resultados
                        System.out.println("Exactos: " + exactos1);
                        System.out.println("Precisos: " + precisos1);

                        break;
                     case 4:
                        System.out.println("Sesgo");
                        System.out.println("Ingrese la cantidad de números:");
                        int cantidadNumeros = scanner.nextInt();

                        double[] datos = new double[cantidadNumeros];

                        System.out.println("Ingrese los números uno por uno:");

                        for (int i = 0; i < cantidadNumeros; i++) {
                           datos[i] = scanner.nextDouble();
                        }

                        double sesgo = calcularSesgo(datos);
                        System.out.println("El sesgo de los datos es: " + sesgo);
                        break;

                     case 5:
                        System.out.println("Error absoluto");
                        System.out.print("Ingrese el valor exacto: ");
                        double valorExacto1 = scanner.nextInt();

                        System.out.print("Ingrese el valor aproximado: ");
                        double valorAproximado1 = scanner.nextDouble();

                        double errorAbsoluto1 = errorAbs(valorExacto1, valorAproximado1);
                        System.out.println("El error absoluto: "+errorAbsoluto1);

                        

                        break;
                     case 6:
                        System.out.println("Error relativo");

                        System.out.print("Ingrese el valor exacto: ");
                        double valorExacto = scanner.nextInt();

                        System.out.print("Ingrese el valor aproximado: ");
                        double valorAproximado = scanner.nextDouble();

                        double errorAbsoluto = errorAbs(valorExacto, valorAproximado);

                        double resultado = errorRel(errorAbsoluto, valorExacto);
                        System.out.print("El error relativo es: " + (resultado * 100));
                        System.out.println();

                        break;

                     case 7:
                        System.out.println("Truncamiento");

                        double vverda, vaprox, errAbs;
                        int decimales;

                        System.out.print("Ingresa el valor verdadero: ");
                        vverda = scanner.nextDouble(); // Solicitamos el valor verdadero
                        System.out.print("Ingresa el numero de decimales que desea truncar en el valor verdadero: ");
                        decimales = scanner.nextInt(); // Solicitamos el no. de decimales para truncar
                        vverda = truncar(vverda, decimales);
                        System.out.println("El resultado del valor verdadero truncado: " + vverda + '\n');

                        System.out.print("Ingresa el valor aproximado: ");
                        vaprox = scanner.nextDouble(); // Solicitamos el valor aproximado
                        System.out.print("Ingresa el numero de decimales que desea truncar en el valor aproximado: ");
                        decimales = scanner.nextInt();
                        vaprox = truncar(vaprox, decimales);
                        System.out.println("El resaultado del valor aproximado truncado: " + vaprox + '\n');

                        errAbs = Math.abs(vverda - vaprox); // Calculamos el error absoluto restando el valor apróximado
                                                            // del verdadero
                        System.out.print("Ingresa el número de decimales que desea truncar en el error absoluto: ");
                        decimales = scanner.nextInt();
                        System.out.println("El valor absuluto es: " + truncar(errAbs, decimales) + '\n');
                        break;
                     case 8:

                        System.out.println("Redondeo");
                        System.out.print("Ingresa el valor verdadero: ");
                        vverda = scanner.nextDouble(); // Solicitamos el valor verdadero
                        System.out.print("Ingresa el numero de decimales que desea redondear en el valor verdadero: ");
                        decimales = scanner.nextInt(); // Solicitamos el no. de decimales para redondear
                        vverda = redondear(vverda, decimales);
                        System.out.println("El resultado del valor verdadero redondeado: " + vverda + '\n');

                        System.out.print("Ingresa el valor aproximado: ");
                        vaprox = scanner.nextDouble(); // Solicitamos el valor aproximado
                        System.out.print("Ingresa el numero de decimales que desea redondear en el valor aproximado: ");
                        decimales = scanner.nextInt();
                        vaprox = redondear(vaprox, decimales);
                        System.out.println("El resaultado del valor aproximado redondeado: " + vaprox + '\n');

                        errAbs = Math.abs(vverda - vaprox); // Calculamos el error absoluto restando el valor apróximado
                                                            // del verdadero
                        System.out.print("Ingresa el número de decimales que desea redondear en el error absoluto: ");
                        decimales = scanner.nextInt();
                        System.out.println("El valor absuluto es: " + redondear(errAbs, decimales) + '\n');
                        break;

                     default:
                        break;

                  }

               } while (opc != 9);
               System.out.println("Saliedo...");
               break;

            case 2:

               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 2");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Metodo de bisección");
                  System.out.println("2.Método punto fijo");
                  System.out.println("3.Interpolación de Newton");
                  System.out.println("4.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                  System.out.println("Metodo de biseccion");
                  System.out.println("Ingrese la función (por ejemplo: x^2-5*x+6): ");
                  String funcion = scanner.nextLine();

                  System.out.println("Ingrese el extremo izquierdo del intervalo (a): ");
                  double a = scanner.nextDouble();

                  System.out.println("Ingrese el extremo derecho del intervalo (b): ");
                  double b = scanner.nextDouble();

                  System.out.println("Ingrese el número de iteraciones: ");
                  int iteraciones = scanner.nextInt();

                  System.out.println("Ingrese el error mínimo: ");
                  double tolerancia = scanner.nextDouble();

                  
                  Biseccion.biseccion(a, b, iteraciones, tolerancia);
    
                     

                        break;

                     case 2:
                        int iter = 0;
                        double x = 0;
                        System.out.println("MÉTODO PUNTO FIJO");

                        while (Math.abs(f(x)) > 1e-8 && iter <= 1000000) {
                           x = ProyectoFinal.g(x);
                           iter++;
                        }
                        if (Math.abs(f(x)) < 1e-8) {
                           System.out.println("e^(-x)-x");
                           System.out.println("Raiz estimada = " + x);
                        } else {
                           System.out.println("No se pudo obtener una raiz");
                        }

                        break;
                     case 3:
                        System.out.println("Interpolación de Newton");
                        // Pedir al usuario que ingrese la cantidad de puntos
                        System.out.print("Ingrese la cantidad de puntos: ");
                        int n = scanner.nextInt();

                        // Inicializar listas para almacenar los puntos
                        List<Double> x1 = new ArrayList<>();
                        List<Double> y = new ArrayList<>();

                        // Pedir al usuario que ingrese los puntos
                        for (int i = 0; i < n; i++) {
                           System.out.print("Ingrese el valor de x" + (i + 1) + ": ");
                           x1.add(scanner.nextDouble());
                           System.out.print("Ingrese el valor de y" + (i + 1) + ": ");
                           y.add(scanner.nextDouble());
                        }

                        // Calcular el polinomio interpolante
                        String polinomio = interpolacionNewton(x1, y);

                        // Calcular las diferencias divididas
                        double[][] dd = calcularDiferenciasDivididas(x1, y);

                        // Imprimir la tabla de tabulación
                        imprimirTabla(x1, y, dd);

                        // Imprimir el polinomio interpolante
                        System.out.println("Polinomio interpolante: P(x) = " + polinomio);
                        break;

                  }

               } while (opc != 4);
               System.out.println("Saliedo...");

               break;
            case 3:

               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 3");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Método de Jacobi");
                  System.out.println("2.Método de Gauss Seidel");
                  System.out.println("3.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                        System.out.println("Método de Jacobi");
                        System.out.println("Ingrese el numero de variables");
                        int n1 = scanner.nextInt();

                        double[][] matrizCoeficientes = new double[n1][n1];
                        System.out.println("Ingrese el valor de los ceficientes");

                        for (int i = 0; i < n1; i++) {
                           for (int j = 0; j < n1; j++) {
                              matrizCoeficientes[i][j] = scanner.nextDouble();
                           }
                        }

                        double[] resultados = new double[n1];
                        System.out.println("Ingrese los resultados de cada ecuacion");

                        for (int i = 0; i < n1; i++) {
                           resultados[i] = scanner.nextDouble();
                        }

                        double valoresIniciales[] = new double[n1];
                        System.out.println("Ingrese los valores la solución inicial");
                        for (int i = 0; i < n1; i++) {
                           valoresIniciales[i] = scanner.nextDouble();
                        }

                        System.out.println("Escriba la tolerancia");
                        double tolerancia = scanner.nextDouble();
                        System.out.println("Ingrese el limite de iteraciones");
                        int iteraciones = scanner.nextInt();

                        try {
                           double[][] matrizDominante = hacerDominante(matrizCoeficientes);
                           metodoJacobi(matrizDominante, resultados, valoresIniciales, tolerancia, iteraciones);
                        } catch (IllegalArgumentException e) {
                           System.out.println(e.getMessage());
                        }

                     case 2:
                     System.out.println("Metodo de Gauss Seidel");
                     System.out.println("Introduce el número de ecuaciones: ");
                     int n = scanner.nextInt();
             
                     double[][] matriz = new double[n][n + 1];
             
                     for (int i = 0; i < n; i++) {
                         System.out.println("Ecuación " + (i + 1) + ":");
                         for (int j = 0; j < n; j++) {
                             System.out.println("Coeficiente x" + (j + 1) + ":");
                             matriz[i][j] = scanner.nextDouble();
                         }
                         System.out.println("Constante: ");
                         matriz[i][n] = scanner.nextDouble();
                     }
             
                     double[] soluciones = gaussSeidel(matriz);
             
                     System.out.println("Resultados:");
                     for (int i = 0; i < n; i++) {
                         System.out.println("x" + (i + 1) + " = " + soluciones[i]);
                     }
             

                  }

               } while (opc != 3);
               System.out.println("Saliedo...");

               break;
            case 4:
               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 4");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Diferenciacion numérica");
                  System.out.println("2.Regla del trapecio");
                  System.out.println("3.Regla de Simpson");
                  System.out.println("4.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                     System.out.println("Diferenciación numérica");
                     
        // Pedir al usuario que ingrese una función
                     System.out.print("Ingrese una función de x (por ejemplo, x^2 + 3*x + 2): ");
                     String input = scanner.next();

                     // Crear una función lambda a partir del input del usuario
                     Funcionc funcion = createFunctionFromInput(input);

                     // Pedir al usuario que ingrese el punto en el que quiere evaluar la derivada
                     double x = getInputDouble(scanner, "Ingrese el valor de x en el que quiere evaluar la derivada: ");

                     // Pedir al usuario que ingrese el valor de h
                     double h = getInputDouble(scanner, "Ingrese un valor pequeño para h: ");

                     // Calcular la derivada usando diferencias finitas hacia adelante
                     double derivadaAdelante = derivadaDiferenciasFinitas(funcion, x, h);
                     System.out.println("Derivada usando diferencias finitas hacia adelante: " + derivadaAdelante);

                     // Calcular la derivada usando diferencias finitas centradas
                     double derivadaCentrada = derivadaDiferenciasCentradas(funcion, x, h);
                     System.out.println("Derivada usando diferencias finitas centradas: " + derivadaCentrada);

               


                        break;

                     case 2:
                     System.out.println("Regla del trapecio");
                      // Solicitamos al usuario que ingrese los límites de integración
                     System.out.print("Ingrese el límite inferior de integración: ");
                     double a = scanner.nextDouble();
                     System.out.print("Ingrese el límite superior de integración: ");
                     double b = scanner.nextDouble();

                     // Solicitamos al usuario que ingrese el número de subintervalos
                     System.out.print("Ingrese el número de subintervalos (mayor número, mayor precisión): ");
                     int n = scanner.nextInt();

                     // Definimos la función f(x) = e^(x^2)
                     Function<Double, Double> funciont = xt -> Math.exp(xt * xt);

                     // Calculamos la aproximación de la integral usando la regla del trapecio
                     double resultado = reglaTrapecio(funciont, a, b, n);

                     // Tomamos el valor absoluto del resultado para asegurarnos de que sea positivo
                     double resultadoPositivo = Math.abs(resultado);

                     // Imprimimos el resultado
                     System.out.println("Aproximación de la integral de e^(x^2) usando la regla del trapecio: " + resultadoPositivo);


                        break;
                     case 3: 
                     System.out.println("Regla de Simpson");
                     System.out.print("Ingrese el límite inferior de integración: ");
                     double a1 = scanner.nextDouble();
             
                     System.out.print("Ingrese el límite superior de integración: ");
                     double b1 = scanner.nextDouble();
             
                     System.out.print("Ingrese el número de segmentos (debe ser par): ");
                     int n1 = scanner.nextInt();
             
                     if (n1 % 2 != 0) {
                         System.out.println("El número de segmentos debe ser par.");
                         return;
                     }
             
                     // Aplicar la Regla de Simpson
                     simpsonRegla(a1, b1, n1);

                        break;

                  }

               } while (opc != 4);
               System.out.println("Saliedo...");

               break;
            case 5:

               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 5");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Polinomio de interpolación de Newton");
                  System.out.println("2.Polinomio de interpolación de Lagrange");
                  System.out.println("3.Regresión lineal");
                  System.out.println("4.Míminos cuadrados");
                  System.out.println("5.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                     System.out.println("Polinomio de interpolacion de Newton");
                      // Lectura de los puntos
                        System.out.print("Ingrese el número de puntos: ");
                        int n = scanner.nextInt();

                        double[] x = new double[n];
                        double[] y = new double[n];

                        System.out.println("Ingrese los puntos (x y): ");
                        for (int i = 0; i < n; i++) {
                              System.out.print("x[" + i + "]: ");
                              x[i] = scanner.nextDouble();
                              System.out.print("y[" + i + "]: ");
                              y[i] = scanner.nextDouble();
                        }

                        // Cálculo de la tabla de diferencias divididas
                        double[][] dividedDifferences = new double[n][n];
                        for (int i = 0; i < n; i++) {
                              dividedDifferences[i][0] = y[i];
                        }

                        for (int j = 1; j < n; j++) {
                              for (int i = 0; i < n - j; i++) {
                                 dividedDifferences[i][j] = (dividedDifferences[i + 1][j - 1] - dividedDifferences[i][j - 1]) / (x[i + j] - x[i]);
                              }
                        }

                        // Mostrar la tabla de diferencias divididas
                        System.out.println("Tabla de diferencias divididas:");
                        for (int i = 0; i < n; i++) {
                              for (int j = 0; j < n - i; j++) {
                                 System.out.printf("%.4f\t", dividedDifferences[i][j]);
                              }
                              System.out.println();
                        }

                        // Lectura del número de puntos en los que se va a evaluar el polinomio
                        System.out.print("Ingrese el número de puntos donde desea evaluar el polinomio: ");
                        int m = scanner.nextInt();
                        double[] evalPoints = new double[m];

                        System.out.println("Ingrese los puntos x donde desea evaluar el polinomio: ");
                        for (int i = 0; i < m; i++) {
                              System.out.print("x_eval[" + i + "]: ");
                              evalPoints[i] = scanner.nextDouble();
                        }

                        // Evaluación del polinomio interpolador en los puntos especificados
                        for (int i = 0; i < m; i++) {
                              double value = evalPoints[i];
                              double result = evaluateNewtonPolynomial(x, dividedDifferences, n, value);
                              System.out.printf("El valor interpolado en x = %.4f es: %.4f\n", value, result);
                        }

                        break;

                     case 2:
                     System.out.println("Polinomio de interpolación de Lagrange");
                     System.out.print("Introduce el número de puntos: ");
                     int n1 = scanner.nextInt();
             
                     double[] x1 = new double[n1];
                     double[] y1 = new double[n1];
             
                     System.out.println("Introduce los valores de x:");
                     for (int i = 0; i < n1; i++) {
                         x1[i] = scanner.nextDouble();
                     }
             
                     System.out.println("Introduce los valores de y:");
                     for (int i = 0; i < n1; i++) {
                         y1[i] = scanner.nextDouble();
                     }
             
                     System.out.print("Introduce el valor de xi para el cual quieres interpolar: ");
                     double xi = scanner.nextDouble();
             
                     double yi = lagrangeInterpolation(x1, y1, xi);
                     System.out.println("El valor interpolado en xi = " + xi + " es yi = " + yi);



                        break;
                     case 3: 
                     System.out.println("Regresión lineal");
                     new App();


                        break;
                     case 4:
                     System.out.println("Minimos cuadrados");
                     System.out.print("Ingresa el número de datos: ");
                     int n3 = scanner.nextInt();

                     double[] x3 = new double[n3];
                     double[] y3 = new double[n3];

                     System.out.println("Ingresa los valores de x:");
                     for (int i = 0; i < n3; i++) {
                           x3[i] = scanner.nextDouble();
                     }

                     System.out.println("Ingresa los valores de y:");
                     for (int i = 0; i < n3; i++) {
                           y3[i] = scanner.nextDouble();
                     }

                     // Calculamos la pendiente (m) y el punto de corte (b)
                     double sumX = 0.0;
                     double sumY = 0.0;
                     double sumXY = 0.0;
                     double sumX2 = 0.0;

                     for (int i = 0; i < n3; i++) {
                           sumX += x3[i];
                           sumY += y3[i];
                           sumXY += x3[i] * y3[i];
                           sumX2 += x3[i] * x3[i];
                     }

                     double m3 = (n3 * sumXY - sumX * sumY) / (n3 * sumX2 - sumX * sumX);
                     double b3 = (sumY - m3 * sumX) / n3;

                     System.out.println("La línea de mejor ajuste es: y = " + m3 + "x + " + b3);
                        break;

                  }

               } while (opc != 5);
               System.out.println("Saliedo...");

               break;
            case 6:

               do {
                  System.out.println("==========================================================");
                  System.out.println("TEMA 6");
                  System.out.println("Elige el método deseado: ");
                  System.out.println("1.Método de Euler");
                  System.out.println("2.Método de Taylor");
                  System.out.println("3.Método de Adams-Bashforth");
                  System.out.println("4.Salir");
                  opc = scanner.nextInt();
                  System.out.println("==========================================================");
                  switch (opc) {
                     case 1:
                     System.out.println("Metodo de Euler");
                      // Entrada del usuario para la función derivada f(x, y) = 2 * x * y
                     System.out.println("Introduce la función derivada f(x, y). Por ejemplo, '2 * x * y':");
                     String funcionDerivada = scanner.next();

                     // Valores iniciales ingresados por el usuario
                     System.out.println("Introduce el valor inicial de x (x0):");
                     double x0 = scanner.nextDouble();

                     System.out.println("Introduce el valor inicial de y (y0):");
                     double y0 = scanner.nextDouble();

                     System.out.println("Introduce el tamaño del paso (h):");
                     double h = scanner.nextDouble();

                     System.out.println("Introduce el número de pasos (n):");
                     int n = scanner.nextInt();

                     // Llamamos al método de Euler
                     double[] resultado = euler(x0, y0, h, n);
                     System.out.printf("Después de %d pasos, x = %.2f y y = %.2f%n", n, resultado[0], resultado[1]);

                        break;

                     case 2:
                     System.out.println("Metodo de Taylor");
                     double x01 = 1.0; // Punto alrededor del cual se expande la serie de Taylor
                     int terms = 5;   // Número de términos en la serie de Taylor

                     String series = taylorSeries(x01, terms);
                     System.out.println("La serie de Taylor de f(x) alrededor de x0 = " + x01 + " es:");
                     System.out.println(series);


                        break;
                        
                     case 3:
                     System.out.println("Método de Adams-Bashforth");
                     // Solicitar valores iniciales al usuario
                     System.out.print("Ingrese el valor inicial de x (x0): ");
                     double x0ol = scanner.nextDouble();

                     System.out.print("Ingrese el valor inicial de y (y0): ");
                     double y0ol = scanner.nextDouble();

                     System.out.print("Ingrese el tamaño de paso (h): ");
                     double hol = scanner.nextDouble();

                     System.out.print("Ingrese el número de pasos: ");
                     int nol = scanner.nextInt();

                     double[] xol = new double[nol + 1];
                     double[] yol = new double[nol + 1];

                     // Valores iniciales
                     xol[0] = x0ol;
                     yol[0] = y0ol;
                     // Calcular los primeros valores con Runge-Kutta de orden 4
                     for (int i = 0; i < 3 && i < nol; i++) {
                           double k1 = f5(xol[i], yol[i]);
                           double k2 = f5(xol[i] + hol / 2, yol[i] + hol / 2 * k1);
                           double k3 = f5(xol[i] + hol / 2, yol[i] + hol / 2 * k2);
                           double k4 = f5(xol[i] + hol, yol[i] + hol * k3);

                           yol[i + 1] = yol[i] + (hol / 6) * (k1 + 2 * k2 + 2 * k3 + k4);
                           xol[i + 1] = xol[i] + hol;

                           System.out.println("Paso " + (i + 1) + ":");
                           System.out.println("k1 = " + k1);
                           System.out.println("k2 = " + k2);
                           System.out.println("k3 = " + k3);
                           System.out.println("k4 = " + k4);
                           System.out.println("x[" + (i + 1) + "] = " + xol[i + 1] + ", y[" + (i + 1) + "] = " + yol[i + 1]);
                     }

                     // Calcular y[n] usando Adams-Bashforth de orden 4 si hay suficientes puntos
                     if (nol >= 4) {
                           for (int i = 3; i < nol; i++) {
                              yol[i + 1] = yol[i] + (hol / 24) * (55 * f5(xol[i], yol[i]) - 59 * f5(xol[i - 1], yol[i - 1])
                                       + 37 * f5(xol[i - 2], yol[i - 2]) - 9 * f5(xol[i - 3], yol[i - 3]));
                              xol[i + 1] = xol[i] + hol;

                              System.out.println("Paso " + (i + 1) + ":");
                              System.out.println("x[" + (i + 1) + "] = " + xol[i + 1] + ", y[" + (i + 1) + "] = " + yol[i + 1]);
                           }
                     } else {
                           System.out.println("Se necesitan al menos 4 puntos para usar el método de Adams-Bashforth.");
                     }
                     

                        break;

                  }

               } while (opc != 4);
               System.out.println("Saliedo...");

               break;
         }

      } while (opcion != 7);
      System.out.println("Saliedo...");

   }

}
class Biseccion {

    
   public static double f(double x) {
       return -2 + 7 * x - 5 * Math.pow(x, 2) + 6 * Math.pow(x, 3);
   }

   
   public static double biseccion(double a, double b, int iteraciones, double tolerancia) {
       double c = 0; 
      
       System.out.println("Iteración\t\tXa\t\tXb\t\tXm\t\tF(Xm)\t\tError");

       
       for (int i = 0; i < iteraciones; i++) {
           c = (a + b) / 2; 

          
           double f_c = f(c);

           
           double error = Math.abs((b - a) / 2);

           
           System.out.printf("%d\t\t\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\n", i + 1, a, b, c, f_c, error);

           
           if (Math.abs(f_c) < tolerancia) {
               break; 
           } else if (f_c * f(a) < 0) {
               b = c; 
           } else {
               a = c; 
           }
       }

       return c; 
   }
}

//REGRESION LINEAL
class App {
   private double[] xValues;
   private double[] yValues;
   private LinearRegression lr;

   public App() {
       Scanner scanner = new Scanner(System.in);

       System.out.print("Número de puntos de datos: ");
       int numPoints = scanner.nextInt();
       xValues = new double[numPoints];
       yValues = new double[numPoints];

       System.out.println("Introduce los valores de X:");
       for (int i = 0; i < numPoints; i++) {
           xValues[i] = scanner.nextDouble();
       }

       System.out.println("Introduce los valores de Y:");
       for (int i = 0; i < numPoints; i++) {
           yValues[i] = scanner.nextDouble();
       }

       lr = new LinearRegression(xValues, yValues);
       lr.fit();

       System.out.println("Pendiente: " + lr.getSlope());
       System.out.println("Intercepto: " + lr.getIntercept());

       System.out.print("Introduce un valor de X para predecir Y: ");
       double xValue = scanner.nextDouble();
       double prediction = lr.predict(xValue);
       System.out.println("Predicción para x = " + xValue + ": " + prediction);
   }

   
}

class LinearRegression {
   private final double[] x;
   private final double[] y;
   private final int n;
   private double slope;
   private double intercept;
   private boolean fitted;

   public LinearRegression(double[] x, double[] y) {
       if (x.length != y.length) {
           throw new IllegalArgumentException("Las longitudes de x y y deben ser iguales.");
       }
       this.x = x;
       this.y = y;
       this.n = x.length;
       this.fitted = false;
   }

   public void fit() {
       double sumX = Arrays.stream(x).sum();
       double sumY = Arrays.stream(y).sum();
       double sumX2 = Arrays.stream(x).map(val -> val * val).sum();
       double sumXY = 0.0;

       for (int i = 0; i < n; i++) {
           sumXY += x[i] * y[i];
       }

       double denominator = (n * sumX2 - sumX * sumX);
       if (denominator == 0) {
           throw new ArithmeticException("El denominador en el cálculo de la pendiente es cero. No se puede ajustar el modelo.");
       }

       slope = (n * sumXY - sumX * sumY) / denominator;
       intercept = (sumY - slope * sumX) / n;
       fitted = true;
   }

   public double predict(double xValue) {
       if (!fitted) {
           throw new IllegalStateException("El modelo no está ajustado. Llame a fit() antes de hacer predicciones.");
       }
       return intercept + slope * xValue;
   }

   public double getSlope() {
       if (!fitted) {
           throw new IllegalStateException("El modelo no está ajustado. Llame a fit() para calcular la pendiente.");
       }
       return slope;
   }

   public double getIntercept() {
       if (!fitted) {
           throw new IllegalStateException("El modelo no está ajustado. Llame a fit() para calcular el intercepto.");
       }
       return intercept;
   }
}
