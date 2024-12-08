package vn.itviec.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.itviec.jobhunter.domain.Skill;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> , JpaSpecificationExecutor<Skill> {
    boolean existsByName(String skill);

    List<Skill> findByIdIn(List<Long> id);
}
