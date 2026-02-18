package com.example.task_tracker.repositories;

import com.example.task_tracker.domain.entities.Space;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID> {

    // Kullanıcının sahibi olduğu alanlar
    List<Space> findAllByOwnerId(UUID ownerId);

    //Kullanıcının sahibi olduğu veya yetkilendirildiği tüm alanları getirme
    @Query("SELECT DISTINCT s FROM Space s LEFT JOIN s.permissions p WHERE s.owner.id = :userId OR p.user.id = :userId")
    List<Space> findAllAvailableSpaces(@Param("userId") UUID userId);
    

    @Query(value = "SELECT DISTINCT s.* FROM spaces s " +
            "LEFT JOIN space_permissions p ON s.id = p.space_id " +
            "WHERE (s.owner_id = :userId OR p.user_id = :userId) " +
            "AND ST_DWithin(s.center_location, :userLocation, :distance, true)",
            nativeQuery = true)
    List<Space> findNearbySpacesAccessibleToUser(@Param("userId") UUID userId,
                                                 @Param("userLocation") Point userLocation,
                                                 @Param("distance") double distance);
}



