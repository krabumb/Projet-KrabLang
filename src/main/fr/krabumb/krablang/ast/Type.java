package fr.krabumb.krablang.ast;

public class Type extends Token {

    public Type(String v) {
        super(v);
    }

    @Override
    public String toString() {
        return "Type : " + this.value;
    }
}
