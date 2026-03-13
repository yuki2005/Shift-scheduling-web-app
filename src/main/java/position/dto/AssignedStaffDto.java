package position.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * finalAssignment 内で扱う担当者DTO。
 *
 * フロントから送られる staff オブジェクトを
 * 型安全に扱うために使用する。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignedStaffDto {

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}