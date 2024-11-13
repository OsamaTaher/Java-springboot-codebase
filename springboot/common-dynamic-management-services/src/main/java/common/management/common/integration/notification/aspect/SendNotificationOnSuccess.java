package common.management.common.integration.notification.aspect;

import common.management.common.events.EventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to send notification event when it execute successfully
 * the method must has a return value and transactional
 * input: event title
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendNotificationOnSuccess {
    EventType event();
    SearchIn searchIn() default SearchIn.nothing;
    String entityIdFiledPath() default "";
}
