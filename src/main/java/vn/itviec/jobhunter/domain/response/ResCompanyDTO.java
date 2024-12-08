package vn.itviec.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.itviec.jobhunter.domain.Job;
import vn.itviec.jobhunter.domain.Skill;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCompanyDTO {
    private long id;
    private String name;
    private String description;

    private String address;

    private String logo;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    private List<Job> jobs;

    private List<Skill> skills;

}
