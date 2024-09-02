package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.CompanyDTO;
import vn.hoidanit.jobhunter.domain.dto.MetaDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreate(CompanyDTO c) {
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
        MetaDTO meta = new MetaDTO();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pCompany.getTotalElements());
        meta.setPages(pCompany.getTotalPages());
        result.setMetaDTO(meta);
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
            this.companyRepository.deleteById(currentCompany.getId());
        }
    }

}
