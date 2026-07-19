package medan.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import medan.exeption.EntityNotFoundExeption;
import medan.exeption.InvalidStatusTransitionExeption;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;
import medan.model.StatusHistory;
import medan.util.JpaUtil;

import java.time.LocalDateTime;
import java.util.*;

public class RequestDao {

    public Request findById(long id){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Request r = em.find(Request.class, id);
            if (r == null) throw new EntityNotFoundExeption(String.format("Заявка с id = %s не найдена", id));
            return r;
        } finally {
            em.close();
        }
    }

    public void save(Request r){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void update(Request r){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(r);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void updateStatus(long requestId, RequestStatus newStatus, long changedByEmployeId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Request r = em.find(Request.class, requestId);
            if (r == null) throw new EntityNotFoundExeption("Заявка не найдена");

            RequestStatus oldStatus = r.getStatus();
            if (!oldStatus.canTransitionTo(newStatus))
                throw new InvalidStatusTransitionExeption(String.format("Недопустимый переход из состояния %s в состояние %s", oldStatus, newStatus));

            r.setStatus(newStatus);
            em.merge(r);

            StatusHistory history = new StatusHistory();
            history.setRequest(r);
            history.setOldStatus(oldStatus);
            history.setNewStatus(newStatus);

            EmployeeDao employeeDao = new EmployeeDao();
            Employee emp = employeeDao.findById(changedByEmployeId);
            if (emp != null) {
                history.setChangedBy(emp);
            } else throw new EntityNotFoundExeption(String.format("Не найден пользователь с id = %s", changedByEmployeId));

            em.persist(history);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public void updateExecutor(long requestId, long newExecutorId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();

            Request r = em.find(Request.class, requestId);
            if (r == null) throw new EntityNotFoundExeption(String.format("Заявка с id = %s не найдена", requestId));

            EmployeeDao employeeDao = new EmployeeDao();

            Employee newExecutor = employeeDao.findById(newExecutorId);
            if (newExecutor == null) throw new EntityNotFoundExeption(String.format("Пользователь с id = %s не найден", newExecutorId));

            r.setExecutor(newExecutor);
            em.merge(r);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Request> findWithFilters(RequestStatus status, Long executorId,
                                         String departament, Boolean overdue){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Request> cq = cb.createQuery(Request.class);
            Root<Request> requestRoot = cq.from(Request.class);
            List<Predicate> predicates = new ArrayList<>();

            if (status != null){
                predicates.add(cb.equal(requestRoot.get("status"), status));
            }
            if (executorId != null){
                predicates.add(cb.equal(requestRoot.get("executor").get("id"), executorId));
            }
            if (departament != null && !departament.isEmpty()){
                predicates.add(cb.equal(requestRoot.get("executor").get("departament"), departament));
            }
            if (overdue != null && overdue){
                predicates.add(cb.lessThan(requestRoot.get("dueDate"), LocalDateTime.now()));
            }

            cq.where(predicates.toArray(new Predicate[0]));
            cq.orderBy(cb.asc(requestRoot.get("dueDay")));

            TypedQuery<Request> query = em.createQuery(cq);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Map<RequestStatus, Long> countByStatus(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r.status, COUNT(r) FROM Request r GROUP BY r.status";

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            List<Object[]> result = query.getResultList();

            Map<RequestStatus, Long> map = new EnumMap<>(RequestStatus.class);
            result.forEach(row -> map.put((RequestStatus) row[0], (Long) row[1]));

            return map;
        } finally {
            em.close();
        }
    }

    public long countOverDue(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM Request r WHERE r.dueDate < CURRENT_TIMESTAMP";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public Map<String, Long> countCompletedByExecutor(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r.executor.fullName, COUNT(r) FROM Request r WHERE r.status = medan.model.RequestStatus.COMPLETED GROUP BY r.executor.fullName";

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
            List<Object[]> result = query.getResultList();

            Map<String, Long> map = new LinkedHashMap<>();
            result.forEach(row -> map.put((String) row[0], (Long) row[1]));
            return map;
        } finally {
            em.close();
        }
    }

    public List<Request> getOverDueInProgressForExecutor(long executorId){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Request r " +
                    "WHERE r.executor.id = :execId " +
                    "AND r.status = :status " +
                    "AND r.dueDate < CURRENT_TIMESTAMP " +
                    "ORDER BY r.dueDate";

            TypedQuery<Request> query = em.createQuery(jpql, Request.class);
            query.setParameter("execId", executorId);
            query.setParameter("status", RequestStatus.IN_PROGRESS);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
