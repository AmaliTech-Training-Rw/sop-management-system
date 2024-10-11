package com.team.authentication_service.services;

import com.team.authentication_service.dtos.AssigneHodReqDto;
import com.team.authentication_service.dtos.CreateDepartmentRequestDto;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.DepartmentRepository;
import com.team.authentication_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DepartmentService {
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;
    private UserService userService;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository, UserService userService
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
        try {
            Optional<Department> department = departmentRepository.findById(id);

            if (department.isPresent()) throw new Exception("Department doesn't exists");

            return department.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Department addDepartment(CreateDepartmentRequestDto departmentDto) throws Exception {
        return departmentRepository.save(
                Department
                        .builder()
                        .name(departmentDto.getDepartmentName())
                        .build()
        );
    }

    public void assigneHod(AssigneHodReqDto assigneHodReqDto) throws Exception {
        Optional<User> user = userRepository.findById(assigneHodReqDto.getHod());
        Optional<Department> department = departmentRepository.findById(assigneHodReqDto.getDepartment());

        if (user.isEmpty()) throw new Exception("User doesn't exists");
        if (department.isEmpty()) throw new Exception("Department doesn't exist exists");

        department.get().setHod(user.get());
        departmentRepository.save(department.get());
    }
}

