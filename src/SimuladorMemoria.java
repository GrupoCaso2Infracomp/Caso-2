import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase principal para la opción 2.
 * Recibe como parámetros: número de marcos y nombre del archivo de referencias.
 */
public class SimuladorMemoria {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el número de marcos de página: ");
        int numMarcos = Integer.parseInt(scanner.nextLine());
        System.out.print("Ingrese el nombre del archivo de referencias: ");
        String refFile = scanner.nextLine();

        // Crear el objeto MemoryManager con los marcos asignados.
        ManejoMemoria memManager = new ManejoMemoria(numMarcos);
        AtomicBoolean finished = new AtomicBoolean(false);

        // Crear y arrancar los threads.
        Thread processThread = new Thread(new ProcessThread(refFile, memManager, finished));
        Thread updateThread = new Thread(new UpdateThread(memManager, finished));

        long startTime = System.nanoTime();
        processThread.start();
        updateThread.start();

        // Esperar a que el thread de proceso termine.
        try {
            processThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        // Luego, se interrumpe el thread de actualización.
        updateThread.interrupt();
        long endTime = System.nanoTime();

        // Recolectar resultados de la simulación.
        long hits = memManager.getHits();
        long faults = memManager.getFaults();
        long totalRefs = memManager.getTotalReferences();
        long simulatedTimeNS = memManager.getSimulatedTimeNS();

        // Tiempos teóricos:
        long timeAllHits = totalRefs * 50;          // 50 ns por acceso
        long timeAllFaults = totalRefs * 10_000_000;  // 10 ms (10,000,000 ns) por acceso

        // Mostrar resultados:
        System.out.println("----- Resultados de la Simulación -----");
        System.out.println("Total de referencias: " + totalRefs);
        System.out.println("Hits: " + hits);
        System.out.println("Fallas de página: " + faults);
        double porcentajeHits = (totalRefs > 0) ? ((double)hits / totalRefs) * 100 : 0;
        System.out.printf("Porcentaje de hits: %.2f%%\n", porcentajeHits);
        System.out.println();
        System.out.println("Tiempo simulado (según accesos): " + simulatedTimeNS + " ns");
        System.out.println("Tiempo si todas las referencias estuvieran en RAM: " + timeAllHits + " ns");
        System.out.println("Tiempo si todas las referencias generaran fallas: " + timeAllFaults + " ns");
        System.out.println();
        long realTimeElapsed = endTime - startTime;
        System.out.println("Tiempo real de ejecución (aprox): " + realTimeElapsed / 1_000_000 + " ms");

        scanner.close();
    }
}
