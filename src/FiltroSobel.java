public class FiltroSobel {
    Imagen imagenIn;
    Imagen imagenOut;

    FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida){
        imagenIn = imagenEntrada;
        imagenOut = imagenSalida;
    }

    // Sobel Kernels para detección de bordes
    static final int[][] SOBEL_X = {
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}
    };

    static final int[][] SOBEL_Y = {
        {-1, -2, -1},
        { 0, 0, 0},
        { 1, 2, 1}
    };

    /**
    * Método para para aplicar el filtro de Sobel a una imagen BMP
    * @pre la matriz imagenIn debe haber sido inicializada con una imagen
    * @pos la mstriz imagenOut fue modificada aplicando el filtro Sobel
    */
    public void applySobel() {
        // Recorrer la imagen aplicando los dos filtros de Sobel
        for (int i = 1; i < imagenIn.alto - 1; i++) {
            for (int j = 1; j < imagenIn.ancho - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;
                // Aplicar las máscaras Sobel X y Y
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        int red = imagenIn.imagen[i+ki][j+kj][0];
                        int green = imagenIn.imagen[i+ki][j+kj][1];
                        int blue = imagenIn.imagen[i+ki][j+kj][2];
                        gradXRed += red * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue += blue * SOBEL_X[ki + 1][kj + 1];
                        gradYRed += red * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue += blue * SOBEL_Y[ki + 1][kj + 1];
                    }
                }
                // Calcular la magnitud del gradiente
                int red = Math.min(Math.max((int)Math.sqrt(gradXRed * gradXRed+
                gradYRed * gradYRed),0),255);
                int green = Math.min(Math.max((int)Math.sqrt(gradXGreen * gradXGreen +
                gradYGreen* gradYGreen),0),255);
                int blue = Math.min(Math.max((int) Math.sqrt(gradXBlue * gradXBlue +
                gradYBlue * gradYBlue),0),255);
                // Crear el nuevo valor RGB
                imagenOut.imagen[i][j][0] = (byte)red;
                imagenOut.imagen[i][j][1] = (byte)green;
                imagenOut.imagen[i][j][2] = (byte)blue;

                if(i == 1 && j == 1){
                    System.out.println(imagenOut.imagen[i][j][0]);
                }   
            }
            
        }
    }
}