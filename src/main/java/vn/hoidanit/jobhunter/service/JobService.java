package vn.hoidanit.jobhunter.service;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    private final SkillService skillService;

    public JobService(JobRepository jobRepository,
                      SkillService skillService) {
        this.jobRepository = jobRepository;
        this.skillService = skillService;
    }

    public ResCreateJobDTO handleCreateJob(Job dto) {
        if(dto.getSkills() != null) {
            List<Long> listSkills = dto.getSkills()
                    .stream().map(item -> item.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillService.findByIdIn(listSkills);
            dto.setSkills(dbSkills);


        }
        ResCreateJobDTO newJob = new ResCreateJobDTO();
        Job job = this.jobRepository.save(dto);

        if(job != null) {
            newJob.setId(job.getId());
            newJob.setName(job.getName());
            newJob.setLevel(job.getLevel());
            newJob.setActive(job.isActive());
            newJob.setLocation(job.getLocation());
            newJob.setCreatedAt(job.getCreatedAt());
            newJob.setCreatedBy(job.getCreatedBy());
            newJob.setSalary(job.getSalary());
            newJob.setQuantity(job.getQuantity());
            newJob.setStartDate(job.getStartDate());
            newJob.setEndDate(job.getEndDate());
            if(job.getSkills() != null) {
                List<String> skills = job.getSkills()
                        .stream().map(item -> item.getName()).collect(Collectors.toList());
                newJob.setSkills(skills);

            }
            return  newJob;
        }
        return null;

    }
    public Job getById(long id) {
        Optional<Job> currentJob = this.jobRepository.findById(id);
        if(currentJob.isPresent()) return currentJob.get();
        return null;
    }

    public ResUpdateJobDTO handleUpdateJob(Job dto)  {
            if(dto.getSkills() != null) {
                List<Long> listSkill = dto.getSkills()
                        .stream().map(item -> item.getId()).collect(Collectors.toList());
                List<Skill> dbSkills = this.skillService.findByIdIn(listSkill);
                dto.setSkills(dbSkills);
            }

            Job currentJob = this.jobRepository.save(dto);

            if(currentJob != null) {

                ResUpdateJobDTO updateJobDto = new ResUpdateJobDTO();
                updateJobDto.setActive(currentJob.isActive());
                updateJobDto.setId(currentJob.getId());
                updateJobDto.setLevel(currentJob.getLevel());
                updateJobDto.setLocation(currentJob.getLocation());
                updateJobDto.setSalary(currentJob.getSalary());
                updateJobDto.setQuantity(currentJob.getQuantity());
                updateJobDto.setName(currentJob.getName());
                updateJobDto.setUpdatedAt(currentJob.getUpdatedAt());
                updateJobDto.setSkills(currentJob.getSkills()
                        .stream().map(item -> item.getName()).collect(Collectors.toList())
                );
                updateJobDto.setEndDate(currentJob.getEndDate());
                updateJobDto.setStartDate(currentJob.getStartDate());

                return updateJobDto;

            }
            return null;



    }

    public ResultPaginationDTO getAllJobs(Specification spec,Pageable pageable) {
        ResultPaginationDTO result = new ResultPaginationDTO();
        Page<Job> pJobs = this.jobRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pJobs.getTotalElements());
        mt.setPages(pJobs.getTotalPages());
        result.setMeta(mt);
        result.setResult(pJobs.getContent());
        return result;

    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }



}