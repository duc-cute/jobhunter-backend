package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.response.CompanyReportDTO;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long>, JpaSpecificationExecutor<Company> {

  @Query("SELECT COUNT(c) FROM Company c WHERE c.createdAt BETWEEN :fromDate AND :toDate")
    long countByCreatedDate(@Param("fromDate") Instant fromDate, @Param("toDate")Instant toDate);

  @Query(value=""" 
          SELECT c.id as id,c.name as name ,count(r.id) as totalResumes,count(DISTINCT j.id) as totalJobs from companies c 
          LEFT JOIN jobs j ON j.company_id = c.id
          LEFT JOIN resumes r ON r.job_id = j.id
          WHERE ( j.created_at BETWEEN Date(:fromDate) AND Date(:toDate) OR r.created_at BETWEEN Date(:fromDate) AND Date(:toDate) )
          GROUP BY c.id,c.name 
          """,nativeQuery = true)
  List<CompanyReportDTO> getReportByCompany(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
