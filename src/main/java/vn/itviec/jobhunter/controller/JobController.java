package vn.itviec.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.Job;
import vn.itviec.jobhunter.domain.User;
import vn.itviec.jobhunter.domain.request.ReqSearchJobDTO;
import vn.itviec.jobhunter.domain.response.ResCreateJobDTO;
import vn.itviec.jobhunter.domain.response.ResUpdateJobDTO;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.service.JobService;
import vn.itviec.jobhunter.service.UserService;
import vn.itviec.jobhunter.util.SercurityUtil;
import vn.itviec.jobhunter.util.annotation.ApiMessage;
import vn.itviec.jobhunter.util.error.IdInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;
    private final UserService userService;

    private final FilterSpecificationConverter filterSpecificationConverter;

    private final FilterBuilder filterBuilder;

    public JobController(JobService jobService,
                         UserService userService,
                         FilterBuilder filterBuilder,
                         FilterSpecificationConverter filterSpecificationConverter

    ) {
        this.jobService = jobService;
        this.userService =userService;
        this.filterBuilder=filterBuilder;
        this.filterSpecificationConverter =filterSpecificationConverter;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job dto) throws IdInvalidException {
        ResCreateJobDTO newJob = this.jobService.handleCreateJob(dto);
        return new ResponseEntity(newJob,newJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/jobs")
    @ApiMessage("Update jobs")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job dto) throws  IdInvalidException {
        Job currentJob  =this.jobService.getById(dto.getId());
        if(currentJob == null) {
            throw  new IdInvalidException("Job " + dto.getName() + " is not found!");
        }
        ResUpdateJobDTO updateJob = this.jobService.handleUpdateJob(dto,currentJob);
        return new ResponseEntity(updateJob,updateJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/jobs")
    @ApiMessage("/Fetch jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobByUser(@Filter Specification<Job> spec, Pageable pageable) {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() :"";
        User currentUser = this.userService.handleGetUserByUserName(email);
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        ResultPaginationDTO result = jobService.getAllJobsByUser(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobs/public")
    @ApiMessage("/Fetch jobs")
    public ResponseEntity<ResultPaginationDTO> pagingAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        ResultPaginationDTO result = jobService.getAllJobs(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/paging-job")
    @ApiMessage("/Paging jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(@RequestBody ReqSearchJobDTO dto) {
        ResultPaginationDTO result = jobService.pagingJobs(dto);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job")
    public  ResponseEntity<Job> getAJob(@PathVariable long id) {
        Job  currentJob = this.jobService.getById(id);
        return new ResponseEntity<Job>(currentJob,currentJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        Job currentJob = this.jobService.getById(id);
        if(currentJob == null) throw  new IdInvalidException("Job is not found");
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }


}
