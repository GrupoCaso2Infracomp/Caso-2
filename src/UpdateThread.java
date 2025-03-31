import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread que se encarga de actualizar el estado de las páginas cada 1 ms.
 */
class UpdateThread implements Runnable {
    private ManejoMemoria memManager;
    private AtomicBoolean finished;

    public UpdateThread(ManejoMemoria memManager, AtomicBoolean finished) {
        this.memManager = memManager;
        this.finished = finished;
    }

    @Override
    public void run() {
        while (!finished.get()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                break;
            }
            // Ejecuta la actualización del estado (reset de los bits de referencia)
            memManager.updateReferenceBits();
        }
    }
}

