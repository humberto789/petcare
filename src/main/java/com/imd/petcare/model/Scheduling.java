package com.imd.petcare.model;

import com.imd.petcare.model.enums.SchedulingType;
import jakarta.persistence.*;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "scheduling")
@Where(clause = "active = true")
public class Scheduling extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private Long month;

    @Column
    private Long day;

    @Column
    private Long year;

    @Column
    @Enumerated(EnumType.STRING)
    private SchedulingType type;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(Long day) {
        this.day = day;
    }

    public Long getMonth() {
        return month;
    }

    public void setMonth(Long month) {
        this.month = month;
    }

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public SchedulingType getType() {
        return type;
    }

    public void setType(SchedulingType type) {
        this.type = type;
    }
}
