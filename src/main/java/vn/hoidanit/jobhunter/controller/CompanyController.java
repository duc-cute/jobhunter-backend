package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.domain.dto.CompanyDTO;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> saveCompany(@Valid @RequestBody CompanyDTO companyDTO) {
        Company newCompany = this.companyService.handleCreate(companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);

    }

    @GetMapping("/companies")
    @ApiMessage("fetch companies all")
    public  ResponseEntity<ResultPaginationDTO> getAllCompany(@Filter Specification<Company> spec, Pageable pageable) {
        ResultPaginationDTO result = this.companyService.fetchCompanies(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company c) {
        Company res = this.companyService.updateCompany(c);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
