package common.management.common.integration.notification.aspect;

import common.management.common.events.NotificationEvent;
import common.management.common.service.UserService;
import common.management.common.util.OpWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static common.management.common.util.OperationStatus.OP_STATUS_SUCCESS;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AopNotificationsIntegration {
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Pointcut("@annotation(SendNotificationOnSuccess)")
    public void sendNotificationOnSuccess() {
    }

    @Pointcut("sendNotificationOnSuccess() ")
    public void pointCut(){
    }

    @AfterReturning(value = "pointCut()",returning = "output")
    public void callPointSystemAfter(JoinPoint joinPoint, Object output) throws IllegalAccessException {
        //CHECK ACTION RESULT
        if(!isMethodCompletedSuccessfully(output)) return;

        //Extract annotation parameters
        var signature = (MethodSignature) joinPoint.getSignature();
        var annotation  = signature.getMethod().getAnnotation(SendNotificationOnSuccess.class);

        Optional<?> entityId = findEntityId(joinPoint,output,annotation);
        Optional<String> actionDoneBy = userService.getAuthUserId();
        var event = annotation.event();
        eventPublisher.publishEvent(new NotificationEvent(event,entityId,actionDoneBy));
    }

    private boolean isMethodCompletedSuccessfully(Object output){
        if(output instanceof Integer returnValue){
            return returnValue.equals(OP_STATUS_SUCCESS);
        }else if (output instanceof OpWrapper<?> returnValue){
            return returnValue.status() == OP_STATUS_SUCCESS;
        }else {
            return false;
        }
    }

    private Optional<?> findEntityId(JoinPoint joinPoint,Object output ,SendNotificationOnSuccess annotation){
        SearchIn searchIn = annotation.searchIn();
        String entityIdFieldPath = annotation.entityIdFiledPath();
        return switch (searchIn) {
            case params -> findEntityIdInMethodArguments(joinPoint, entityIdFieldPath);
            case returnVal -> findEntityIdInMethodReturnValue(output, entityIdFieldPath);
            default -> Optional.empty();
        };
    }

    private Optional<?> findEntityIdInMethodArguments(JoinPoint joinPoint, String entityIdFieldPath){
        Object entityId = null;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        var path = splitPath(entityIdFieldPath);
        if(path.length < 1) return Optional.empty();

        int depth = 0;
        var index = findMethodArgumentIndex(methodSignature, path[0]);
        if(index.isEmpty()) return Optional.empty(); // No argument found

        depth++;
        return followPath(joinPoint.getArgs()[index.getAsInt()], path, depth);
    }

    private Optional<?> findEntityIdInMethodReturnValue(Object returnVal, String entityIdFieldPath){
        if(entityIdFieldPath.isEmpty()) return Optional.of(returnVal);
        return followPath(returnVal,splitPath(entityIdFieldPath), 0);
    }

    private Optional<?> followPath(Object objectToSearchIn,String[] path,int depth ){
        Object ob = objectToSearchIn;
        Field field = null;
        while(depth < path.length){
            try {
                field = ob.getClass().getDeclaredField(path[depth]);
                field.setAccessible(true);
                ob = field.get(ob);
                depth++;
            } catch (Exception e) {
                log.error("[Exception] AopNotificationsIntegration => followPath => {}",e.getMessage());
                return Optional.empty();
            }
        }
        return Optional.of(ob);
    }

    private OptionalInt findMethodArgumentIndex(MethodSignature methodSignature, String name){
        Parameter[] parameters = methodSignature.getMethod().getParameters();
        return IntStream.range(0, parameters.length)
                .filter(i -> name.equals(parameters[i].getName())).findFirst();
    }

    private String[] splitPath(String path){
        return path.split(Pattern.quote("."));
    }
}
