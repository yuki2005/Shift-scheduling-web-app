package Position.mapper;

import Position.Employee;
import Position.Pos;
import Position.entity.EmployeeEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class EmployeeMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // --- Entity → Domain ---
    public static Employee toDomain(EmployeeEntity entity) {
        Map<Pos, Integer> skills = new HashMap<>();

        try {
            String json = entity.getSkillsJson();
            if (json != null && !json.isBlank()) {
                Map<String, Integer> jsonMap =
                        objectMapper.readValue(json, new TypeReference<Map<String, Integer>>() {});
                for (Map.Entry<String, Integer> e : jsonMap.entrySet()) {
                    skills.put(Pos.valueOf(e.getKey()), e.getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ★ ここで使うIDは PK ではなく「社員番号」
        return new Employee(entity.getEmployeeNumber(), entity.getName(), skills);
    }

    // --- Domain → Entity ---
    public static EmployeeEntity toEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();

        // ★ DBのidは新規作成時は触らない（自動採番に任せる）
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
