package com.team.authentication_service.services;

import com.team.authentication_service.dtos.AssigneHodReqDto;
import com.team.authentication_service.dtos.CreateDepartmentRequestDto;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.DepartmentRepository;
import com.team.authentication_service.repositories.UserRepository;
import com.team.authentication_service.utils.errorHandlers.ReturnableException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;
    private UserService userService;

    public DepartmentService(
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.userService = userService;
    }

    public List<Department> getAllDepartments() throws Exception {
        try {
            return departmentRepository.findAll();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Department getDepartmentById(int id) throws Exception {
//      Query the db for department with id and throw an exception if it is non existent
        Optional<Department> department = departmentRepository.findById(id);
        if (department.isEmpty())
            throw new ReturnableException("Department doesn't exists", HttpStatus.NOT_FOUND);

        return department.get();
    }

    public void addDepartment(CreateDepartmentRequestDto departmentDto) throws Exception {
//      Query the db for a similar department and if present throw an exception
        Optional<Department> department = departmentRepository.findByName(departmentDto.getDepartmentName());
        if (department.isPresent())
            throw new ReturnableException("Department already exists", HttpStatus.NOT_FOUND);

        departmentRepository.save(
                Department.builder()
                        .name(departmentDto.getDepartmentName())
                        .build()
        );
    }

    public void assignHod(AssigneHodReqDto assigneHodReqDto) throws ReturnableException {
//      Query the db for both the user and department
        Optional<User> user = userRepository.findById(assigneHodReqDto.getHod());
        Optional<Department> department = departmentRepository.findById(assigneHodReqDto.getDepartment());

//      If either the user and department are non existent, throw an exception
        if (user.isEmpty())
            throw new ReturnableException("User doesn't exists", HttpStatus.NOT_FOUND);
        if (department.isEmpty())
            throw new ReturnableException("Department doesn't exist exists", HttpStatus.NOT_FOUND);

//      If the user isn't in that department throw an exception
        if (!department.get().equals(user.get().getDepartment()))
            throw new ReturnableException("A department's HOD has to belong to that department", HttpStatus.BAD_REQUEST);

//      Set the HOD and persist the changes to the db
        department.get().setHod(user.get());
        departmentRepository.save(department.get());
    }
}

