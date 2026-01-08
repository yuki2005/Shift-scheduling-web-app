package Position.mapper;

import Position.Employee;
import Position.Pos;
import Position.entity.EmployeeEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * 従業員情報に関する Domain ↔ Entity の変換を行う Mapper クラス。
 *
 * ・Domain モデルではスキル情報を Map<Pos, Integer> として保持
 * ・Entity では JSON 文字列として永続化する
 *
 * 業務ロジックは Service 層に集約し、
 * 本クラスはデータ構造の変換責務のみに限定する。
 */
public class EmployeeMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * EmployeeEntity を Employee（ドメインモデル）へ変換する。
     *
     * ・スキル情報は JSON 文字列から Map<Pos, Integer> に変換する
     * ・ドメインモデルで使用する ID は DB の主キーではなく社員番号とする
     *
     * @param entity 変換元の従業員Entity
     * @return 変換後の従業員ドメインモデル
     */
    public static Employee toDomain(EmployeeEntity entity) {
        Map<Pos, Integer> skills = new HashMap<>();

        try {
            String json = entity.getSkillsJson();
            if (json != null && !json.isBlank()) {
                Map<String, Integer> jsonMap =
                        objectMapper.readValue(
                                json,
                                new TypeReference<Map<String, Integer>>() {}
                        );

                for (Map.Entry<String, Integer> e : jsonMap.entrySet()) {
                    skills.put(Pos.valueOf(e.getKey()), e.getValue());
                }
            }
        } catch (Exception e) {
            // JSON形式が不正な場合は空のスキル情報として扱う
            e.printStackTrace();
        }

        // Domain モデルでは DB の主キーではなく社員番号を識別子として使用する
        return new Employee(entity.getEmployeeNumber(), entity.getName(), skills);
    }

    /**
     * Employee（ドメインモデル）を EmployeeEntity へ変換する。
     *
     * ・社員番号と氏名を Entity に設定
     * ・スキル情報は Map から JSON 文字列に変換して保存する
     * ・DB の主キー（id）は新規作成時には設定せず、自動採番に任せる
     *
     * @param employee 変換元の従業員ドメインモデル
     * @return 変換後の従業員Entity
     */
    public static EmployeeEntity toEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();

        entity.setEmployeeNumber(employee.getId());
        entity.setName(employee.getName());

        try {
            Map<String, Integer> stringMap = new HashMap<>();
            for (Map.Entry<Pos, Integer> e : employee.getSkills().entrySet()) {
                stringMap.put(e.getKey().name(), e.getValue());
            }
            entity.setSkillsJson(objectMapper.writeValueAsString(stringMap));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return entity;
    }
}
