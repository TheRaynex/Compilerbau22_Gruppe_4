package compiler.ast;

public class Tuple<A, B> {
    public A _1;
    public B _2;
    
    public Tuple(A first, B second) {
        _1 = first;
        _2 = second;
    }
}
