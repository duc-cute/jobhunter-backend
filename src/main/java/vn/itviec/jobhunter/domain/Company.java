package vn.itviec.jobhunter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.itviec.jobhunter.util.SercurityUtil;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name ="companies")
@Getter
@Setter
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private String address;

    private String logo;

   // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> listUser;

    @OneToMany(mappedBy = "company",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

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
