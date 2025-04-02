import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("----- Menú Principal -----");
            System.out.println("1. Opción 1: Generación de las referencias.");
            System.out.println("2. Opción 2: Calcular datos buscados: número de fallas de página, porcentaje de hits, tiempos.");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    generarReferencias(scanner);
                    break;
                case 2:
                    simularMemoria(scanner);
                    break;
                case 3:
                    System.out.println("Saliendo del programa...");
                    scanner.close();
                    return; // Termina el programa
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private static void generarReferencias(Scanner scanner) {
        String refPath = "referencias.txt";

        System.out.print("Ingrese el tamaño de página: ");
        int tamanoPagina = Integer.parseInt(scanner.nextLine());

        System.out.print("Ingrese el nombre del archivo de la imagen: ");
        String imgPath = "datos/" + scanner.nextLine();

        try {
            GenRef.genRef(imgPath, tamanoPagina, refPath);
            System.out.println("Referencias generadas exitosamente en: " + refPath);
        } catch (Exception e) {
            System.err.println("Error al generar las referencias: " + e.getMessage());
        }
    }

    private static void simularMemoria(Scanner scanner) {
        System.out.print("Ingrese el número de marcos de página: ");
        int numMarcos = Integer.parseInt(scanner.nextLine());
        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = scanner.nextLine();

        ManejoMemoria memManager = new ManejoMemoria(numMarcos);
        AtomicBoolean finished = new AtomicBoolean(false);

        Thread processThread = new Thread(new ProcessThread(refFile, memManager, finished));
        Thread updateThread = new Thread(new UpdateThread(memManager, finished));

        long startTime = System.nanoTime();
        processThread.start();
        updateThread.start();

        try {
            processThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        updateThread.interrupt();
        long endTime = System.nanoTime();

        long hits = memManager.getHits();
        long faults = memManager.getFaults();
        long totalRefs = memManager.getTotalReferences();
        long simulatedTimeNS = memManager.getSimulatedTimeNS();

        long timeAllHits = totalRefs * 50;
        long timeAllFaults = totalRefs * 10_000_000;

        System.out.println("----- Resultados de la Simulación -----");
        System.out.println("Total de referencias: " + totalRefs);
        System.out.println("Hits: " + hits);
        System.out.println("Fallas de página: " + faults);
        double porcentajeHits = (totalRefs > 0) ? ((double) hits / totalRefs) * 100 : 0;
        System.out.printf("Porcentaje de hits: %.2f%%\n", porcentajeHits);
        System.out.println();
        System.out.println("Tiempo simulado (según accesos): " + simulatedTimeNS + " ns");
        System.out.println("Tiempo si todas las referencias estuvieran en RAM: " + timeAllHits + " ns");
        System.out.println("Tiempo si todas las referencias generaran fallas: " + timeAllFaults + " ns");
        System.out.println();
        long realTimeElapsed = endTime - startTime;
        System.out.println("Tiempo real de ejecución (aprox): " + realTimeElapsed / 1_000_000 + " ms");
    }
}
