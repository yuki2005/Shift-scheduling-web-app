package Position.mapper;

import Position.Employee;
import Position.Pos;
import Position.entity.EmployeeEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class EmployeeMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // --- Entity → Domain ---
    public static Employee toDomain(EmployeeEntity entity) {
        Map<Pos, Integer> skills = new HashMap<>();

        try {
            // JSONをMap<String, Integer>に変換
            Map<String, Integer> jsonMap =
                    objectMapper.readValue(entity.getSkillsJson(), Map.class);

            // Pos列挙型に変換して格納
            for (Map.Entry<String, Integer> e : jsonMap.entrySet()) {
                skills.put(Pos.valueOf(e.getKey()), e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Domainモデルを返す
        return new Employee(entity.getId().intValue(), entity.getName(), skills);
    }

    // --- Domain → Entity ---
    public static EmployeeEntity toEntity(Employee employee) {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setId((long) employee.getId());
        entity.setName(employee.getName());

        try {
            // Map<Pos, Integer> を Map<String, Integer> に変換してJSON化
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
