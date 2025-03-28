import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenRef {

    public static void genRef(String imagenPath, int tamanoPagina, String refPath) throws IOException{

        
        Imagen imagen = new Imagen(imagenPath);

        //datos 
        int des = 0;
        int pg = 0;
        int f = imagen.alto;
        int c = imagen.ancho;
        int tamImagen = f * c * 3;
        int tamRespuesta = tamImagen;
        int tamFiltro = 3 * 3 * 4;
        int totalBytes = tamImagen + tamFiltro * 2 + tamRespuesta;
        int numPaginas = (int) Math.ceil((double) totalBytes / tamanoPagina);

        //datos img og
        int tamImagenOg = f * c * 3; // RGB
        int numPaginasOg = (int) Math.ceil((double) tamImagenOg / tamanoPagina);
        System.out.println(numPaginasOg);

        //datos filtro
        int oftFiltro = f * c * 3;  
        int pgFiltroX = oftFiltro / tamanoPagina;
        int desFiltroX = oftFiltro % tamanoPagina;
        int oftFiltroY = oftFiltro + (3 * 3 * 4); 
        int pgFiltroY = oftFiltroY / tamanoPagina;
        int desFiltroY = oftFiltroY % tamanoPagina;

        //datos nueva imagen
        int oftNuevaImagen = oftFiltroY + (3 * 3 * 4); 
        int pgNuevaImagen = oftNuevaImagen / tamanoPagina;
        int desNuevaImagen = oftNuevaImagen % tamanoPagina;


        BufferedWriter writer = new BufferedWriter(new FileWriter(refPath));

        List<String> referencias = new ArrayList<>();

        
        for (int i = 1; i < imagen.alto - 1; i++) { //fila imagen
            for (int j = 1; j < imagen.ancho - 1; j++) { 
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {
                        for (int z = 0; z < 3; z++) {
                            des = ((i + ki) * c * 3 + (j + kj) * 3 + z) % tamanoPagina;
                            pg = ((i + ki) * c * 3 + (j + kj) * 3 + z) / tamanoPagina;
                            String color = "";
                            if (z == 0) {
                                color = "r";
                            } else if (z == 1) {
                                color = "g";
                            } else {
                                color = "b";
                            }
                            referencias.add("Imagen[" + (i + ki) + "][" + (j + kj) + "]." + color + "," + pg + "," + des + ",R");
                        }
                        for(int k = 0; k < 3; k++){
                            referencias.add("SOBEL_X[" + (ki + 1) + "][" + (kj + 1) + "]," + pgFiltroX + "," + desFiltroX + ",R");
                        }
                        for(int k = 0; k < 3; k++){
                            referencias.add("SOBEL_Y[" + (ki + 1) + "][" + (kj + 1) + "]," + pgFiltroY + "," + desFiltroY + ",R");
                        }
                        desFiltroX += 4;
                        desFiltroY += 4;
                        if (desFiltroX >= tamanoPagina) {
                            pgFiltroX++;
                            desFiltroX = 0;
                        } 
                        if (desFiltroY >= tamanoPagina) {
                            pgFiltroY++;
                            desFiltroY = 0;
                        } 
                    }
                }
                oftFiltro = f * c * 3;  
                pgFiltroX = oftFiltro / tamanoPagina;
                desFiltroX = oftFiltro % tamanoPagina;
                oftFiltroY = oftFiltro + (3 * 3 * 4); 
                pgFiltroY = oftFiltroY / tamanoPagina;
                desFiltroY = oftFiltroY % tamanoPagina;
                for (int z = 0; z < 3; z++) { // nueva imagen
                    int desRta = ((i * c * 3 + j * 3 + z) % tamanoPagina) + desNuevaImagen;
                    int pgRta = ((i * c * 3 + j * 3 + z) / tamanoPagina) + pgNuevaImagen;
                    String color = "";
                    if (z == 0) {
                        color = "r";
                    } else if (z == 1) {
                        color = "g";
                    } else {
                        color = "b";
                    }
                    referencias.add("Rta[" + i + "][" + j + "]." + color + "," + pgRta + "," + desRta + ",W");
                }
            }
        }

        writer.write("TP=" + tamanoPagina + "\n");
        writer.write("NF=" + f + "\n");
        writer.write("NC=" + c + "\n");
        writer.write("NR=" + referencias.size() + "\n");
        writer.write("NP=" + numPaginas + "\n");

        for (String ref : referencias) {
            writer.write(ref + "\n");
        }

        writer.close();
        System.out.println("Archivo de referencias generado: " + refPath);    
    }

}