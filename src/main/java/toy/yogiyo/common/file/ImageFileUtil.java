package toy.yogiyo.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageFileUtil {

    private static String path;

    @Value("${image.path}")
    public void setPath(String value) {
        if (path == null)
            path = "/" + value + "/";
    }

    public static String getFilePath(String filename) {
        return path + filename;
    }

    public static String extractFilename(String filePath) {
        return filePath.replaceAll(path, "");
    }
}
