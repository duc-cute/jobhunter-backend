package vn.itviec.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.HrRegister;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.service.HrRegisterService;
import vn.itviec.jobhunter.util.annotation.ApiMessage;
import vn.itviec.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class HrRegisterController {

    private final HrRegisterService hrRegisterService;

    public HrRegisterController(HrRegisterService hrRegisterService) {
        this.hrRegisterService = hrRegisterService;
    }

    @PostMapping("/hr-register")
    @ApiMessage("create hr-register")
    public ResponseEntity<HrRegister> createHrRegister(@RequestBody HrRegister dto) throws IdInvalidException {
        HrRegister newHrRegister = this.hrRegisterService.handleCreate(dto);
        return new ResponseEntity<HrRegister>(newHrRegister,(newHrRegister != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/hr-register-active")
    @ApiMessage("update hr-register")
    public ResponseEntity<HrRegister> updateHrRegister(@RequestBody HrRegister dto) throws IdInvalidException {
        HrRegister updateHrRegister = this.hrRegisterService.activeHr(dto.getId());
        return new ResponseEntity<HrRegister>(updateHrRegister,(updateHrRegister != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }
    @GetMapping("/hr-register")
    @ApiMessage("fetch hr all")
    public  ResponseEntity<ResultPaginationDTO> getAllHr(@Filter Specification<HrRegister> spec, Pageable pageable) {
        ResultPaginationDTO result = this.hrRegisterService.fetchHr(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/hr-register/{id}")
    @ApiMessage("fetch hr all")
    public  ResponseEntity<HrRegister> getHrById(@PathVariable long id) {
        HrRegister hr = this.hrRegisterService.getHrById(id);
        return ResponseEntity.status(HttpStatus.OK).body(hr);
    }

    @DeleteMapping("/hr-register/{id}")
    public ResponseEntity<Void> deleteHr(@PathVariable long id) {
        this.hrRegisterService.deleteHrById(id);
        return ResponseEntity.ok(null);
    }
}
