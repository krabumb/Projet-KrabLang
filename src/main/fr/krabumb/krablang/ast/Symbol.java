package fr.krabumb.krablang.ast;

public class Symbol extends Token {

    public Symbol(String v) {
        super(v);
    }

    @Override
    public String toString() {
        return "Symbol : " + this.value;
    }
}
