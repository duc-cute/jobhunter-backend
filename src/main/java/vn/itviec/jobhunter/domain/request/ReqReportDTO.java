package vn.itviec.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReqReportDTO {
 private Date fromDate;
 private Date toDate;
}
