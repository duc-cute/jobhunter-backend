package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final CompanyService companyService;

    public UserService(UserRepository userRepository,
                       CompanyService companyService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    public User handleCreateUser(User user) {
        if(user.getCompany() != null) {
            Company company = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(company != null ? company : null);

        }

       return this.userRepository.save(user);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);

    }
    public boolean isEmailExist(String email) {
       return this.userRepository.existsByEmail(email);
    }

    public User handleGetUserByUserName(String userName) {
       return this.userRepository.findByEmail(userName);
    }

    public User getUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;

    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pUser = this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pUser.getTotalElements());
        mt.setPages(pUser.getTotalPages());
        result.setMeta(mt);


        List<ResUserDTO> listUser = pUser.getContent()
                        .stream().map(item -> new ResUserDTO(
                                item.getId(),
                                item.getEmail(),
                                item.getName(),
                                item.getAddress(),
                                item.getAge(),
                                item.getGender(),
                                item.getCreatedAt(),
                                item.getUpdatedAt(),
                                new ResUserDTO.CompanyUser(
                                        item.getCompany() != null ? item.getCompany().getId() : 0,
                                        item.getCompany() != null ? item.getCompany().getName(): ""
                                ))).collect(Collectors.toList()

                );
        result.setResult(listUser);
        return  result;
    }

    public User updateUser(User userUpdate) {
        User currentUser = this.getUserById(userUpdate.getId());
        if(userUpdate != null) {
            currentUser.setAge(userUpdate.getAge());
            currentUser.setName(userUpdate.getName());
            currentUser.setAddress(userUpdate.getAddress());
            currentUser.setGender(userUpdate.getGender());

            if(currentUser.getCompany() != null) {
                Company company = this.companyService.getCompanyById(currentUser.getCompany().getId());
                currentUser.setCompany(company != null ? company : null);

            }


            this.userRepository.save(currentUser);
        }
        return currentUser;

    }
    public void updateUserToken(String token,String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res  = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        return res;

    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res  = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser  = new ResUpdateUserDTO.CompanyUser();

        res.setAge(user.getAge());
        res.setName(user.getName());

        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());

        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        return res;

    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res  = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser  = new ResUserDTO.CompanyUser();
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        return res;

    }

    public User getUserByRefreshTokenAndEmail(String token,String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token,email);
    }


}
