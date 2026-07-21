package medan.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Утилитный класс для работы с JPA EntityManager
 * Содержит фабрику и методы получения/закрытия менеджера
 */

public class JpaUtil {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("requestPU");

    /**
     * Возвращает новый экземпляр EntityManager.
     * Использует фабрику, созданную на основе persistence-unit "requestPU".
     *
     * @return EntityManager
     */
    public static EntityManager getEntityManager(){
        return emf.createEntityManager();
    }

    /**
     * Закрывает фабрику EntityManagerFactory, освобождая ресурсы.
     * Вызывается при завершении приложения.
     */
    public static void close(){
        emf.close();
    }
}
