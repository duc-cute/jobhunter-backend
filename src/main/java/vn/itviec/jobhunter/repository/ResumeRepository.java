package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Resume;

import java.time.Instant;
import java.util.Date;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long> , JpaSpecificationExecutor<Resume> {

    @Query("SELECT COUNT(r) FROM Resume r WHERE r.createdAt BETWEEN :fromDate AND :toDate")
    long countByCreatedDate(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
