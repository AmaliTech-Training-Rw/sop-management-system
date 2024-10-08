package com.team.authentication_service.services;

import com.team.authentication_service.dtos.CreateDepartmentRequestDto;
import com.team.authentication_service.models.Department;
import com.team.authentication_service.models.User;
import com.team.authentication_service.repositories.DepartmentRepository;
import com.team.authentication_service.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Department> getDepartmentById(int id) throws Exception {
        try {
            return departmentRepository.findById(id);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public Department addDepartment(CreateDepartmentRequestDto departmentDto) throws Exception {
        try {
            Optional<User> user = userRepository.findById(departmentDto.getHodId());

            if (!user.isPresent()) throw new Exception("Can't find the specified HOD");

            return departmentRepository.save(
                    Department
                            .builder()
                            .name(departmentDto.getDepartmentName())
                            .hod(user.get())
                            .build()
            );
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}

