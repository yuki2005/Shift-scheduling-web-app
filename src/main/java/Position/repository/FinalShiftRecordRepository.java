package Position.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Position.entity.FinalShiftRecordEntity;

@Repository
public interface FinalShiftRecordRepository 
        extends JpaRepository<FinalShiftRecordEntity, Long> {
	List<FinalShiftRecordEntity> findAllByOrderByDateDesc();
	
	//日付で検索
	List<FinalShiftRecordEntity> findByDate(LocalDate date);
	
	void deleteByDate(LocalDate date);
	
	boolean existsByDate(LocalDate date);
}
