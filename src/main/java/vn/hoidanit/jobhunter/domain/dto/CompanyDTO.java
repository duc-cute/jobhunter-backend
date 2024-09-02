package vn.hoidanit.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {

    @NotBlank(message = "Không được được trống tên công ty")
    private String name;

    private String address;

    private String description;

    private String logo;
}
