package com.evyatra.repository;

import com.evyatra.model.Review;
import com.evyatra.model.EvStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByStationOrderByCreatedAtDesc(EvStation station);

    boolean existsByUserIdAndStationId(Long userId, Long stationId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.station.id = :stationId")
    Double getAverageRatingByStationId(Long stationId);

    Long countByStationId(Long stationId);
}