package position.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import position.entity.EmployeeEntity;
import position.mapper.EmployeeMapper;
import position.model.Employee;
import position.repository.EmployeeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 従業員情報に関する業務ロジックを管理するサービスクラス。
 * Controller と Repository の間に位置し、
 * 従業員情報の永続化、取得、更新といった処理を担当する。
 *
 * ・DBの主キー（ID）と業務上の一意キー（社員番号）を使い分けて管理する
 * ・ドメインモデルとEntity間の変換処理を集約する
 */

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository repository;
    
    // EmployeeRepository を注入するコンストラクタ
    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }
    
    /**
     * 従業員ドメインモデルを Entity に変換し、DBへ永続化する。
     *
     * @param employee 保存対象の従業員情報
     * @return 保存後の従業員情報
     */
    public Employee save(Employee employee) {
        EmployeeEntity entity = EmployeeMapper.toEntity(employee);
        EmployeeEntity saved = repository.save(entity);
        return EmployeeMapper.toDomain(saved);
    }
    
    /**
     * 登録されている全ての従業員情報を取得する。
     *
     * @return 従業員情報の一覧
     */
    public List<Employee> findAll() {
        return repository.findAll().stream()
                .map(EmployeeMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * DBの主キー（ID）を用いて従業員情報を取得する。
     *
     * @param id 従業員ID（DBの主キー）
     * @return 該当する従業員情報
     * @throws RuntimeException 該当する従業員が存在しない場合
     */
    public Employee findById(Long id) {
    	EmployeeEntity entity = repository.findById(id)
    			.orElseThrow(() -> new RuntimeException("Employee not find: id =" + id));
    	
    	return EmployeeMapper.toDomain(entity);
    }
    
    /**
     * 社員番号（業務上の一意キー）を用いて従業員情報を取得する。
     *
     * @param employeeNumber 社員番号
     * @return 該当する従業員情報
     * @throws RuntimeException 該当する従業員が存在しない場合
     */
    public Employee findByEmployeeNumber(int employeeNumber) {
        EmployeeEntity entity = repository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("Employee not found: number=" + employeeNumber));

        return EmployeeMapper.toDomain(entity);
    }
    
    /**
     * 社員番号を指定して従業員情報を削除する。
     *
     * @param employeeNumber 削除対象の社員番号
     */
    public void deleteByEmployeeNumber(int employeeNumber) {
        repository.deleteByEmployeeNumber(employeeNumber);
    }
    
    /**
     * 社員番号をキーとして従業員情報を更新する。
     *
     * ・氏名およびスキル情報を更新対象とする
     * ・スキル情報は JSON 形式に変換して保存する
     *
     * @param employee 更新後の従業員情報
     * @return 更新後の従業員情報
     * @throws RuntimeException 対象の従業員が存在しない場合
     */
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
