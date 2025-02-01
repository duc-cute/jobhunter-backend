package vn.itviec.jobhunter.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.Job;
import vn.itviec.jobhunter.domain.Skill;
import vn.itviec.jobhunter.domain.User;
import vn.itviec.jobhunter.domain.request.ReqSearchJobDTO;
import vn.itviec.jobhunter.domain.response.ResCreateJobDTO;
import vn.itviec.jobhunter.domain.response.ResUpdateJobDTO;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.repository.CompanyRepository;
import vn.itviec.jobhunter.repository.JobRepository;
import vn.itviec.jobhunter.util.SercurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobRepository jobRepository;

    private final SkillService skillService;
    private final CompanyRepository companyRepository;

    private final EntityManager entityManager;

    private final UserService userService;

    public JobService(JobRepository jobRepository,
                      SkillService skillService,
                      CompanyRepository companyRepository,
                      EntityManager entityManager,
                      UserService userService
                      ) {
        this.jobRepository = jobRepository;
        this.skillService = skillService;
        this.companyRepository=companyRepository;
        this.entityManager =entityManager;
        this.userService =userService;
    }

    public ResCreateJobDTO handleCreateJob(Job dto) {
        if(dto.getSkills() != null) {
            List<Long> listSkills = dto.getSkills()
                    .stream().map(item -> item.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillService.findByIdIn(listSkills);
            dto.setSkills(dbSkills);
        }

        if(dto.getCompany() != null) {
            Optional<Company> c = this.companyRepository.findById(dto.getCompany().getId());
            if(c.isPresent()) {
                dto.setCompany(c.get());

            }
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

    public ResUpdateJobDTO handleUpdateJob(Job dto,Job jobInDb)  {
            if(dto.getSkills() != null) {
                List<Long> listSkill = dto.getSkills()
                        .stream().map(item -> item.getId()).collect(Collectors.toList());
                List<Skill> dbSkills = this.skillService.findByIdIn(listSkill);
                jobInDb.setSkills(dbSkills);
            }

        if(dto.getCompany() != null) {
            Optional<Company> c = this.companyRepository.findById(dto.getCompany().getId());
            if(c.isPresent()) {
                jobInDb.setCompany(c.get());

            }
        }

        jobInDb.setName(dto.getName());
        jobInDb.setActive(dto.isActive());
        jobInDb.setId(dto.getId());
        jobInDb.setEndDate(dto.getEndDate());
        jobInDb.setStartDate(dto.getStartDate());
        jobInDb.setLevel(dto.getLevel());
        jobInDb.setLocation(dto.getLocation());
        jobInDb.setSalary(dto.getSalary());
        jobInDb.setDescription(dto.getDescription());


        Job currentJob = this.jobRepository.save(jobInDb);

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

    public ResultPaginationDTO getAllJobs(Specification<Job> spec,Pageable pageable) {

        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() :"";
        User currentUser = this.userService.handleGetUserByUserName(email);


        boolean isSuperAdmin = "SUPER ADMIN".equalsIgnoreCase(currentUser.getRole().getName());
        if(!isSuperAdmin){
            Long companyId = currentUser.getCompany().getId();
            Specification<Job> companySpec = (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("company").get("id"), companyId);

            spec = spec.and(companySpec);
        }
        Page<Job> pJobs = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pJobs.getTotalElements());
        mt.setPages(pJobs.getTotalPages());
        result.setMeta(mt);
        result.setResult(pJobs.getContent());
        return result;

    }


    public ResultPaginationDTO pagingJobs(ReqSearchJobDTO dto) {
        if (dto == null) return null;

        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) pageIndex--;
        else pageIndex = 0;

        String sqlCount = "SELECT COUNT(j.id) FROM Job j WHERE (1=1) ";
        String sql = "SELECT j FROM Job j WHERE (1=1) ";

        String whereClause = "";

        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() :"";
        User currentUser = this.userService.handleGetUserByUserName(email);
        boolean isSuperAdmin = "SUPER ADMIN".equalsIgnoreCase(currentUser.getRole().getName());


        // Nếu không phải admin, chỉ lấy job của công ty họ
        if (!isSuperAdmin && dto.getCompanyId() != null) {
            whereClause += " AND j.company.id = :companyId ";
        }

        // Lọc theo từ khóa
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND ( j.name LIKE :text) ";
        }

        if (dto.getLocation() != null && StringUtils.hasText(dto.getLocation())) {
            whereClause += " AND ( j.location LIKE :location) ";
        }

        whereClause += " AND (j.active = :isActive) ";


        // Sắp xếp theo ngày cập nhật mới nhất
        String orderBy = " ORDER BY j.updatedAt DESC ";

        sql += whereClause + orderBy;
        sqlCount += whereClause;

        Query q = entityManager.createQuery(sql, Job.class);
        Query qCount = entityManager.createQuery(sqlCount);

        // Set tham số
        if (!isSuperAdmin && dto.getCompanyId() != null) {
            q.setParameter("companyId", dto.getCompanyId());
            qCount.setParameter("companyId", dto.getCompanyId());
        }

        if (dto.getLocation() != null && StringUtils.hasText(dto.getLocation())) {
            q.setParameter("location", "%" + dto.getLocation() + "%");
            qCount.setParameter("location", "%" + dto.getLocation() + "%");
        }

        q.setParameter("isActive",  dto.isActive() );
        qCount.setParameter("isActive",  dto.isActive() );

        // Phân trang
        int startPosition = pageIndex * pageSize;
        q.setFirstResult(startPosition);
        q.setMaxResults(pageSize);


        List<Job> jobs = q.getResultList();
        long count = ((Number) qCount.getSingleResult()).longValue();
        int totalPages = (int) Math.ceil((double) count / pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(count);
        mt.setPages(totalPages);
        result.setMeta(mt);
        result.setResult(jobs);
        return result;


    }


    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }



}
