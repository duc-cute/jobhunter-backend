package vn.hoidanit.jobhunter.domain.dto;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

import java.time.Instant;
@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String address;
    private int age;
    private GenderEnum gender;
    private Instant updatedAt;
}