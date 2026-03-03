package com.blackbox.vero.common;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class ChildIndexEntityListener {

    private final Logger logger = LoggerFactory.getLogger(ChildIndexEntityListener.class);

    @PrePersist
    @PreUpdate
    public void updateIndexes(Object entity) {
        logger.info("Updating indexes for entity: {}", entity.getClass().getName());
        Field[] fields = entity.getClass().getDeclaredFields();

        for (Field field : fields) {

            if (!List.class.isAssignableFrom(field.getType())) {
                logger.info("Field is not a list: {}", field.getName());
                continue;
            }

            field.setAccessible(true);

            try {

                List<?> list = (List<?>) field.get(entity);

                if (list == null)
                    continue;

                for (int i = 0; i < list.size(); i++) {

                    Object child = list.get(i);

                    if (child instanceof ChildEntity childEntity) {
                        childEntity.setIdx(i + 1);
                    }

                }

                logger.info("Indexes updated for entity: {}", entity.getClass().getName());
                

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
