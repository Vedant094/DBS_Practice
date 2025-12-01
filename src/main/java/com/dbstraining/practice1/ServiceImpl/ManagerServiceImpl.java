package com.dbstraining.practice1.ServiceImpl;

import com.dbstraining.practice1.Model.Manager;
import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;
import com.dbstraining.practice1.Repository.ManagerRepository;
import com.dbstraining.practice1.Repository.RequestRepository;
import com.dbstraining.practice1.Repository.UserRepository;
import com.dbstraining.practice1.Service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

        @Override
        public Manager createManager(Manager manager) {
            return managerRepository.save(manager);
        }

        @Override
        public Optional<Manager> getManagerById(Long id) {
            return Optional.ofNullable(managerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Manager not found")));
        }

    @Override
    public void approveRequest(Long requestId,Long managerId) {
        Requests requests=requestRepository.findById(requestId)
                .orElseThrow(()->new RuntimeException("Request not Found"));
        requests.setStatus("APPROVED");
        requests.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(requests);

        User user=requests.getUser();
        if(user!=null){
            userRepository.delete(user);
        }
    }

    @Override
    public void rejectRequest(Long requestId,Long managerId) {
        Requests requests=requestRepository.findById(requestId)
                .orElseThrow(()->new RuntimeException("Request not Found"));
        requests.setStatus("REJECTED");
        requests.setUpdatedAt(LocalDateTime.now());
        requestRepository.save(requests);
    }

    @Override
         public boolean loginAdmin(String email, String password) {
            Manager manager=managerRepository.findByEmail(email);
            return manager!=null && manager.getPassword().equals(password);
         }

    @Override
    public Manager getByEmail(String email) {
        return managerRepository.findByEmail(email);
    }


    @Override
         public List<Manager> getAllManagers() {
            return managerRepository.findAll();
        }

         @Override
         public List<User> getUsersUnderManager(Long managerId) {
            return userRepository.findByManagerId(managerId);
        }

         @Override
         public List<Requests> getRequestsForManager(Long managerId) {
            return requestRepository.findRequestsByManagerId(managerId);
        }
    }

