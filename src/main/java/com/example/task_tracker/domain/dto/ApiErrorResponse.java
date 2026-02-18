package com.example.task_tracker.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

// Hata cevabı yapısı
public record ApiErrorResponse(
        int status,             // HTTP Kodu (404, 400 vs.)
        String error,           // Hatanın kısa adı (Not Found)
        String message,         // Detaylı mesaj (ID'si 5 olan task bulunamadı)
        String path,            // Hatanın olduğu adres (/api/tasks/5)
        LocalDateTime timestamp,// Ne zaman oldu?
        List<String> details    // Varsa ekstra detaylar (Validation hataları için)
) {

    public static ApiErrorResponse of(int status, String error, String message, String path, List<String> details) {
        return new ApiErrorResponse(status, error, message, path, LocalDateTime.now(), details);
    }
}
