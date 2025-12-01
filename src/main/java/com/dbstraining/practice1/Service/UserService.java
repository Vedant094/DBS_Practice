package com.dbstraining.practice1.Service;

import com.dbstraining.practice1.Model.Requests;
import com.dbstraining.practice1.Model.User;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;

import java.util.List;
import java.util.Optional;

public interface UserService {

   // User updateUser(Long userId,User user);
    //void deleteUser(Long userId);
    //Optional<User> getUserId(Long userId);
//    List<User> getAllUsers();
    Requests requestAccountDeletion(Long userId);
    boolean loginUser(String email,String password);

    public User getByEmail(String email);
    User createUser(User user, Long managerId); // Assign user to a manager
    User getUserById(Long id);
    List<User> getAllUsers();
    List<Requests> getRequestsByUser(Long userId);
    }


