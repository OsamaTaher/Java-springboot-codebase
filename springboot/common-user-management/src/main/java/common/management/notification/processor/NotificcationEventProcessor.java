package common.management.notification.processor;

import common.management.common.events.NotificationEvent;

public interface NotificcationEventProcessor {
    void process(NotificationEvent event);
}
