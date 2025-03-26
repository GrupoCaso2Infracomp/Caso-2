import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        try (Scanner scanner = new Scanner(System.in)) {
            String refPath = "referencias.txt";

            System.out.print("Ingrese el tamaño de página: ");
            int tamanoPagina = Integer.parseInt(scanner.nextLine());
   
            System.out.print("Ingrese el nombre del archivo de la imagen: ");
            String imgPath = "datos/" + scanner.nextLine();

            try {
                GenRef.genRef(imgPath, tamanoPagina, refPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
