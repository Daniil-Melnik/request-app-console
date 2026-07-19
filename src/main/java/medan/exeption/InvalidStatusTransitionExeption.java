package medan.exeption;

public class InvalidStatusTransitionExeption extends RuntimeException{
    public InvalidStatusTransitionExeption(String msg){
        super(msg);
    }
}
