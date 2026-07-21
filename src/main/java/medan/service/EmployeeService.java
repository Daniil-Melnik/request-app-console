package medan.service;

import medan.dao.EmployeeDao;
import medan.model.Employee;

import java.util.List;

/**
 * Сервис для работы с сотрудниками. Предоставляет методы создания, получения списка,
 * получения по ID, обновления. Делегирует вызовы EmployeeDao.
 */

public class EmployeeService {
    private final EmployeeDao employeeDao = new EmployeeDao();

    /**
     * Создаёт нового сотрудника и сохраняет в БД.
     *
     * @param name        ФИО
     * @param departament подразделение
     * @param position    должность
     */
    public void createEmployee(String name, String departament, String position){
        employeeDao.save(new Employee(name, departament, position));
    }

    /**
     * Возвращает список всех сотрудников.
     *
     * @return список сотрудников (может быть пустым)
     */
    public List<Employee> getAllEmployees(){
        return (new EmployeeDao()).findAll();
    }

    /**
     * Находит сотрудника по его ID.
     *
     * @param id идентификатор
     * @return сотрудник
     */
    public Employee getSinggleEmployee(long id){
        return (new EmployeeDao()).findById(id);
    }

    /**
     * Обновляет данные сотрудника в БД.
     *
     * @param emp сотрудник с изменёнными полями (ID должен быть заполнен)
     */
    public void updateEmployee(Employee emp){
        (new EmployeeDao()).update(emp);
    }
}
