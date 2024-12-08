package vn.itviec.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.itviec.jobhunter.util.SercurityUtil;
import vn.itviec.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Entity
@Table(name = "hr_register")
@Getter
@Setter
public class HrRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;
    private String position;
    private String emailRegister;
    private int age;
    private GenderEnum gender;
    private String permanentAddress;
    private String companyName;
    private String companyAddress;
    private boolean isActive;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Thêm cột user_id trong bảng hr_register
    @JsonIgnore
    private User user;



    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SercurityUtil.getCurrentUserLogin().isPresent() == true
                ? SercurityUtil.getCurrentUserLogin().get()
                : "";

        this.createdAt = Instant.now();
    }
    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SercurityUtil.getCurrentUserLogin().isPresent() == true
                ? SercurityUtil.getCurrentUserLogin().get()
                : "";

        this.updatedAt = Instant.now();
    }



}
