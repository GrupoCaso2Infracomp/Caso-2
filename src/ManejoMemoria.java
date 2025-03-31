
/**
 * Clase que simula el manejo de memoria virtual.
 * Implementa el algoritmo “páginas no usadas recientemente” (similar al algoritmo Clock).
 */
class ManejoMemoria {
    // Clase interna para representar un marco de página.
    public static class Frame {
        int virtualPage;      // Número de página virtual cargada
        boolean reference;    // Bit de referencia (R)
        boolean occupied;     // Indica si el marco está ocupado

        public Frame() {
            this.virtualPage = -1;
            this.reference = false;
            this.occupied = false;
        }
    }

    private Frame[] frames;         // Arreglo de marcos disponibles en RAM
    private int numFrames;          // Número total de marcos asignados
    private int clockHand = 0;      // Puntero para el algoritmo Clock
    // Contadores de simulación
    private long hits = 0;
    private long faults = 0;
    private long totalReferences = 0;

    // Tiempo acumulado (en nanosegundos) de la simulación real
    private long simulatedTimeNS = 0;

    // Constantes de tiempos (convertir 10ms a ns)
    private final long TIME_HIT = 50; // 50 ns
    private final long TIME_FAULT = 10_000_000; // 10 ms = 10,000,000 ns

    public ManejoMemoria(int numFrames) {
        this.numFrames = numFrames;
        this.frames = new Frame[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = new Frame();
        }
    }

    // Método sincronizado para acceder a una página virtual.
    // Devuelve true si es hit, false si es falta de página.
    public synchronized boolean accessPage(int virtualPage, char action) {
        totalReferences++;

        // Verificar si la página ya está cargada en alguno de los marcos.
        for (Frame frame : frames) {
            if (frame.occupied && frame.virtualPage == virtualPage) {
                // Hit: se marca como referenciada y se suma el tiempo de acceso en RAM.
                frame.reference = true;
                simulatedTimeNS += TIME_HIT;
                hits++;
                return true;
            }
        }
        // Miss: falta de página
        faults++;
        simulatedTimeNS += TIME_FAULT;

        // Intentar buscar un marco libre.
        for (Frame frame : frames) {
            if (!frame.occupied) {
                frame.virtualPage = virtualPage;
                frame.occupied = true;
                frame.reference = true;
                return false;
            }
        }
        // Si no hay marco libre, se aplica el algoritmo Clock:
        while (true) {
            Frame current = frames[clockHand];
            if (!current.reference) {
                // Se reemplaza esta página.
                current.virtualPage = virtualPage;
                // Al cargar la nueva página, se marca el bit de referencia.
                current.reference = true;
                // Avanzar el puntero para la próxima búsqueda.
                clockHand = (clockHand + 1) % numFrames;
                break;
            } else {
                // Si el bit de referencia está en true, se resetea y se avanza.
                current.reference = false;
                clockHand = (clockHand + 1) % numFrames;
            }
        }
        return false;
    }

    // Método para que el thread de actualización resetee los bits de referencia.
    // Este método recorre todos los marcos y pone en false el bit de referencia.
    public synchronized void updateReferenceBits() {
        for (Frame frame : frames) {
            // Se resetea el bit; la idea es que solo se mantenga en true si la página fue referenciada recientemente.
            frame.reference = false;
        }
    }

    // Getters para obtener los resultados de la simulación.
    public synchronized long getHits() {
        return hits;
    }

    public synchronized long getFaults() {
        return faults;
    }

    public synchronized long getTotalReferences() {
        return totalReferences;
    }

    // Tiempo simulado en nanosegundos
    public synchronized long getSimulatedTimeNS() {
        return simulatedTimeNS;
    }
}
