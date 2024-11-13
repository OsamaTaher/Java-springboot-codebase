package common.management.notification;

import common.management.common.events.NotificationEvent;
import common.management.notification.processor.NotificcationEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EventHandler {

    private final Map<String, NotificcationEventProcessor> eventProcessors = new HashMap<>();

    @Autowired
    public EventHandler(List<NotificcationEventProcessor> processorList){
        processorList.forEach(
                strategy -> this.eventProcessors.put(strategy.getClass().getAnnotation(Component.class).value(), strategy)
        );
    }

    @EventListener
    @Async
    public void handleNotificationEvent(NotificationEvent event) throws InterruptedException {
        var eventName = event.event().name();
        var module = eventName.substring(0, eventName.indexOf("_"));
        if(eventProcessors.containsKey(module)) eventProcessors.get(module).process(event);
        else log.error("[ERROR] handleNotificationEvent=> module processor not found");
    }
}
