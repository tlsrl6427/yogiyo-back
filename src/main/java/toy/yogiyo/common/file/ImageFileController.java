package toy.yogiyo.common.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;

@RestController
@RequiredArgsConstructor
public class ImageFileController {

    private final ImageFileHandler imageFileHandler;

    @GetMapping("/${image.path}/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return imageFileHandler.load(filename);
    }
}
