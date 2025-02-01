package vn.itviec.jobhunter.domain.request;

import lombok.Data;

@Data
public class ReqSearchJobDTO {
        private int pageIndex = 1;
        private int pageSize = 10;
        private String keyword;
        private Long companyId;
        private String skills;
        private String location;
        private boolean isActive;

}
