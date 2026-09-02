package org.tracker.model.entities;

import jakarta.persistence.*;
import org.tracker.model.enums.ApplicationStatus;
import org.tracker.model.enums.WorkMode;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "position_title", nullable = false)
    private String positionTitle;

    @Column(name = "location")
    private String location;

    @Column(name = "work_mode")
    @Enumerated(value = EnumType.STRING)
    private WorkMode workMode;

    @Column(name = "application_url")
    private String applicationUrl;

    @Column(name = "date_applied")
    private LocalDate dateApplied;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private ApplicationStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Application() {}

    public Application(UUID id, User user, String companyName, String positionTitle, String location,
                       WorkMode workMode, String applicationUrl, LocalDate dateApplied, Instant createdAt){
        this.id = id;
        this(user, companyName, positionTitle, location, workMode, applicationUrl, dateApplied, createdAt);
    }
    public Application(User user, String companyName, String positionTitle, String location,
                       WorkMode workMode, String applicationUrl, LocalDate dateApplied, Instant createdAt) {
        this.user = user;
        this.companyName = companyName;
        this.positionTitle = positionTitle;
        this.location = location;
        this.workMode = workMode;
        this.applicationUrl = applicationUrl;
        this.dateApplied = dateApplied;
        this.status = ApplicationStatus.APPLIED;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public WorkMode getWorkMode() {
        return workMode;
    }

    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Application{" +
                "user=" + user +
                ", companyName='" + companyName + '\'' +
                ", positionTitle='" + positionTitle + '\'' +
                ", location='" + location + '\'' +
                ", workMode=" + workMode +
                ", applicationUrl='" + applicationUrl + '\'' +
                ", dateApplied=" + dateApplied +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
