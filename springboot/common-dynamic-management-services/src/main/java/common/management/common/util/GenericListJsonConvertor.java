package common.management.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Converter(autoApply = true)
@Slf4j
public class GenericListJsonConvertor<T> implements AttributeConverter<List<T>, String> {

    @Override
    public String convertToDatabaseColumn(List<T> o) {
        try {
            ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
            return objectMapper.writeValueAsString(o);
        } catch (Exception ex) {
            log.error("[EXCEPTION] cannot convert object to json string {},{}",ex.getMessage(),ex);
            return null;
        }
    }

    @Override
    public List<T> convertToEntityAttribute(String s) {
        try {
            if(s == null) return null;
            ObjectMapper objectMapper = SpringContext.getBean(ObjectMapper.class);
            return objectMapper.readValue(s, new TypeReference<List<T>>(){});
        } catch (Exception ex) {
            log.error("[EXCEPTION] cannot convert json string to object {} {},{}",s,ex.getMessage(),ex);
            return null;
        }
    }
}
