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
import vn.itviec.jobhunter.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;

    public HrRegisterService(HrRegisterRepository hrRegisterRepository,UserService userService,RoleService roleService,CompanyRepository companyRepository) {
        this.hrRegisterRepository = hrRegisterRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.companyRepository = companyRepository;

    }
    public HrRegister handleCreate(HrRegister dto) {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = userService.handleGetUserByUserName(email);
        dto.setUser(currentUser);
        dto.setActive(false);
        HrRegister newHr = new HrRegister();
        newHr.setAge(dto.getAge());
        newHr.setEmailRegister(dto.getEmailRegister());
        if(dto.getCompanyName() != null) {
            newHr.setCompanyName(dto.getCompanyName());
        }
        if(dto.getCompanyAddress() != null) {
            newHr.setCompanyAddress(dto.getCompanyAddress());
        }
        if(dto.getCompanyId() != null) {
            Optional<Company> company = this.companyRepository.findById(Long.valueOf(dto.getCompanyId()));
            if(company.isPresent()) {
                newHr.setCompanyName(company.get().getName());
                newHr.setCompanyAddress(company.get().getAddress());
            }
            newHr.setCompanyId(dto.getCompanyId());
        }
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
        if(currentHr.getCompanyId() != null) {
            Optional<Company> company = this.companyRepository.findById(Long.valueOf(currentHr.getCompanyId()));
            if(company.isPresent()) {
                currentUser.setCompany(company.get());
            }
        }
        if(currentHr != null) {
            currentHr.setActive(true);
            HrRegister newHr = this.hrRegisterRepository.save(currentHr);
            Optional<Role> role = this.roleService.getARole(5);
            if (role.isPresent()) {
                currentUser.setRole(role.get());
                this.userService.updateUserByAdmin(currentUser);
            }
            return newHr;

        }
        return null;

    }

}
