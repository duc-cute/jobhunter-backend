package vn.itviec.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface CompanyReportDTO {
    Long getId();
    String getName();
    Long getTotalResumes();
    Long getTotalJobs();
}

