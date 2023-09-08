package toy.yogiyo.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import toy.yogiyo.common.exception.ErrorCode;
import toy.yogiyo.common.exception.FileIOException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Component
public class ImageFileHandler {

    @Value("${file.dir}")
    private String fileDir;

    public String store(MultipartFile multipartFile) throws IOException {
        validationFile(multipartFile);

        String originalFilename = multipartFile.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        multipartFile.transferTo(new File(getFullPath(storeFileName)));

        // 692c0741-f234-448e-ba3f-35b5a394f33d.jpg
        return storeFileName;
    }

    public Resource load(String filename) throws MalformedURLException {
        return new UrlResource("file:" + getFullPath(filename));
    }

    public boolean remove(String filename) {
        File file = new File(getFullPath(filename));
        return file.delete();
    }

    private void validationFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new FileIOException(ErrorCode.FILE_EMPTY);
        }

        if (!multipartFile.getContentType().startsWith("image")) {
            throw new IllegalArgumentException();
        }
    }

    private String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    private String createStoreFileName(String originalFileName) {
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }
}
