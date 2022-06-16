package fr.krabumb.krablang.analex;

import fr.krabumb.krablang.ast.Token;
import fr.krabumb.krablang.exceptions.LexicalException;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Stream;

public class AnalyseLexical {

    public static final int RIEN = 0;
    public static final int ENTIER = 1;
    public static final int DECIMAL = 2;
    public static final int TVF = 3;
    public static final int STRING = 4;

    public Stream<Token> generateStreamToken(String file) throws LexicalException {
        ArrayList<Token> tokens = new ArrayList<>();
        int state = RIEN;
        int ln = 1;
        int cn = 1;
        StringBuilder currentToken = new StringBuilder();
        for (char c : file.toCharArray()) {
            if (c == '\n') {
                ln += 1;
                cn = 1;
            } else {
                cn += 1;
            }
            switch (state) {
                case RIEN:
                    if (!Character.isWhitespace(c)) {
                        if (Character.isLetterOrDigit(c)) {
                            if (Character.isDigit(c)) {
                                state = ENTIER;
                            } else {
                                state = TVF;
                            }
                            currentToken.append(c);
                        } else if (c == '"') {
                            state = STRING;
                        } else {
                            tokens.add(getTokenFromSymbol(c, ln, cn));
                        }
                    }
                    break;
                case ENTIER:
                    if (!Character.isWhitespace(c)) {
                        if (Character.isLetterOrDigit(c)) {
                            if (Character.isDigit(c)) {
                                currentToken.append(c);
                            } else {
                                throw new LexicalException(String.format("ERREUR : nombre non valide (%d:%d)", ln, cn));
                            }
                        } else if (c == '.') {
                            state = DECIMAL;
                            currentToken.append(c);
                        } else {
                            tokens.add(new Token("ENTIER", currentToken.toString(), ln, cn));
                            currentToken.delete(0, currentToken.length());
                            state = RIEN;
                            tokens.add(getTokenFromSymbol(c, ln, cn));
                        }
                    } else {
                        tokens.add(new Token("ENTIER", currentToken.toString(), ln, cn));
                        currentToken.delete(0, currentToken.length());
                        state = RIEN;
                    }
                    break;
                case DECIMAL:
                    if (!Character.isWhitespace(c)) {
                        if (Character.isLetterOrDigit(c)) {
                            if (Character.isDigit(c)) {
                                currentToken.append(c);
                            } else {
                                throw new LexicalException(String.format("ERREUR : nombre decimal non valide (%d:%d)", ln, cn));
                            }
                            currentToken.append(c);
                        } else {
                            tokens.add(new Token("DECIMAL", currentToken.toString(), ln, cn));
                            currentToken.delete(0, currentToken.length());
                            state = RIEN;
                            tokens.add(getTokenFromSymbol(c, ln, cn));
                        }
                    } else {
                        tokens.add(new Token("DECIMAL", currentToken.toString(), ln, cn));
                        currentToken.delete(0, currentToken.length());
                        state = RIEN;
                    }
                    break;
                case TVF:
                    if (!Character.isWhitespace(c)) {
                        if (Character.isLetterOrDigit(c) || c == '_') {
                            currentToken.append(c);
                        } else {
                            tokens.add(getTokenFromTVF(currentToken.toString(), ln, cn));
                            currentToken.delete(0, currentToken.length());
                            state = RIEN;
                            tokens.add(getTokenFromSymbol(c, ln, cn));
                        }
                    } else {
                        tokens.add(getTokenFromTVF(currentToken.toString(), ln, cn));
                        currentToken.delete(0, currentToken.length());
                        state = RIEN;
                    }
                    break;
                case STRING:
                    if (c == '\n') {
                        throw new LexicalException(String.format("ERREUR : Chaine de caractere non fermee (%d:%d)", ln, cn));
                    } else if (c != '"') {
                        currentToken.append(c);
                    } else {
                        tokens.add(new Token("STRING", currentToken.toString(), ln, cn));
                        currentToken.delete(0, currentToken.length());
                        state = RIEN;
                    }
                    break;
            }
        }
        return tokens.stream();
    }

    public Token getTokenFromSymbol(char c, int ln, int cn) throws LexicalException {
        String type;
        switch (c) {
            case '=':
                type = "EGAL";
                break;
            case '/':
            case '*':
            case '%':
                type = "COMPMATHOP";
                break;
            case '<':
            case '>':
                type = "INFSUP";
                break;
            case '-':
                type = "MINUS";
                break;
            case '+':
                type = "PLUS";
                break;
            case '(':
                type = "PARENTHESEO";
                break;
            case ')':
                type = "PARENTHESEF";
                break;
            case ';':
                type = "EOI";
                break;
            case ',':
                type = "VIRGULE";
                break;
            default:
                throw new LexicalException(String.format("ERREUR: symbole non autorisé(%d:%d) : %c", ln, cn, c));
        }
        return new Token(type, Character.toString(c), ln, cn);
    }

    public Token getTokenFromTVF(String str, int ln, int cn) {
        String type;
        switch (str) {
            case "Algorithme":
            case "debut":
            case "fin":
            case "null":
            case "non":
            case "si":
            case "sinon":
            case "fsi":
            case "tantque":
            case "ftant":
                type = str.toUpperCase(Locale.ROOT);
                break;
            case "true":
            case "false":
                type = "BOOLEAN";
                break;
            case "entier":
            case "decimal":
            case "chaine":
            case "booleen":
                type = "TYPEPRIM";
                break;
            case "et":
            case "ou":
                type = "BOOLOP";
                break;
            default:
                type = "TVF";
                break;
        }
        return new Token(type, str, ln, cn);
    }
}
