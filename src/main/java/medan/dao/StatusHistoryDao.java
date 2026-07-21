package medan.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import medan.model.StatusHistory;
import medan.util.JpaUtil;

import java.util.List;

/**
 * Репозиторий для доступа к записям истории статусов.
 */

public class StatusHistoryDao {
    /**
     * Возвращает все записи истории для заданной заявки, отсортированные по времени изменения.
     * Загружает связанные сущности (changedBy и request) через JOIN FETCH.
     *
     * @param requestId ID заявки
     * @return список записей (может быть пустым)
     */
    public List<StatusHistory> findByRequest(long requestId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<StatusHistory> query = em.createQuery(
                    "SELECT h FROM StatusHistory h JOIN FETCH h.changedBy JOIN FETCH h.request WHERE h.request.id = :reqId ORDER BY h.changedAt",
                    StatusHistory.class
            );
            query.setParameter("reqId", requestId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Возвращает все записи истории для всех заявок.
     *
     * @return список всех записей (может быть пустым)
     */
    public List<StatusHistory> findAll(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<StatusHistory> query = em.createQuery("SELECT s FROM StatusHistory s JOIN FETCH s.changedBy JOIN FETCH s.request", StatusHistory.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Сохраняет новую запись истории в БД.
     *
     * @param history запись истории
     */
    public void save(StatusHistory history){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(history);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
