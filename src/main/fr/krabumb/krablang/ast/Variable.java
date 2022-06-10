package fr.krabumb.krablang.ast;

public class Variable extends Token {

    public Variable(String v) {
        super(v);
    }

    @Override
    public String toString() {
        return "Variable : " + this.value;
    }
}
