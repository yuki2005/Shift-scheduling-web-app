package Position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Position.entity.EmployeeEntity;
import Position.repository.EmployeeRepository;
import Position.mapper.EmployeeMapper;
import Position.Employee;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee save(Employee employee) {
        EmployeeEntity entity = EmployeeMapper.toEntity(employee);
        EmployeeEntity saved = repository.save(entity);
        return EmployeeMapper.toDomain(saved);
    }

    public List<Employee> findAll() {
        return repository.findAll().stream()
                .map(EmployeeMapper::toDomain)
                .collect(Collectors.toList());
    }
}
