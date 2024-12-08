package vn.itviec.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.HrRegister;
import vn.itviec.jobhunter.domain.Role;
import vn.itviec.jobhunter.domain.User;
import vn.itviec.jobhunter.domain.response.ResCompanyDTO;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.repository.HrRegisterRepository;
import vn.itviec.jobhunter.util.SercurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HrRegisterService {
    private final HrRegisterRepository hrRegisterRepository;
    private final UserService userService;
    private final RoleService roleService;

    public HrRegisterService(HrRegisterRepository hrRegisterRepository,UserService userService,RoleService roleService) {
        this.hrRegisterRepository = hrRegisterRepository;
        this.userService = userService;
        this.roleService = roleService;

    }
    public HrRegister handleCreate(HrRegister dto) {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = userService.handleGetUserByUserName(email);
        dto.setUser(currentUser);
        dto.setActive(false);
        HrRegister newHr = new HrRegister();
        newHr.setAge(dto.getAge());
        newHr.setEmailRegister(dto.getEmailRegister());
        newHr.setCompanyName(dto.getCompanyName());
        newHr.setGender(dto.getGender());
        newHr.setActive(false);
        newHr.setPermanentAddress(dto.getPermanentAddress());
        newHr.setFullName(dto.getFullName());
        newHr.setPosition(dto.getPosition());
        newHr.setUser(currentUser);
        HrRegister newHrNotAcive = this.hrRegisterRepository.save(newHr);

        return newHrNotAcive;

    }
    public ResultPaginationDTO fetchHr(Specification<HrRegister> spec, Pageable pageable){

        Page<HrRegister> pHr = this.hrRegisterRepository.findAll(spec,pageable);
        ResultPaginationDTO result  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pHr.getTotalElements());
        meta.setPages(pHr.getTotalPages());
        result.setMeta(meta);
        result.setResult(pHr.getContent());
        return result;
    }
    public HrRegister getHrById(long id) {
        Optional<HrRegister> hr = this.hrRegisterRepository.findById(id);
        if(hr.isPresent()) {
            HrRegister currentHr = hr.get();
            return currentHr;
        }
        return null;
    }
    public void deleteHrById(long id) {
        Optional<HrRegister> currentHr = this.hrRegisterRepository.findById(id);
        if(currentHr.isPresent()) {
            this.hrRegisterRepository.deleteById(currentHr.get().getId());
        }
    }
    public HrRegister activeHr(long id) {
        HrRegister currentHr = this.getHrById(id);
        User currentUser = this.userService.getUserById(currentHr.getUser().getId());

        if(currentHr != null) {
            currentHr.setActive(true);
            HrRegister newHr = this.hrRegisterRepository.save(currentHr);
            Optional<Role> role = this.roleService.getARole(5);
            if (role.isPresent()) {
                currentUser.setRole(role.get());
                this.userService.updateUser(currentUser);
            }
            return newHr;

        }
        return null;

    }

}
