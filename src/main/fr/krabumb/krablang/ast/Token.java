package fr.krabumb.krablang.ast;

public abstract class Token {

    protected String value;

    public Token(String v){
        this.value = v;
    }

    public String getValue(){
        return this.value;
    }

    @Override
    public abstract String toString();
}
