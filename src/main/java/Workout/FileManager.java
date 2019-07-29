package Workout;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public List<String> getTelephones() {

        List<String> tels = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    Process.pathListadoIps));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                tels.add(line);
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("No se ha encontrado el archivo " + Process.pathListadoIps);
        }
        return tels;
    }
}
