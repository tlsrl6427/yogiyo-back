package toy.yogiyo.common.file;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class ImageFileUtilTest {

    @Value("${image.path}")
    String imagePath;

    @Test
    @DisplayName("이미지 경로 추가, 파일명 추출")
    void addPathExtractFilename() throws Exception {
        String filePath = ImageFileUtil.getFilePath("filename.png");
        String filename = ImageFileUtil.extractFilename(filePath);

        assertThat(filePath).isEqualTo("/" + imagePath + "/filename.png");
        assertThat(filename).isEqualTo("filename.png");
    }

}