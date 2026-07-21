package medan.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Сущность для хранения истории изменений статусов заявок.
 * Содержит ссылку на заявку, старый и новый статус, время изменения и сотрудника, выполнившего изменение.
 */

@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private RequestStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private RequestStatus newStatus;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private Employee changedBy;

    /**
     * Конструктор без параметров
     * Устанавливает текущее время изменения
     */

    public StatusHistory(){
        this.changedAt = LocalDateTime.now();
    }

    public Request getRequest() { return request;}
    public RequestStatus getOldStatus() { return  oldStatus;}
    public RequestStatus getNewStatus() { return newStatus;}
    public LocalDateTime getChangedAt() { return changedAt;}
    public Employee getChangedBy() {return changedBy;}

    public long getId() { return id; }

    public void setRequest(Request r) {this.request = r;}
    public void setOldStatus(RequestStatus oS) {this.oldStatus = oS;}
    public void setNewStatus(RequestStatus nS) {this.newStatus = nS;}
    public void setChangedAt(LocalDateTime cA) {this.changedAt = cA;}
    public void setChangedBy(Employee cB) {this.changedBy = cB;}
}
