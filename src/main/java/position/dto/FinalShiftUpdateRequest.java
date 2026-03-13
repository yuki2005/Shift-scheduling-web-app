package position.dto;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 保存済み確定シフトの更新リクエストDTO。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinalShiftUpdateRequest {

    private Long recordId;
    private Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment;

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Map<String, Map<String, List<AssignedStaffDto>>> getFinalAssignment() {
        return finalAssignment;
    }

    public void setFinalAssignment(Map<String, Map<String, List<AssignedStaffDto>>> finalAssignment) {
        this.finalAssignment = finalAssignment;
    }
}