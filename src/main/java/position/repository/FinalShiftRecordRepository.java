package position.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import position.entity.FinalShiftRecordEntity;

@Repository
public interface FinalShiftRecordRepository 
        extends JpaRepository<FinalShiftRecordEntity, Long> {
	
	//全てのシフトを取得
	List<FinalShiftRecordEntity> findAllByOrderByDateDesc();
	
	//日付で検索して取得
	List<FinalShiftRecordEntity> findByDate(LocalDate date);
	
	//日付で削除
	void deleteByDate(LocalDate date);
	
	//対応する日付のシフトが存在するかを取得
	boolean existsByDate(LocalDate date);
}
