package toy.yogiyo.util;

import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.snippet.Attributes.key;

public class ConstrainedFields {
    private final Class<?> clazz;
    private final ConstraintDescriptions constraintDescriptions;
    private final ResourceBundleConstraintDescriptionResolver bundleDescriptionResolver;
    private final ConstraintDescriptionResolver constraintDescriptionResolver;

    public ConstrainedFields(Class<?> clazz) {
        this.clazz = clazz;
        this.bundleDescriptionResolver = new ResourceBundleConstraintDescriptionResolver();
        this.constraintDescriptionResolver = (constraint) -> {
            String message = (String) constraint.getConfiguration().get("message");
            if (message != null && !Pattern.compile("\\{(.*?)\\}").matcher(message).matches()) {
                return message;
            }
            return bundleDescriptionResolver.resolveDescription(constraint);
        };
        this.constraintDescriptions = new ConstraintDescriptions(clazz, constraintDescriptionResolver);

    }
    
    public FieldDescriptor withPath(String path) throws NoSuchFieldException {
        Attributes.Attribute attribute = key("constraints").value(constraintDescriptions.descriptionsForProperty(path));

        if (path.contains(".")) {
            attribute = getLastPathAttribute(path);
        }

        return fieldWithPath(path).attributes(attribute);
    }

    private Attributes.Attribute getLastPathAttribute(String path) throws NoSuchFieldException {
        String[] tokens = path
                .replaceAll("[^a-zA-Z0-9-_.]", "")
                .split("\\.");


        ConstraintDescriptions descriptions = new ConstraintDescriptions(getLastClass(clazz, tokens), constraintDescriptionResolver);
        return key("constraints").value(descriptions.descriptionsForProperty(tokens[tokens.length-1]));
    }

    private Class<?> getLastClass(Class<?> clazz, String[] tokens) throws NoSuchFieldException {
        for (int i = 0; i < tokens.length - 1; i++) {
            Field field = clazz.getDeclaredField(tokens[i]);
            Class<?> fieldType;

            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                fieldType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
            } else {
                fieldType = (Class<?>) genericType;
            }

            if (fieldType.isPrimitive() || fieldType.getName().startsWith("java.lang")) {
                break;
            }
            clazz = fieldType;
        }

        return clazz;
    }
}
