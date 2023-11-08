package toy.yogiyo.util;

import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class ConstrainedFields {
    private final Class<?> clazz;
    private final ConstraintDescriptions constraintDescriptions;

    public ConstrainedFields(Class<?> clazz) {
        this.clazz = clazz;
        this.constraintDescriptions = new ConstraintDescriptions(clazz);
    }
    
    public FieldDescriptor withPath(String path) throws NoSuchFieldException {
        Attributes.Attribute attribute = key("constraints").value(constraintDescriptions.descriptionsForProperty(path));

        if (path.contains(".")) {
            attribute = getLastPathAttribute(path);
        }

        return fieldWithPath(path).attributes(attribute);
    }

    private Attributes.Attribute getLastPathAttribute(String path) throws NoSuchFieldException {
        String[] token = path
                .replaceAll("[^a-zA-Z0-9-_.]", "")
                .split("\\.");

        Field field = clazz.getDeclaredField(token[0]);

        Class<?> fieldType;
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            fieldType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
        } else {
            fieldType = (Class<?>) genericType;
        }

        ConstraintDescriptions descriptions = new ConstraintDescriptions(fieldType);
        return key("constraints").value(descriptions.descriptionsForProperty(token[1]));
    }
}
