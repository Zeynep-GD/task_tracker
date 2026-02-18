package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.domain.entities.PermissionType;
import com.example.task_tracker.domain.entities.Space;
import com.example.task_tracker.domain.entities.User;
import com.example.task_tracker.domain.entities.SpacePermission;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.SpaceMapper;
import com.example.task_tracker.repositories.SpaceRepository;
import com.example.task_tracker.repositories.SpacePermissionRepository;
import com.example.task_tracker.repositories.UserRepository;
import com.example.task_tracker.services.PermissionService;
import com.example.task_tracker.services.SpaceService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpaceServiceImpl implements SpaceService {
    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;
    private final PermissionService permissionService;
    private final UserRepository userRepository;
    private final SpacePermissionRepository spacePermissionRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    @Override
    public List<SpaceDto> listSpaces(UUID userId) {
        return spaceRepository.findAllAvailableSpaces(userId).stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public SpaceDto createSpace(SpaceDto spaceDto, UUID userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        Space space = spaceMapper.fromDto(spaceDto);
        space.setOwner(owner);

        if (spaceDto.centerLocation() != null) {
            space.setCenterLocation(geometryFactory.createPoint(new Coordinate(
                    spaceDto.centerLocation().longitude(),
                    spaceDto.centerLocation().latitude()
            )));
        }

        space.setCreated(LocalDateTime.now());
        space.setUpdated(LocalDateTime.now());

        Space savedSpace = spaceRepository.save(space);

        SpacePermission permission = new SpacePermission();
        permission.setSpace(savedSpace);
        permission.setUser(owner);
        permission.setPermissionType(PermissionType.OWNER);
        spacePermissionRepository.save(permission);

        return spaceMapper.toDto(savedSpace);
    }
    @Override
    @Cacheable(value = "spaces", key = "#id")
    public SpaceDto getSpace(UUID id, UUID userId) {
        permissionService.validateAccess(id, userId, PermissionType.VIEWER);
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alan bulunamadı"));
        return spaceMapper.toDto(space);
    }

    @Override
    @Transactional
    @CacheEvict(value = "spaces", key = "#id")
    public SpaceDto updateSpace(UUID id, UUID userId, SpaceDto spaceDto) {
        //Güncelleme için en az EDITOR yetkisi gerekir
        permissionService.validateAccess(id, userId, PermissionType.EDITOR);

        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek Alan bulunamadı! ID: " + id));

        existingSpace.setTitle(spaceDto.title());
        existingSpace.setDescription(spaceDto.description());

        if (spaceDto.centerLocation() != null) {
            existingSpace.setCenterLocation(geometryFactory.createPoint(new Coordinate(
                    spaceDto.centerLocation().longitude(),
                    spaceDto.centerLocation().latitude()
            )));
        }

        existingSpace.setUpdated(LocalDateTime.now());

        return spaceMapper.toDto(spaceRepository.save(existingSpace));
    }

    @Override
    @Transactional
    @CacheEvict(value = "spaces", key = "#id")
    public SpaceDto patchSpace(UUID id, UUID userId, SpaceDto spaceDto) {
        permissionService.validateAccess(id, userId, PermissionType.EDITOR);

        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alan bulunamadı! ID: " + id));

        if (spaceDto.title() != null) existingSpace.setTitle(spaceDto.title());
        if (spaceDto.description() != null) existingSpace.setDescription(spaceDto.description());

        if (spaceDto.centerLocation()!= null) {
            existingSpace.setCenterLocation(geometryFactory.createPoint(new Coordinate(
                    spaceDto.centerLocation().longitude(),
                    spaceDto.centerLocation().latitude()
            )));
        }

        existingSpace.setUpdated(LocalDateTime.now());
        return spaceMapper.toDto(spaceRepository.save(existingSpace));
    }

    @Override
    @Transactional
    @CacheEvict(value = "spaces", key = "#id")
    public void deleteSpace(UUID id, UUID userId) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Space bulunamadı"));

        boolean hasOwnerPermission = spacePermissionRepository
                .findByUserIdAndSpaceId(userId, id)
                .map(p -> p.getPermissionType() == PermissionType.OWNER)
                .orElse(false);

        if (!space.getOwner().getId().equals(userId) && !hasOwnerPermission) {
            throw new AccessDeniedException("Sadece bir OWNER bu alanı silebilir!");
        }
        spaceRepository.deleteById(id);
    }

    @Override
    public List<SpaceDto> getNearbySpaces(double lat, double lon, double radius, UUID userId) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(lon, lat));

        return spaceRepository.findNearbySpacesAccessibleToUser(userId, userLocation, radius)
                .stream()
                .map(spaceMapper::toDto)
                .toList();
    }
}