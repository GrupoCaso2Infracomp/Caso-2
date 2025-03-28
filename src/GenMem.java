import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenMem {

    public static void genMem(String imagenPath, int tamanoPagina, String refPath) throws IOException{

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

        BufferedWriter writer = new BufferedWriter(new FileWriter(refPath));
        writer.write("TP=" + tamanoPagina + "\n");
        writer.write("NF=" + f + "\n");
        writer.write("NC=" + c + "\n");


        List<String> referencias = new ArrayList<>();
        int numReferencias = 0;

        for (int x = 0; x < f; x++) {
            for (int y = 0; y < c; y++) {
                for (int z = 0; z < 3; z++) {
                    des = (x * c * 3 + y * 3 + z) % tamanoPagina;
                    pg = (x * c * 3 + y * 3 + z) / tamanoPagina;
                    String color = "";
                    if (z == 0) {
                        color = "r";
                    } else if (z == 1) {
                        color = "g";
                    } else {
                        color = "b";
                    }
                    referencias.add("Imagen[" + x + "][" + y + "]." + color + "," + pg + "," + des + ",R");
                }
            }
        }

        //datos sobel
        int oft = f * c * 3;  
        int pgS = oft / tamanoPagina;
        int desplazamiento = oft % tamanoPagina;
        for (String filtro : new String[]{"SOBEL_X", "SOBEL_Y"}) { //filtro sobel
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    for(int k = 0; k < 3; k++)
                        referencias.add(filtro + "[" + i + "][" + j + "]," + pgS + "," + desplazamiento + ",R");
                    desplazamiento += 4;
                    if (desplazamiento >= tamanoPagina) {
                        pgS++;
                        desplazamiento = 0;
                    }
                    numReferencias++;
                }
            }
        } // fin filtro sobel
        
        //ref rta
        for (int x = 0; x < f; x++) {
            for (int y = 0; y < c; y++) {
                for (int z = 0; z < 3; z++) {
                    des = ((x * c * 3 + y * 3 + z) % tamanoPagina) + desplazamiento;
                    pg = ((x * c * 3 + y * 3 + z) / tamanoPagina) + pgS;
                    String color = "";
                    if (z == 0) {
                        color = "r";
                    } else if (z == 1) {
                        color = "g";
                    } else {
                        color = "b";
                    }
                    referencias.add("Rta[" + x + "][" + y + "]." + color + "," + pg + "," + des + ",W");
                }
            }
        }
        writer.write("NR=" + numReferencias + "\n");
        writer.write("NP=" + numPaginas + "\n");
        for (String ref : referencias) {
            writer.write(ref + "\n");
        }
        writer.close();
        System.out.println("Archivo de referencias generado: " + refPath);
    }

        

}
