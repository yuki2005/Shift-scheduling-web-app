package Position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import Position.entity.EmployeeEntity;
import Position.mapper.EmployeeMapper;
import Position.repository.EmployeeRepository;
import Position.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    public Employee findById(Long id) {
    	EmployeeEntity entity = repository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Employee not find: id =" + id));
    	
    	return EmployeeMapper.toDomain(entity);
    }
    
 // ------------ 社員番号で検索する（従来の仕様に対応） ------------
    public Employee findByEmployeeNumber(int employeeNumber) {
        EmployeeEntity entity = repository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found: number=" + employeeNumber));

        return EmployeeMapper.toDomain(entity);
    }
    
    public void deleteByEmployeeNumber(int employeeNumber) {
        repository.deleteByEmployeeNumber(employeeNumber);
    }

    public Employee update(Employee employee) {
        EmployeeEntity entity = repository.findByEmployeeNumber(employee.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        entity.setName(employee.getName());

        // skills 更新
        try {
            Map<String, Integer> skillMap = new HashMap<>();
            for (var e : employee.getSkills().entrySet()) {
                skillMap.put(e.getKey().name(), e.getValue());
            }
            entity.setSkillsJson(new ObjectMapper().writeValueAsString(skillMap));
        } catch (Exception e) {
            throw new RuntimeException("Skill JSON error");
        }

        return EmployeeMapper.toDomain(repository.save(entity));
    }
}
