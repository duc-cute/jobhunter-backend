package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.*;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resumes")
    public ResponseEntity<Resume> createResume( @RequestBody Resume dto) throws  IdInvalidException {
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(dto);
        if(!isIdExist) {
            throw  new IdInvalidException("User id/Job id không tồn tại");
        }
        ResCreateResumeDTO newResume = this.resumeService.handleCreateResume(dto);
        return new ResponseEntity(newResume,newResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/resume")
    @ApiMessage("Update resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume( @RequestBody Resume dto) throws  IdInvalidException {
        Optional<Resume> currentResume  =this.resumeService.getById(dto.getId());
        if(currentResume.isEmpty()) {
            throw  new IdInvalidException("Resume  is not found!");
        }
        Resume resumeUpdate = currentResume.get();
        resumeUpdate.setStatus(dto.getStatus());
        ResUpdateResumeDTO updateResume = this.resumeService.handleUpdateResume(resumeUpdate);
        return new ResponseEntity(updateResume,updateResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/resumes")
    @ApiMessage("/Fetch resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
        ResultPaginationDTO result = this.resumeService.getAllResumes(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume")
    public  ResponseEntity<ResFetchResumeDTO> getAResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resume = this.resumeService.getById(id);
        if(resume.isEmpty()) {
            throw  new IdInvalidException("Resume is not found");
        }

        ResFetchResumeDTO currentResume = this.resumeService.getResume(resume.get());
        return new ResponseEntity<ResFetchResumeDTO>(currentResume,currentResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.getById(id);
        if(currentResume.isEmpty()) throw  new IdInvalidException("Resume is not found");
        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }



}
