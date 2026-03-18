package position.dto;

import java.util.Map;
import java.util.stream.Collectors;

import position.model.Employee;
import position.model.Pos;

/**
 * 従業員情報をフロントエンドとの通信に使用する DTO クラス。
 *
 * ・API経由で受け取る / 返却するデータ構造を表す
 * ・JSON形式との互換性を考慮し、
 *   スキル情報は Map<String, Integer> として保持する
 *
 * ドメインモデル（Employee）への変換は本クラス内で行い、
 * Controller での変換処理を簡潔にする。
 */
public class EmployeeDto {

    // 従業員の社員番号（業務上の一意キー）
    private int id;

    // 従業員名
    private String name;

    // スキル情報（JSONではキーが文字列となるため String を使用）
    private Map<String, Integer> skills;

    public EmployeeDto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Integer> getSkills() { return skills; }
    public void setSkills(Map<String, Integer> skills) { this.skills = skills; }

    /**
     * DTO を Employee（ドメインモデル）へ変換する。
     *
     * ・スキルのキーは文字列から Pos enum へ変換する
     * ・JSON入力を想定し、大文字変換を行った上で enum にマッピングする
     *
     * @return 変換後の従業員ドメインモデル
     */
    public Employee toEmployee() {

        Map<Pos, Integer> convertedSkills = null;

        if (skills != null) {
            convertedSkills = skills.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Pos.valueOf(e.getKey().toUpperCase()),
                            e -> e.getValue()
                    ));
        }

        return new Employee(this.id, this.name, convertedSkills);
    }
    
    public static EmployeeDto fromEmployee(Employee employee) {
	    EmployeeDto dto = new EmployeeDto();
	    dto.setId(employee.getId());
	    dto.setName(employee.getName());

	    Map<String, Integer> skills = employee.getSkills().entrySet().stream()
	            .collect(Collectors.toMap(
	                    e -> e.getKey().name(),
	                    Map.Entry::getValue
	            ));
	    dto.setSkills(skills);

	    return dto;
	}
}
