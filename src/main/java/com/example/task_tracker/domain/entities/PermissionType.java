package com.example.task_tracker.domain.entities;

public enum PermissionType {
    VIEWER,  // Sadece okuma yapabilir
    EDITOR,  // Yetki verilen alanda ekleme,silme ve güncelleme yapabilir
    OWNER   //Yetki verilen alanın sahibi olur
}
