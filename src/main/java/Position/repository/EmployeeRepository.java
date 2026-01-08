package Position.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Position.entity.EmployeeEntity;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
	
	//社員番号で社員情報を取得
    Optional<EmployeeEntity> findByEmployeeNumber(int employeeNumber);
    
    //社員番号で社員情報を削除
    void deleteByEmployeeNumber(int employeeNumber);
}
