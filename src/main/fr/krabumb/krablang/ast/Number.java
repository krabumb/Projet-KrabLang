package fr.krabumb.krablang.ast;

public class Number extends Token{

    public Number(String v) {
        super(v);
    }

    @Override
    public String toString() {
        return "Number : " + this.value;
    }
}
