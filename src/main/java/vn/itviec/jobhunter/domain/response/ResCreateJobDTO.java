package vn.itviec.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.itviec.jobhunter.util.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private LevelEnum level;
    private double salary;
    private int quantity;
    private Instant startDate;
    private Instant endDate;

    private boolean active;

    private List<String> skills;
    private Instant createdAt;
    private String createdBy;

}
