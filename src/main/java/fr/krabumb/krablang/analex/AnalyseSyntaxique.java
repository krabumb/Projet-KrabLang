package fr.krabumb.krablang.analex;

import fr.krabumb.krablang.ast.Token;
import fr.krabumb.krablang.exceptions.SyntaxException;

import java.util.Iterator;

public class AnalyseSyntaxique {

    private Iterator<Token> tokens;
    private Token currentToken;

    public void analyserProgramme(Iterator<Token> tokens) throws SyntaxException {
        this.tokens = tokens;
        this.nextToken();

        this.analyserAlgorithme();
        this.analyserS();
        this.analyserEOF();
    }

    private void analyserS() throws SyntaxException {
        this.analyserDebut();
        this.analyserBlock();
        this.analyserFin();
    }

    private void analyserBlock() throws SyntaxException {
        if (! this.valeurEquals("fin") && !this.valeurEquals("sinon") && !this.valeurEquals("fsi") && !this.valeurEquals("ftant")) {
            this.analyserBlockInfo();
            this.analyserBlock();
        }
    }

    private void analyserBlockInfo() throws SyntaxException {
        if (this.typeEquals("TYPEPRIM")){
            this.analyserDeclaration();
        } else if (this.typeEquals("TVF")){
            this.analyserTVF();
            this.analyserAssiOuFunc();
        } else if (this.valeurEquals("si")){
            this.analyserSiFsi();
        } else if (this.valeurEquals("tant")){
            this.analyserTantQue();
        } else {
            throw new SyntaxException(String.format("ERREUR : Syntaxe inconnue (%d,%d), attendu : (Déclaration, assignation, fonction, si, tant que), obtenu : (%s)",this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserTantQue() throws SyntaxException {
        this.analyserTant();
        this.analyserQue();
        this.analyserExpr();
        this.analyserBlock();
        this.analyserFtant();
    }

    private void analyserSiFsi() throws SyntaxException {
        this.analyserSi();
        this.analyserExpr();
        this.analyserBlock();
        this.analyserSinonOuPas();
        this.analyserFsi();
    }

    private void analyserSinonOuPas() throws SyntaxException {
        if (! this.valeurEquals("fsi")){
            this.analyserSinon();
            this.analyserBlock();
        }
    }

    private void analyserAssiOuFunc() throws SyntaxException {
        if (this.valeurEquals("=")){
            this.analyserAssignation();
        } else if (this.valeurEquals("(")){
            this.analyserFunctionExe();
        } else {
            throw new SyntaxException(String.format("ERREUR : Assignation ou fonction attendue (%d,%d), attendu : '(' ou (=), obtenu : %s", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserFunctionExe() throws SyntaxException {
        this.analyserParentheseO();
        this.analyserArgument();
        this.analyserParentheseF();
        this.analyserEOI();
    }

    private void analyserArgument() throws SyntaxException {
        if (!this.valeurEquals(")")){
            this.analyserArgumentN();
        }
    }

    private void analyserArgumentN() throws SyntaxException {
        this.analyserExpr();
        this.analyserArgumentG();
    }

    private void analyserArgumentG() throws SyntaxException {
        if (!this.valeurEquals(")")){
            this.analyserVirgule();
            this.analyserArgumentN();
        }
    }

    private void analyserDeclaration() throws SyntaxException {
        this.analyserTypePrim();
        this.analyserTVF();
        this.analyserDeclaPoss();
    }

    private void analyserDeclaPoss() throws SyntaxException {
        if (this.valeurEquals(";")){
            this.analyserEOI();
        } else if (this.valeurEquals("=")){
            this.analyserAssignation();
        } else {
            throw new SyntaxException(String.format("Erreur : Déclaration attendue (%d,%d), attendu : (=) ou (;), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserAssignation() throws SyntaxException {
        this.analyserEgal();
        this.analyserExpr();
        this.analyserEOI();
    }

    private void analyserExpr() throws SyntaxException {
        if (this.valeurEquals("(")){
            this.analyserParentheseO();
            this.analyserExpr();
            this.analyserParentheseF();
        } else if (this.valeurEquals("non") || this.typeEquals("PLUSMINUS")){
            this.analyserUnaOperator();
            this.analyserExpr();
        } else if (this.typeEquals("ENTIER") || this.typeEquals("DECIMAL") || this.typeEquals("TVF") || this.typeEquals("BOOLEAN") || this.typeEquals("STRING") || this.valeurEquals("null")){
            this.analyserOperand();
        } else {
            throw new SyntaxException(String.format("Erreur : Expression attendue (%d,%d), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
        if (this.typeEquals("PLUSMINUS") || this.typeEquals("COMPMATHOP") || this.typeEquals("BOOLOP") || this.typeEquals("INFSUP") || this.valeurEquals("=") || this.valeurEquals("non")){
            this.analyserBinOperator();
            this.analyserExpr();
        }
    }

    private void analyserBinOperator() throws SyntaxException {
        if (this.typeEquals("PLUSMINUS") || this.typeEquals("COMPMATHOP") || this.typeEquals("BOOLOP")) {
            this.analyserOperator();
        } else if (this.valeurEquals("non") || this.valeurEquals("=")){
            this.analyserOpegal();
            this.analyserEgal();
        } else if (this.typeEquals("INFSUP")){
            this.analyserInfSup();
            this.analyserInfSupOp();
        } else {
            throw new SyntaxException(String.format("Erreur : Opérateur binaire attendu (%d,%d), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserInfSupOp() throws SyntaxException {
        if (!this.valeurEquals("(") && !this.valeurEquals("non") && !this.typeEquals("PLUSMINUS") && !this.typeEquals("ENTIER") && !this.typeEquals("DECIMAL") && !this.typeEquals("TVF") && !this.typeEquals("BOOLEAN") && !this.typeEquals("STRING") && !this.valeurEquals("null")){
            this.analyserEgal();
        }
    }

    private void analyserOpegal() throws SyntaxException {
        if (this.valeurEquals("non")){
            this.analyserNon();
        } else if (this.valeurEquals("=")){
            this.analyserEgal();
        } else {
            throw new SyntaxException(String.format("Erreur : Opérateur attendu (%d,%d), attendu : (non) ou (=), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserOperator() throws SyntaxException {
        if (this.typeEquals("PLUSMINUS")){
            this.analyserPlusMinus();
        } else if (this.typeEquals("COMPMATHOP")){
            this.analyserCompMathOp();
        } else if (this.typeEquals("BOOLOP")){
            this.analyserBoolOp();
        } else {
            throw new SyntaxException(String.format("Erreur : Opérateur binaire attendu (%d,%d), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserOperand() throws SyntaxException {
        if (this.typeEquals("ENTIER") || this.typeEquals("DECIMAL")){
            this.analyserNumber();
        } else if (this.typeEquals("TVF")){
            this.analyserTVF();
            this.analyserOperandFunc();
        } else if (this.typeEquals("BOOLEAN")) {
            this.analyserBoolean();
        } else if (this.typeEquals("STRING")) {
            this.analyserString();
        } else if (this.valeurEquals("null")) {
            this.analyserNull();
        } else {
            throw new SyntaxException(String.format("Erreur : Opérande attendue (%d,%d), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserOperandFunc() throws SyntaxException {
        if (!this.valeurEquals(")") && !this.typeEquals("PLUSMINUS") && !this.typeEquals("COMPMATHOP") && !this.typeEquals("BOOLOP") && !this.typeEquals("INFSUP") && !this.valeurEquals("non") && !this.valeurEquals("=") && !this.valeurEquals("fin") && !this.valeurEquals("fsi") && !this.valeurEquals("ftant") && !this.valeurEquals(";") && !this.typeEquals("TYPEPRIM") && !this.typeEquals("TVF") && !this.valeurEquals("si") && !this.valeurEquals("tant")){
            this.analyserFunctionExe();
        }
    }

    private void analyserNumber() throws SyntaxException {
        if (this.typeEquals("ENTIER")){
            analyserEntier();
        } else if (this.typeEquals("DECIMAL")){
            analyserDecimal();
        } else {
            throw new SyntaxException(String.format("Erreur : Nombre entier ou décimal attendu (%d,%d), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserUnaOperator() throws SyntaxException {
        if (this.valeurEquals("non")){
            this.analyserNon();
        } else if (this.typeEquals("PLUSMINUS")) {
            this.analyserPlusMinus();
        } else {
            throw new SyntaxException(String.format("ERREUR : opérateur unitaire attendu (%d,%d), attendu : (non) ou (+) ou (-), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), this.currentToken.getValue()));
        }
    }

    private void analyserVirgule() throws SyntaxException {
        this.analyserValeurToken(",");
        this.nextToken();
    }

    private void analyserInfSup() throws SyntaxException {
        this.analyserTypeToken("INFSUP");
        this.nextToken();
    }

    private void analyserCompMathOp() throws SyntaxException {
        this.analyserTypeToken("COMPMATHOP");
        this.nextToken();
    }

    private void analyserBoolOp() throws SyntaxException {
        this.analyserTypeToken("BOOLOP");
        this.nextToken();
    }


    private void analyserNull() throws SyntaxException {
        this.analyserValeurToken("null");
        this.nextToken();
    }

    private void analyserBoolean() throws SyntaxException {
        this.analyserTypeToken("BOOLEAN");
        this.nextToken();
    }

    private void analyserString() throws SyntaxException {
        this.analyserTypeToken("STRING");
        this.nextToken();
    }

    private void analyserEntier() throws SyntaxException {
        this.analyserTypeToken("ENTIER");
        this.nextToken();
    }

    private void analyserDecimal() throws SyntaxException {
        this.analyserTypeToken("DECIMAL");
        this.nextToken();
    }

    private void analyserPlusMinus() throws SyntaxException {
        this.analyserTypeToken("PLUSMINUS");
        this.nextToken();
    }

    private void analyserNon() throws SyntaxException {
        this.analyserValeurToken("non");
        this.nextToken();
    }

    private void analyserTant() throws SyntaxException {
        this.analyserValeurToken("tant");
        this.nextToken();
    }

    private void analyserQue() throws SyntaxException {
        this.analyserValeurToken("que");
        this.nextToken();
    }

    private void analyserFtant() throws SyntaxException {
        this.analyserValeurToken("ftant");
        this.nextToken();
    }

    private void analyserSinon() throws SyntaxException {
        this.analyserValeurToken("sinon");
        this.nextToken();
    }

    private void analyserFsi() throws SyntaxException {
        this.analyserValeurToken("fsi");
        this.nextToken();
    }

    private void analyserSi() throws SyntaxException {
        this.analyserValeurToken("si");
        this.nextToken();
    }

    private void analyserParentheseO() throws SyntaxException {
        this.analyserValeurToken("(");
        this.nextToken();
    }

    private void analyserParentheseF() throws SyntaxException {
        this.analyserValeurToken(")");
        this.nextToken();
    }

    private void analyserEgal() throws SyntaxException {
        this.analyserValeurToken("=");
        this.nextToken();
    }

    private void analyserEOI() throws SyntaxException {
        this.analyserValeurToken(";");
        this.nextToken();
    }

    private void analyserTypePrim() throws SyntaxException {
        this.analyserTypeToken("TYPEPRIM");
        this.nextToken();
    }

    private void analyserAlgorithme() throws SyntaxException {
        this.analyserValeurToken("Algorithme");
        this.nextToken();
    }

    private void analyserEOF() throws SyntaxException {
        this.analyserTypeToken("EOF");
    }

    private void analyserDebut() throws SyntaxException {
        this.analyserValeurToken("debut");
        this.nextToken();
    }

    private void analyserFin() throws SyntaxException {
        this.analyserValeurToken("fin");
        this.nextToken();
    }

    private void analyserTVF() throws SyntaxException {
        this.analyserTypeToken("TVF");
        this.nextToken();
    }

    private boolean valeurEquals(String valeur) {
        return this.currentToken.getValue().equals(valeur);
    }

    private boolean typeEquals(String type) {
        return this.currentToken.getType().equals(type);
    }

    private void analyserTypeToken(String type) throws SyntaxException {
        if (! this.currentToken.getType().equals(type)){
            throw new SyntaxException(String.format("ERREUR : Le type attendu est invalide (%d:%d), attendu : (%s), obtenu : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), type, this.currentToken.getType()));
        }
    }

    private void analyserValeurToken(String valeur) throws SyntaxException {
        if (! this.currentToken.getValue().equals(valeur)){
            throw new SyntaxException(String.format("ERREUR : La valeur attendue est invalide (%d:%d), attendue : (%s), obtenue : (%s)", this.currentToken.getLineNumber(), this.currentToken.getCharNumber(), valeur, this.currentToken.getValue()));
        }
    }

    private void nextToken() {
        this.currentToken = this.tokens.hasNext() ? this.tokens.next() : new Token("EOF", "", -1, -1);
    }
}
