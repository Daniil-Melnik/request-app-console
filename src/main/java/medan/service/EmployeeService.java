package medan.service;

import medan.dao.EmployeeDao;
import medan.model.Employee;

import java.util.List;

public class EmployeeService {
    private final EmployeeDao employeeDao = new EmployeeDao();

    public void createEmployee(String name, String departament, String position){
        employeeDao.save(new Employee(name, departament, position));
    }

    public List<Employee> getAllEmployees(){
        return (new EmployeeDao()).findAll();
    }

    public Employee getSinggleEmployee(long id){
        return (new EmployeeDao()).findById(id);
    }

    public void updateEmployee(Employee emp){
        (new EmployeeDao()).update(emp);
    }
}
