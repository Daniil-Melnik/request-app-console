package medan.model;

import jakarta.persistence.*;

import java.util.List;

/**
 * Сущность "Сотрудник". Хранит персональные данные: ФИО, подразделение, должность
 * Используется как автор или исполнитель заявок
 */

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String departament;

    @Column(nullable = false, length = 100)
    private String position;

    /**
     * Конструктор без параметров
     */
    public Employee(){}

    /**
     * Конструктор для создания нового сотрудника
     *
     * @param fullName    полное имя
     * @param departament подразделение
     * @param position    должность
     */

    public Employee(String fullName, String departament, String position){
        this.fullName = fullName;
        this.departament = departament;
        this.position = position;
    }

    public String getFullName(){ return fullName; }
    public String getDepartament() { return departament; }
    public String getPosition() { return position; }
    public long getId() { return id; }

    public void setFullName(String fN) { this.fullName = fN; }
    public void setDepartament(String d) { this.departament = d;}
    public void setPosition (String p) { this.position = p; }
}
