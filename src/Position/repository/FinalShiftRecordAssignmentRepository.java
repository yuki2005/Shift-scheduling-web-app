package Position.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Position.entity.FinalShiftRecordAssignmentEntity;

@Repository
public interface FinalShiftRecordAssignmentRepository extends JpaRepository<FinalShiftRecordAssignmentEntity, Long> {
    List<FinalShiftRecordAssignmentEntity> findByRecordId(Long recordId);
}
