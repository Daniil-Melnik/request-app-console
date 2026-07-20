package medan.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import medan.model.StatusHistory;
import medan.util.JpaUtil;

import java.util.List;

public class StatusHistoryDao {
    public List<StatusHistory> findByRequest(long requestId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<StatusHistory> query = em.createQuery(
                    "SELECT h FROM StatusHistory h WHERE h.request.id = :reqId ORDER BY h.changedAt",
                    StatusHistory.class
            );
            query.setParameter("reqId", requestId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

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
