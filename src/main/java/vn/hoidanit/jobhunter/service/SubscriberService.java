package vn.hoidanit.jobhunter.service;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
  private final SubscriberRepository subscriberRepository;

  private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }



    public boolean isEmailExist(String email) {
       return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber save(Subscriber dto) {
        if(!CollectionUtils.isEmpty(dto.getSkills())) {
            List<Long> listSkills = dto.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> listSkillDb = this.skillRepository.findByIdIn(listSkills);
            dto.setSkills(listSkillDb);

        }
        return this.subscriberRepository.save(dto);
    }
    public  Subscriber update(Subscriber sub,Subscriber subDB) {
        if(!CollectionUtils.isEmpty(sub.getSkills())) {
            List<Long> listSkills = sub.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> listSkillDb = this.skillRepository.findByIdIn(listSkills);
            subDB.setSkills(listSkillDb);
        }
        return this.subscriberRepository.save(subDB);

    }
    public Optional<Subscriber> fetchById(Long id) {
        Optional<Subscriber> OpSubscriber = this.subscriberRepository.findById(id);

        return OpSubscriber;
    }

    public void delete(Long id) {
        this.subscriberRepository.deleteById(id);
    }

    public ResultPaginationDTO paging(Specification<Subscriber> spec, Pageable pageable) {
        Page<Subscriber> pSubscriber = this.subscriberRepository.findAll(spec,pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPages(pSubscriber.getTotalPages());
        mt.setTotal(pSubscriber.getTotalElements());
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        result.setMeta(mt);
        result.setResult(pSubscriber.getContent());
        return result;
    }

}
