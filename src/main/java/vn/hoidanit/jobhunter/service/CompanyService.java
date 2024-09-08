package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository =userRepository;
    }

    public Company handleCreate(ReqCompanyDTO c) {
        Company newCompany = new Company();
        newCompany.setLogo(c.getLogo());
        newCompany.setAddress(c.getAddress());
        newCompany.setDescription(c.getDescription());
        newCompany.setName(c.getName());
        this.companyRepository.save(newCompany);
        return newCompany;

    }

    public ResultPaginationDTO fetchCompanies(Specification<Company> spec, Pageable pageable){

        Page<Company> pCompany = this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO result  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pCompany.getTotalElements());
        meta.setPages(pCompany.getTotalPages());
        result.setMeta(meta);
        result.setResult(pCompany.getContent());
        return result;
    }

    public Company getCompanyById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if(company.isPresent()) return company.get();
        return null;
    }

    public Company updateCompany(Company c) {
        Company currentCompany = this.getCompanyById(c.getId());
        if(currentCompany != null) {
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            currentCompany.setLogo(c.getLogo());
            this.companyRepository.save(currentCompany);
        }
        return  currentCompany;

    }

    public void deleteCompany(long id) {
        Company currentCompany = this.getCompanyById(id);
        if(currentCompany != null) {
            List<User> users = this.userRepository.findByCompany(currentCompany);
            this.userRepository.deleteAll(users);

            this.companyRepository.deleteById(currentCompany.getId());
        }
    }

}
