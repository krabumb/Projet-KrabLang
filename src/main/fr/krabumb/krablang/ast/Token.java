package fr.krabumb.krablang.ast;

public class Token {

    private final String type;
    private final String value;

    private final int lineNumber;
    private final int charNumber;

    public Token(String t, String v, int l, int c){
        this.type = t;
        this.value = v;
        this.lineNumber = l;
        this.charNumber = c;
    }

    public String getType(){
        return this.type;
    }

    public String getValue(){
        return this.value;
    }

    public int getLineNumber(){
        return this.lineNumber;
    }

    public int getCharNumber(){
        return this.charNumber;
    }
}
