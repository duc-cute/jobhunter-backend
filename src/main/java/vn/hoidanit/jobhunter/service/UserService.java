package vn.hoidanit.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.List;
import java.util.Optional;

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

    public List<User> getAllUsers() {
        List<User> listUsers = this.userRepository.findAll();
        return  listUsers;
    }

    public User updateUser(User userUpdate) {
        User currentUser = this.getUserById(userUpdate.getId());
        if(userUpdate != null) {
            currentUser.setEmail(userUpdate.getEmail());
            currentUser.setName(userUpdate.getName());
            currentUser.setPassword(userUpdate.getPassword());

            this.userRepository.save(currentUser);
        }
        return currentUser;

    }



}
