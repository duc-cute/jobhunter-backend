package vn.itviec.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.itviec.jobhunter.domain.Skill;
import vn.itviec.jobhunter.domain.request.ReqReportDTO;
import vn.itviec.jobhunter.domain.response.CompanyReportDTO;
import vn.itviec.jobhunter.domain.response.ResReportDTO;
import vn.itviec.jobhunter.domain.response.ResultPaginationDTO;
import vn.itviec.jobhunter.repository.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class Reportervice {

    private final JobRepository jobRepository;

    private final ResumeRepository resumeRepository;

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;
    public Reportervice(UserRepository userRepository ,
                        JobRepository jobRepository,
                        CompanyRepository companyRepository,
                        ResumeRepository resumeRepository
                        ) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.resumeRepository = resumeRepository;

    }

    public ResReportDTO getDashBoard(ReqReportDTO dto) {
        if (dto == null || dto.getFromDate() == null || dto.getToDate() == null) {
            throw new IllegalArgumentException("fromDate và toDate không được null");
        }

        ResReportDTO res = new ResReportDTO();
        Instant from = dto.getFromDate().toInstant();
        Instant to = dto.getToDate().toInstant();
        long countJobs = jobRepository.countByCreatedDate(from,to);
        long countUsers = userRepository.countByCreatedDate(from,to);
        long countCompanies = companyRepository.countByCreatedDate(from,to);
        List<CompanyReportDTO> listReportCompany = companyRepository.getReportByCompany(from,to);
        long countResumes = resumeRepository.countByCreatedDate(from,to);
        res.setUsers(countUsers);
        res.setCvs(countResumes);
        res.setCompanies(countCompanies);
        res.setJobs(countJobs);
        res.setListReportCompany(listReportCompany);

        return res;



    }


}
