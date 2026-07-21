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

/**
 * Репозиторий для доступа к данным заявок.
 * Содержит методы поиска, сохранения, обновления, фильтрации, статистики и замера производительности.
 */

public class RequestDao {

    /**
     * Находит заявку по ID с подгрузкой исполнителя и автора.
     *
     * @param id идентификатор
     * @return заявка
     * @throws EntityNotFoundExeption если заявка не найдена
     */
    public Request findById(long id){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Request r " +
                    "JOIN FETCH r.executor " +
                    "JOIN FETCH r.author " +
                    "WHERE r.id = :id";
            TypedQuery<Request> query = em.createQuery(jpql, Request.class);
            query.setParameter("id", id);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Находит заявку по её уникальному номеру с подгрузкой исполнителя и автора.
     *
     * @param number номер заявки
     * @return заявка
     * @throws EntityNotFoundExeption если заявка не найдена
     */
    public Request findByNumber(String number){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r FROM Request r " +
                    "JOIN FETCH r.executor " +
                    "JOIN FETCH r.author " +
                    "WHERE r.number = :number";
            TypedQuery<Request> query = em.createQuery(jpql, Request.class);
            query.setParameter("number", number);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Возвращает ID заявки по её номеру (без загрузки сущности).
     *
     * @param number номер заявки
     * @return ID заявки
     * @throws EntityNotFoundExeption если заявка не найдена
     */
    public Long findIdByNumber(String number){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT r.id FROM Request r " +
                    "WHERE r.number = :number";
            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("number", number);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Сохраняет новую заявку в БД.
     *
     * @param r заявка (без ID)
     */
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

    /**
     * Обновляет существующую заявку.
     *
     * @param r заявка с заполненным ID
     */
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

    /**
     * Изменяет статус заявки с проверкой бизнес-правил и записью в историю.
     *
     * @param requestId           ID заявки
     * @param newStatus           новый статус
     * @param changedByEmployeId  ID сотрудника, выполнившего изменение
     * @throws EntityNotFoundExeption если заявка или сотрудник не найдены
     * @throws InvalidStatusTransitionExeption если переход недопустим
     */
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

    /**
     * Назначает нового исполнителя для заявки.
     *
     * @param requestId      ID заявки
     * @param newExecutorId  ID нового исполнителя
     * @throws EntityNotFoundExeption если заявка или исполнитель не найдены
     */
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

    /**
     * Возвращает список заявок по динамическим фильтрам.
     *
     * @param status     статус (может быть null)
     * @param executorId ID исполнителя (может быть null)
     * @param department подразделение (может быть null)
     * @param overdue    true – только просроченные, false – все
     * @return список заявок (может быть пустым)
     */
    public List<Request> findWithFilters(RequestStatus status, Long executorId,
                                         String department, Boolean overdue){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT r FROM Request r JOIN FETCH r.executor e WHERE 1=1"
            );
            Map<String, Object> params = new HashMap<>();

            if (status != null) {
                jpql.append(" AND r.status = :status");
                params.put("status", status);
            }
            if (executorId != null) {
                jpql.append(" AND e.id = :executorId");
                params.put("executorId", executorId);
            }
            if (department != null && !department.isEmpty()) {
                jpql.append(" AND e.department = :department");
                params.put("department", department);
            }
            if (overdue != null && overdue) {
                jpql.append(" AND r.dueDate < CURRENT_TIMESTAMP");
            }
            jpql.append(" ORDER BY r.dueDate");

            TypedQuery<Request> query = em.createQuery(jpql.toString(), Request.class);
            params.forEach(query::setParameter);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Возвращает количество заявок по каждому статусу.
     *
     * @return Map<Статус, Количество>
     */
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

    /**
     * Возвращает общее количество просроченных заявок.
     *
     * @return количество
     */
    public long countOverDue(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(r) FROM Request r WHERE r.dueDate < CURRENT_TIMESTAMP";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Возвращает количество выполненных заявок по каждому исполнителю (по полному имени).
     *
     * @return Map<Имя исполнителя, Количество>
     */
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

    /**
     * Возвращает просроченные заявки в статусе IN_PROGRESS для указанного исполнителя,
     * отсортированные по сроку выполнения.
     * Используется для замера производительности.
     *
     * @param executorId ID исполнителя
     * @return список заявок
     */

    public List<Request> getOverdueInProgressForExecutor(Long executorId) {
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
