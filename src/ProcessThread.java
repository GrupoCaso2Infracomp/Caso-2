import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread que simula el proceso que lee las referencias desde el archivo y accede a la memoria.
 */
class ProcessThread implements Runnable {
    private String refFilePath;
    private ManejoMemoria memManager;
    // Para finalizar la lectura cuando se acaben las referencias.
    private AtomicBoolean finished;

    public ProcessThread(String refFilePath, ManejoMemoria memManager, AtomicBoolean finished) {
        this.refFilePath = refFilePath;
        this.memManager = memManager;
        this.finished = finished;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(refFilePath))) {
            // Se leen y descartan las primeras 5 líneas de encabezado: TP, NF, NC, NR, NP
            for (int i = 0; i < 5; i++) {
                reader.readLine();
            }
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                // Cada línea tiene formato: <id>,<nroPagina>,<desplazamiento>,<accion>
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                int virtualPage = Integer.parseInt(parts[1].trim());
                char action = parts[3].trim().charAt(0);
                // Se accede a la página en la memoria.
                memManager.accessPage(virtualPage, action);
                count++;
                // Cada 10,000 referencias se espera 1 ms.
                if (count % 10000 == 0) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        // En caso de interrupción se finaliza.
                        break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            finished.set(true);
        }
    }
}