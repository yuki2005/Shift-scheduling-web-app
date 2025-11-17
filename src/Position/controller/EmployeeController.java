package Position.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import Position.*;
import Position.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	private final EmployeeService employeeService;
	
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	@GetMapping
	public List<Employee> getAll(){
		return employeeService.findAll();
	}
	
	@GetMapping("/{id}")
	   public Employee getById(@PathVariable int id) {
	       return employeeService.findByEmployeeNumber(id);
	}
	
	@PostMapping
	public Employee create(@RequestBody EmployeeDto dto) {
		Employee employee = dto.toEmployee();
		return employeeService.save(employee);
	}
	
	// 従業員削除
    @DeleteMapping("/{employeeNumber}")
    public void delete(@PathVariable int employeeNumber) {
        employeeService.deleteByEmployeeNumber(employeeNumber);
    }

    // 従業員更新
    @PutMapping("/{employeeNumber}")
    public Employee update(
            @PathVariable int employeeNumber,
            @RequestBody EmployeeDto dto
    ) {
        dto.setId(employeeNumber);
        return employeeService.update(dto.toEmployee());
    }
}
