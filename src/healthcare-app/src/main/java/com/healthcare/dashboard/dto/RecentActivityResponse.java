package com.healthcare.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record RecentActivityResponse(
    List<ActivityItem> activities,
    int totalCount
) {
    public record ActivityItem(
        String id,
        String type,
        String title,
        String description,
        Instant timestamp,
        String actorName,
        String actorRole,
        String resourceId,
        String resourceType
    ) {}
}
