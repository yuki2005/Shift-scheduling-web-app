package Position.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import Position.entity.ShiftPreferenceEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftPreferenceRepository extends JpaRepository<ShiftPreferenceEntity, Long> {

    // ある社員の全希望を取得
    List<ShiftPreferenceEntity> findByEmployeeNumber(Integer employeeNumber);

    // 特定の日付の希望を取得
    List<ShiftPreferenceEntity> findByDate(LocalDate date);

    // 社員番号＋日付で取得（1件だけ）
    ShiftPreferenceEntity findByEmployeeNumberAndDate(Integer employeeNumber, LocalDate date);
}
