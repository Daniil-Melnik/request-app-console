package medan.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import medan.exeption.EntityNotFoundExeption;
import medan.model.Employee;
import medan.util.JpaUtil;

import java.util.List;

public class EmployeeDao {
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

    public List<Employee> findAll(){
        EntityManager em = JpaUtil.getEntityManager();
        try {
            TypedQuery<Employee> query = em.createQuery("SELECT e FROM Employee e", Employee.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

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
