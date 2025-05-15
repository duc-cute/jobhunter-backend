package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Job;
import vn.itviec.jobhunter.domain.Skill;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job,Long> , JpaSpecificationExecutor<Job> {
    List<Job> findBySkillsIn(List<Skill> skill);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.createdAt BETWEEN :fromDate AND :toDate")
    long countByCreatedDate(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
