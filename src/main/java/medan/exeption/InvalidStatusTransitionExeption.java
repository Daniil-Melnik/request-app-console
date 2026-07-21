package medan.exeption;

/**
 * Исключение, выбрасываемое при попытке недопустимого перехода статуса заявки
 * (например, NEW → COMPLETED)
 */
public class InvalidStatusTransitionExeption extends RuntimeException{
    public InvalidStatusTransitionExeption(String msg){
        super(msg);
    }
}
