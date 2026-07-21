package medan.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

/**
 * Сущность "Заявка". Содержит всю информацию о заявке: номер, автор, исполнитель,
 * даты, статус, описание. Статус изменяется по правилам бизнес-логики
 */

@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 20)
    private String number;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private Employee executor;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    /**
     * Конструктор без параметров
     * Устанавливает текущую дату создания и статус NEW.
     */
    public Request(){
        this.createdDate = LocalDateTime.now();
        this.status = RequestStatus.NEW;
    }

    public RequestStatus getStatus() {
        return status;
    }
    public String getNumber() { return number; }
    public Employee getExecutor() { return executor; }
    public long getId() { return id; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public Employee getAuthor() { return author; }
    public String getDescription() {return description; }

    public void setExecutor(Employee e) {
        this.executor = e;
    }

    public void setStatus(RequestStatus s){
        this.status = s;
    }

    public void setCreatedDate(LocalDateTime d) { this.createdDate = d;}

    /**
     * Конструктор для создания новой заявки. Устанавливает текущую дату и статус NEW.
     *
     * @param n      уникальный номер заявки
     * @param a      автор (сотрудник)
     * @param e    исполнитель
     * @param d текстовое описание
     * @param dD     срок выполнения
     */
    public Request(String n, Employee a, Employee e,
                   String d, LocalDateTime dD) {
        this();
        this.number = n;
        this.author = a;
        this.executor = e;
        this.description = d;
        this.dueDate = dD;
    }
}
