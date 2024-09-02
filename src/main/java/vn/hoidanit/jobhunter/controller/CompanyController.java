package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.RestResponse;
import vn.hoidanit.jobhunter.domain.dto.CompanyDTO;
import vn.hoidanit.jobhunter.service.CompanyService;

import java.util.List;

@RestController
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
    public  ResponseEntity<List<Company>> getAllCompany() {
        List<Company> list = this.companyService.getAllCompany();
        return ResponseEntity.status(HttpStatus.OK).body(list);
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
