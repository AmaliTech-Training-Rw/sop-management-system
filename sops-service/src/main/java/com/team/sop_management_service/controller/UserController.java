//package com.team.sop_management_service.controller;
//
//import com.team.sop_management_service.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
////@Api(value = "User Management System")
//public class UserController {
//
//    private final UserService userService;
//
//    @Autowired
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping
//    //@Operation(value = "Create a new user")
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
//    }
//
//    @GetMapping
//    //@ApiOperation(value = "Get all users")
//    public ResponseEntity<List<User>> getAllUsers() {
//        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}")
//    //@ApiOperation(value = "Get a user by ID")
//    public ResponseEntity<User> getUserById(@PathVariable String id) {
//        return userService.getUserById(id)
//                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
//                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    @PutMapping("/{id}")
//   // @ApiOperation(value = "Update a user")
//    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
//        return new ResponseEntity<>(userService.updateUser(id, userDetails), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//   // @ApiOperation(value = "Delete a user")
//    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
//        userService.deleteUser(id);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//}
