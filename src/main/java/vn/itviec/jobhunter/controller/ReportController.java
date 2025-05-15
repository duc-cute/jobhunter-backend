package vn.itviec.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.itviec.jobhunter.domain.Skill;
import vn.itviec.jobhunter.domain.request.ReqReportDTO;
import vn.itviec.jobhunter.domain.response.ResReportDTO;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.service.Reportervice;
import vn.itviec.jobhunter.service.SkillService;
import vn.itviec.jobhunter.util.annotation.ApiMessage;
import vn.itviec.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

    private  final Reportervice reportervice;

    public ReportController(Reportervice reportervice) {
        this.reportervice = reportervice;
    }

    @PostMapping("/")
    @ApiMessage("get dashboard")
    public ResponseEntity<ResReportDTO> createSkill(@Valid @RequestBody ReqReportDTO searchObj) throws  IdInvalidException {
            ResReportDTO dto = this.reportervice.getDashBoard(searchObj);
            return new ResponseEntity<ResReportDTO>(dto,(dto != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST));

    }






}
