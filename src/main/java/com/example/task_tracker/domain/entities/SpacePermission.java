package com.example.task_tracker.domain.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
@Entity
@Table(name = "space_permissions")
public class SpacePermission implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Yetki verilen kişi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", nullable = false)
    private Space space; // Hangi alana yetki verildiği

    @Enumerated(EnumType.STRING)
    private PermissionType permissionType; // VIEWER, EDITOR, OWNER
}