package vn.itviec.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateResumeDTO {
    private long id;
    private Instant updatedAt;
    private String updatedBy;
}
