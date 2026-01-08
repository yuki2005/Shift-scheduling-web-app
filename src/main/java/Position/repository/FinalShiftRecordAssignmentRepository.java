package Position.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Position.entity.*;

@Repository
public interface FinalShiftRecordAssignmentRepository extends JpaRepository<FinalShiftRecordAssignmentEntity, Long> {
	
	//シフト結果を取り出す
    List<FinalShiftRecordAssignmentEntity> findByRecordId(Long recordId);
    
    //シフトを削除する
    void deleteByRecord(FinalShiftRecordEntity record);
    
}
