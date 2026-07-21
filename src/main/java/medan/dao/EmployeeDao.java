package medan.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import medan.exeption.EntityNotFoundExeption;
import medan.model.Employee;
import medan.util.JpaUtil;

import java.util.List;

/**
 * Репозиторий для доступа к данным сотрудников.
 * Использует EntityManager для CRUD-операций.
 */

public class EmployeeDao {

    /**
     * Находит сотрудника по ID.
     *
     * @param id идентификатор
     * @return найденный сотрудник
     */
    public Employee findById(long id){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Employee e = em.find(Employee.class, id);
            if (e == null){
                throw new EntityNotFoundExeption(String.format("Сотрудник с id = %s не найден", id));
            }
            return e;
        } finally {
            em.close();
        }
    }

    /**
     * Возвращает список всех сотрудников.
     *
     * @return список сотрудников (может быть пустым)
     */
    public List<Employee> findAll(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e", Employee.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Сохраняет нового сотрудника в БД.
     *
     * @param e сотрудник (без ID)
     */
    public void save(Employee e){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(e);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        }  finally {
            em.close();
        }
    }

    /**
     * Обновляет существующего сотрудника.
     *
     * @param e сотрудник с заполненным ID
     */
    public void update(Employee e){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(e);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /**
     * Удаляет сотрудника по ID.
     *
     * @param id идентификатор
     */
    public void delete(long id){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Employee e = em.find(Employee.class, id);
            if (e != null)  em.refresh(e);
            em.getTransaction().commit();
        } catch (Exception ex){
            em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
