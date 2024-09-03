package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.*;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
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
        MetaDTO mt = new MetaDTO();
        ResultPaginationDTO result = new ResultPaginationDTO();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pUser.getTotalElements());
        mt.setPages(pUser.getTotalPages());
        result.setMetaDTO(mt);
        List<ResUserDTO> listUser = pUser.getContent()
                        .stream().map(item -> new ResUserDTO(
                                item.getId(),
                                item.getEmail(),
                                item.getName(),
                                item.getAddress(),
                                item.getAge(),
                                item.getGender(),
                                item.getCreatedAt(),
                                item.getUpdatedAt())).collect(Collectors.toList());
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
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        return res;

    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res  = new ResUpdateUserDTO();
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        return res;

    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res  = new ResUserDTO();
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        return res;

    }


}
