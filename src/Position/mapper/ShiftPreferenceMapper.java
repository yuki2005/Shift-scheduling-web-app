package Position.mapper;

import Position.ShiftPreference;
import Position.ShiftTime;
import Position.entity.ShiftPreferenceEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class ShiftPreferenceMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    // Domain → Entity
    public static ShiftPreferenceEntity toEntity(ShiftPreference pref) {
        ShiftPreferenceEntity entity = new ShiftPreferenceEntity();

        entity.setEmployeeNumber(pref.getEmployee().getId());
        entity.setDate(pref.getDate());

        try {
            Map<String, Integer> jsonMap = new HashMap<>();
            for (var e : pref.getAvailabilityMap().entrySet()) {
                jsonMap.put(e.getKey().name(), e.getValue());
            }
            entity.setPreferenceJson(mapper.writeValueAsString(jsonMap));
        } catch (Exception e) {
            throw new RuntimeException("JSON変換エラー: " + e.getMessage());
        }

        return entity;
    }

    // Entity → Domain
    public static ShiftPreference toDomain(ShiftPreferenceEntity entity, Position.Employee employee) {
        try {
            Map<String, Integer> jsonMap =
                    mapper.readValue(entity.getPreferenceJson(), Map.class);

            Map<ShiftTime, Integer> availability = new HashMap<>();

            for (var e : jsonMap.entrySet()) {
                availability.put(ShiftTime.valueOf(e.getKey()), e.getValue());
            }

            return new ShiftPreference(employee, availability, entity.getDate());

        } catch (Exception e) {
            throw new RuntimeException("JSON復元エラー: " + e.getMessage());
        }
    }
}
