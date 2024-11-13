package common.management.notification.model;


import common.management.common.events.EventType;
import common.management.common.events.NotificationEvent;
import common.management.common.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Entity
@Table(name = "notifications")
@NoArgsConstructor
@Getter
@SQLRestriction("deleted=false")
public class NotificationEntity extends BaseEntity {
    @NotNull
    String message;
    @NotNull
    @Enumerated(EnumType.STRING)
    EventType event;
    String entityId;
    @Enumerated(EnumType.STRING)
    Module module;
    String doneBy;

    @ElementCollection
    List<String> notifyTo;

    public NotificationEntity create(NotificationEvent event,String message, Module module, List<String> notifyTo){
        this.event = event.event();
        this.message = message;
        this.entityId = ""+event.entityId().orElse(null);
        this.module = module;
        this.notifyTo = notifyTo;
        this.doneBy = event.doneBy().orElse(null);
        return this;
    }
}
