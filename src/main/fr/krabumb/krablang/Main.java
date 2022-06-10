package fr.krabumb.krablang;

import fr.krabumb.krablang.analex.AnalyseLexical;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("usage : krablang.jar <file.krl>");
            System.exit(1);
        }
        String contents = readFile(args[0]);
        AnalyseLexical analyseLexical = new AnalyseLexical();
    }

    public static String readFile(String path){
        StringBuilder sb = new StringBuilder();
        String erreur = "Erreur lors de la lecture du fichier";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println(erreur + " : fichier introuvable");
            System.exit(2);
        } catch (IOException e) {
            System.out.println(erreur + " : le fichier n'est pas un fichier textuel");
            System.exit(3);
        }
        return sb.toString();
    }
}
