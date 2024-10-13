//package com.team.sop_management_service.service;
//
//import com.team.sop_management_service.models.User;
//import com.team.sop_management_service.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    // Create a new user
//    public User createUser(User user) {
//        return userRepository.save(user);
//    }
//
//    // Get all users
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    // Get a user by ID
//    public Optional<User> getUserById(String id) {
//        return userRepository.findById(id);
//    }
//
//    // Update a user
//    public User updateUser(String id, User userDetails) {
//        userDetails.setId(id);
//        return userRepository.save(userDetails);
//    }
//
//    // Delete a user
//    public void deleteUser(String id) {
//        userRepository.deleteById(id);
//    }
//}
