package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.HrRegister;

@Repository
public interface HrRegisterRepository extends JpaRepository<HrRegister,Long> , JpaSpecificationExecutor<HrRegister> {



}
