package Position.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import Position.entity.FinalShiftRecordEntity;

public interface FinalShiftRecordRepository 
        extends JpaRepository<FinalShiftRecordEntity, Long> {

}
