package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long>, JpaSpecificationExecutor<Company> {


}
