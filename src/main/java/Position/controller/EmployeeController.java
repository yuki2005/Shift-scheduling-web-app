package Position.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import Position.*;
import Position.dto.EmployeeDto;
import Position.service.EmployeeService;

/**
 * 従業員情報に関する REST API を提供する Controller クラス。
 *
 * ・従業員の取得、登録、更新、削除を担当する
 * ・業務ロジックは Service 層に委譲し、
 *   本クラスはリクエスト／レスポンスの仲介に専念する
 *
 * エンドポイント: /api/employees
 */

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
	
	// 従業員情報に関する業務ロジックを扱う Service
	private final EmployeeService employeeService;
	
	// EmployeeService を注入するコンストラクタ
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	/**
	 * 全ての従業員情報を取得する。
	 *
	 * @return 従業員情報の一覧
	 */
	@GetMapping
	public List<Employee> getAll(){
		return employeeService.findAll();
	}
	
	/**
	 * 社員番号を指定して従業員情報を取得する。
	 *
	 * @param id 社員番号（業務上の一意キー）
	 * @return 該当する従業員情報
	 */
	@GetMapping("/{id}")
	   public Employee getById(@PathVariable int id) {
	       return employeeService.findByEmployeeNumber(id);
	}
	
	/**
	 * 新しい従業員を登録する。
	 *
	 * @param dto フロントエンドから送信された従業員情報
	 * @return 登録後の従業員情報
	 */
	@PostMapping
	public Employee create(@RequestBody EmployeeDto dto) {
		Employee employee = dto.toEmployee();
		return employeeService.save(employee);
	}
	
	/**
	 * 社員番号を指定して従業員情報を削除する。
	 *
	 * @param employeeNumber 削除対象の社員番号
	 */
    @DeleteMapping("/{employeeNumber}")
    public void delete(@PathVariable int employeeNumber) {
        employeeService.deleteByEmployeeNumber(employeeNumber);
    }

    /**
     * 社員番号を指定して従業員情報を更新する。
     *
     * @param employeeNumber 更新対象の社員番号
     * @param dto            更新後の従業員情報
     * @return 更新後の従業員情報
     */
    @PutMapping("/{employeeNumber}")
    public Employee update(
            @PathVariable int employeeNumber,
            @RequestBody EmployeeDto dto
    ) {
        dto.setId(employeeNumber);
        return employeeService.update(dto.toEmployee());
    }
}
