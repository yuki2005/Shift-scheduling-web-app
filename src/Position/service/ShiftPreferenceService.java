package Position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Position.ShiftPreference;
import Position.Employee;
import Position.entity.ShiftPreferenceEntity;
import Position.repository.ShiftPreferenceRepository;
import Position.mapper.ShiftPreferenceMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftPreferenceService {

    private final ShiftPreferenceRepository repository;
    private final EmployeeService employeeService;

    public ShiftPreferenceService(ShiftPreferenceRepository repository,
                                  EmployeeService employeeService) {
        this.repository = repository;
        this.employeeService = employeeService;
    }

    // 保存
    public ShiftPreference save(ShiftPreference pref) {
        ShiftPreferenceEntity entity = ShiftPreferenceMapper.toEntity(pref);
        ShiftPreferenceEntity saved = repository.save(entity);
        
        //注意
        Employee employee = employeeService.findByEmployeeNumber(pref.getEmployee().getId());
        return ShiftPreferenceMapper.toDomain(saved, employee);
    }

    // 指定社員の全希望
    public List<ShiftPreference> findByEmployeeNumber(int employeeNumber) {
        Employee emp = employeeService.findByEmployeeNumber(employeeNumber);

        return repository.findByEmployeeNumber(employeeNumber).stream()
                .map(e -> ShiftPreferenceMapper.toDomain(e, emp))
                .collect(Collectors.toList());
    }

    // 指定日付の全希望
    public List<ShiftPreference> findByDate(LocalDate date) {
        return repository.findByDate(date).stream()
                .map(e -> {
                    Employee emp = employeeService.findByEmployeeNumber(e.getEmployeeNumber());
                    return ShiftPreferenceMapper.toDomain(e, emp);
                })
                .collect(Collectors.toList());
    }

    // 一件取得
    public ShiftPreference findOne(int employeeNumber, LocalDate date) {
        ShiftPreferenceEntity entity =
                repository.findByEmployeeNumberAndDate(employeeNumber, date);

        if (entity == null) {
        	return null;
        }

        Employee emp = employeeService.findByEmployeeNumber(employeeNumber);
        return ShiftPreferenceMapper.toDomain(entity, emp);
    }
}
