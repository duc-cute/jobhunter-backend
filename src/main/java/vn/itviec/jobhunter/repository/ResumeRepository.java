package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long> , JpaSpecificationExecutor<Resume> {

}