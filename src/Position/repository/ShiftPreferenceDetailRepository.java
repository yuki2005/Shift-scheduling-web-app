package Position.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Position.entity.ShiftPreferenceDetailEntity;

@Repository
public interface ShiftPreferenceDetailRepository extends JpaRepository<ShiftPreferenceDetailEntity, Long> {
    List<ShiftPreferenceDetailEntity> findByHeaderId(Long headerId);
}
