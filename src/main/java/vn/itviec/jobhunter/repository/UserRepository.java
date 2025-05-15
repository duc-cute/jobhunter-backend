package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Company;
import vn.itviec.jobhunter.domain.User;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String userName);

    boolean existsByEmail(String email);

    User findByRefreshTokenAndEmail(String token,String email);

    List<User> findByCompany(Company company);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :fromDate AND :toDate")
    long countByCreatedDate(@Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

}
