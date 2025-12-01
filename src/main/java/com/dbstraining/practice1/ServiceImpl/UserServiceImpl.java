package com.dbstraining.practice1.ServiceImpl;

import com.dbstraining.practice1.Model.Manager;
import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;
import com.dbstraining.practice1.Repository.ManagerRepository;
import com.dbstraining.practice1.Repository.UserRepository;
import com.dbstraining.practice1.Service.RequestService;
import com.dbstraining.practice1.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;

    private final RequestService requestService;

    UserServiceImpl(UserRepository userRepository,ManagerRepository managerRepository,RequestService requestService){
        this.requestService=requestService;
        this.managerRepository=managerRepository;
        this.userRepository=userRepository;
    }

    @Override
    public Requests requestAccountDeletion(Long userId) {
        return requestService.createDeletionRequest(userId);
    }

    @Override
    public boolean loginUser(String email, String password) {
        User user=userRepository.findByEmail(email);
        return user!=null &&user.getPassword().equals(password);
    }
    
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(User user, Long managerId) {
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        user.setManager(manager);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Requests> getRequestsByUser(Long userId) {
        return userRepository.findById(userId)
                .map(User::getRequests)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
