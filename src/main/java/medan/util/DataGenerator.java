package medan.util;

import jakarta.persistence.EntityManager;
import medan.model.Employee;
import medan.model.Request;
import medan.model.RequestStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Утилитный класс для генерации тестовых данных (сотрудников и заявок).
 * Использует пакетные вставки для ускорения.
 */

public class DataGenerator {

    private static final Random random = new Random();

    /**
     * Генерирует заданное количество сотрудников и заявок и сохраняет их в БД
     * Сотрудники создаются с именами "Employee X", подразделениями "Departament Y", должностями "Position Z"
     * Заявки получают номера "REQ-000001" и случайные параметры (даты, статусы)
     *
     * @param employeeCount количество сотрудников
     * @param requestCount  количество заявок
     * @throws RuntimeException если возникает ошибка при работе с БД
     */

    public static void generateEmployeesAndRequests(int employeeCount, int requestCount){

        EntityManager em = JpaUtil.getEntityManager();
        List<Employee> employees = new ArrayList<>();
        em.getTransaction().begin();
        for (int i = 0; i < employeeCount; i++){
            Employee emp = new Employee(
                    "Employee " + i,
                    "Departament " + (i % 20),
                    "Position " + (i % 7)
            );
            em.persist(emp);
            if ( i % 100 == 0){
                em.flush();
                em.clear();
            }
        }
        em.getTransaction().commit();
        em.clear();

        employees = em.createQuery("SELECT e FROM Employee e", Employee.class).getResultList();

        int batchSize = 50;
        em.getTransaction().begin();
        for (int i = 0; i < requestCount; i++){
            Employee author = employees.get(random.nextInt(employees.size()));
            Employee executor = employees.get(random.nextInt(employees.size()));
            LocalDateTime created = LocalDateTime.now().minusDays(random.nextInt(365));
            LocalDateTime due = created.plusDays(random.nextInt(30) + 1);
            RequestStatus status = RequestStatus.values()[random.nextInt(3)];

            Request request = new Request(
                    "REQ-" + String.format("%06d", i),
                    author, executor,
                    String.format("Описание заявки # %s", i),
                    due
            );
            request.setCreatedDate(created);
            request.setStatus(status);
            em.persist(request);

            if (i % batchSize == 0 && i > 0){
                em.flush();
                em.clear();
            }
        }
        em.getTransaction().commit();
        em.close();
    }
}
