package fr.krabumb.krablang.ast;

import java.util.ArrayList;

public class AST {

    private ArrayList<Token> tokens;

    public AST(){
        this.tokens = new ArrayList<Token>();
    }

    public void addToken(Token t){
        this.tokens.add(t);
    }
}
