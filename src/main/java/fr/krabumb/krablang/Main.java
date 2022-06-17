package fr.krabumb.krablang;

import fr.krabumb.krablang.analex.AnalyseLexical;
import fr.krabumb.krablang.ast.Token;
import fr.krabumb.krablang.exceptions.LexicalException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage : krablang.jar <file.krbl>");
            System.exit(1);
        } else if (! args[0].endsWith("krbl")){
            System.out.println("Erreur : le fichier ne se termine pas par '.krbl' ");
            System.exit(11);
        }
        String contents = readFile(args[0]);
        AnalyseLexical analyseLexical = new AnalyseLexical();
        try {
            Stream<Token> tokens = analyseLexical.generateStreamToken(contents);
            tokens.forEach((token) -> System.out.println(token.getType() + " : " + token.getValue()));
        } catch (LexicalException e) {
            e.printStackTrace();
            System.exit(2);
        }
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
            System.out.println(erreur + " : le fichier est illisible");
            System.exit(3);
        }
        return sb.toString();
    }
}
