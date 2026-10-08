import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (RandomAccessFile articulos = new RandomAccessFile("articulos.dat", "rw");
             Scanner leer = new Scanner(System.in)) {

            // Limpia el contenido previo del archivo
            articulos.setLength(0);

            // Encabezado del reporte
            String titulo = "                          ANÁLISIS DE INFLACIÓN";
            String lineaDecorativa = "=".repeat(titulo.length());

            articulos.write((titulo + "\n").getBytes());
            articulos.write((lineaDecorativa + "\n\n").getBytes());

            System.out.println("Ingrese cuántos artículos va a registrar: ");
            int n = leer.nextInt();
            leer.nextLine(); // Limpiar salto de línea

            int[] numArticulo = new int[n];
            String[] descripcion = new String[n];
            double[] pan = new double[n]; // Precio anterior
            double[] pac = new double[n]; // Precio actual
            double[] porcentaje = new double[n];

            // Escribir encabezados de columna en el archivo
            String formatoEncabezado = String.format("%-10s %-20s %-15s %-15s %-15s%n",
                    "Artículo", "Descripción", "Precio anterior", "Precio actual", "PTJE. INFLACIÓN");
            articulos.write(formatoEncabezado.getBytes());

            // Captura de datos
            for (int i = 0; i < n; i++) {
                try {
                    numArticulo[i] = i + 1;
                    System.out.println("Ingrese la descripción del artículo " + (i + 1) + ": ");
                    descripcion[i] = leer.nextLine();

                    System.out.println("Ingrese el precio anterior de " + descripcion[i] + ": ");
                    pan[i] = leer.nextDouble();
                    leer.nextLine();

                    System.out.println("Ingrese el precio actual de " + descripcion[i] + ": ");
                    pac[i] = leer.nextDouble();
                    leer.nextLine();

                    // Cálculo del porcentaje de inflación
                    porcentaje[i] = ((pac[i] - pan[i]) / pan[i]) * 100;

                    // Formato de registro para el archivo
                    String lineaRegistro = String.format("%-10d %-20s $%-14.2f $%-14.2f %.2f%%%n",
                            numArticulo[i], descripcion[i], pan[i], pac[i], porcentaje[i]);
                    articulos.write(lineaRegistro.getBytes());

                } catch (Exception e) {
                    System.err.println("Error al ingresar los datos del artículo " + (i + 1) + ": " + e.getMessage());
                    leer.nextLine();
                    i--; // Reintentar el registro fallido
                }
            }

            // Procesamiento de métricas finales
            double sumaInflacion = 0;
            double maxInflacion = -1;
            int posMayor = 0;

            for (int i = 0; i < n; i++) {
                sumaInflacion += porcentaje[i];
                if (porcentaje[i] > maxInflacion) {
                    maxInflacion = porcentaje[i];
                    posMayor = i;
                }
            }

            double promedioInflacion = n > 0 ? (sumaInflacion / n) : 0;

            // Reporte final en consola y archivo
            String resumen = String.format("%n" + lineaDecorativa + "%n" +
                    "Promedio de Inflación: %.2f%%%n" +
                    "Artículo con mayor inflación: %s (%.2f%%)%n",
                    promedioInflacion, descripcion[posMayor], maxInflacion);

            articulos.write(resumen.getBytes());
            System.out.println(resumen);
            System.out.println("Análisis guardado exitosamente en 'articulos.dat'.");

        } catch (IOException e) {
            System.err.println("Error de Entrada/Salida al manejar el archivo: " + e.getMessage());
        }
    }
}
