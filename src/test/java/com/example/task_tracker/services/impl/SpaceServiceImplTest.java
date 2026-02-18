package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.domain.entities.*;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.SpaceMapper;
import com.example.task_tracker.repositories.SpacePermissionRepository;
import com.example.task_tracker.repositories.SpaceRepository;
import com.example.task_tracker.repositories.UserRepository;
import com.example.task_tracker.services.PermissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceServiceImplTest {

    @Mock
    private PermissionService permissionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpacePermissionRepository spacePermissionRepository;

    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private SpaceMapper spaceMapper;
    @InjectMocks
    private SpaceServiceImpl spaceService;

    private final UUID spaceId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Yakındaki alanları getirme testi")
    void getNearbySpaces_ShouldReturnList() {
        // GIVEN
        double lat = 41.0, lon = 29.0, radius = 500.0;
        Space space = new Space();
        when(spaceRepository.findNearbySpacesAccessibleToUser(eq(userId), any(Point.class), eq(radius)))
                .thenReturn(List.of(space));
        when(spaceMapper.toDto(any())).thenReturn(new SpaceDto(spaceId, "Yakın Alan",
                null, userId, null, null, null));

        // WHEN
        List<SpaceDto> result = spaceService.getNearbySpaces(lat, lon, radius, userId);

        // THEN
        assertFalse(result.isEmpty());
        verify(spaceRepository).findNearbySpacesAccessibleToUser(eq(userId), any(Point.class), eq(radius));
    }

    @Test
    @DisplayName("ID ile alan getirme - Başarılı")
    void getSpace_ShouldReturnDto_WhenFound() {
        // GIVEN
        Space space = new Space();
        SpaceDto dto = new SpaceDto(spaceId, "Bulunan Alan", null, userId, null, null, null);

        doNothing().when(permissionService).validateAccess(spaceId, userId, PermissionType.VIEWER);
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(spaceMapper.toDto(space)).thenReturn(dto);

        // WHEN
        SpaceDto result = spaceService.getSpace(spaceId, userId);

        // THEN
        assertEquals("Bulunan Alan", result.title());
    }

    @Test
    @DisplayName("ID ile alan getirme - Hata: Alan Bulunamadı")
    void getSpace_ShouldThrowException_WhenSpaceNotFound() {
        // GIVEN
        doNothing().when(permissionService).validateAccess(spaceId, userId, PermissionType.VIEWER);

        // Repository boş dönsün
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        // WHEN-THEN
        // Metod çağrıldığında ResourceNotFoundException fırlatmasını bekleniyor
        assertThrows(ResourceNotFoundException.class, () -> {
            spaceService.getSpace(spaceId, userId);
        });

        verify(spaceMapper, never()).toDto(any());
    }

}