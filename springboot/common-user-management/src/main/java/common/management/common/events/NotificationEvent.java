package common.management.common.events;

import java.util.Optional;

public record NotificationEvent
(
        EventType event,
        Optional<?> entityId,
        Optional<String> doneBy
)
{}
