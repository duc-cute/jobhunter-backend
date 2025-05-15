package vn.itviec.jobhunter.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.itviec.jobhunter.util.constant.GenderEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResReportDTO {
    private Long users;
    private Long cvs;
    private Long companies;
    private Long jobs;
    private List<CompanyReportDTO> listReportCompany;

}
