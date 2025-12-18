package Position.dto;

import java.util.Map;
import java.util.stream.Collectors;

import Position.Employee;
import Position.Pos;

public class EmployeeDto {

    private int id;
    private String name;

    // JSON ではキーが文字列なので String にする
    private Map<String, Integer> skills;

    public EmployeeDto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, Integer> getSkills() { return skills; }
    public void setSkills(Map<String, Integer> skills) { this.skills = skills; }

    // ★ Pos enum に変換して Employee に渡す
    public Employee toEmployee() {

        Map<Pos, Integer> convertedSkills = null;

        if (skills != null) {
            convertedSkills = skills.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> Pos.valueOf(e.getKey().toUpperCase()), // ← 文字列 → Enum
                            e -> e.getValue()
                    ));
        }

        return new Employee(this.id, this.name, convertedSkills);
    }
}
