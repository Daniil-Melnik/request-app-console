package medan.exeption;

/**
 * Исключение, выбрасываемое, когда запрошенная сущность (сотрудник, заявка) не найдена в БД
 */
public class EntityNotFoundExeption extends RuntimeException{
    public EntityNotFoundExeption(String msg){
        super(msg);
    }
}
