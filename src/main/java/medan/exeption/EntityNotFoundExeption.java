package medan.exeption;

public class EntityNotFoundExeption extends RuntimeException{
    public EntityNotFoundExeption(String msg){
        super(msg);
    }
}
