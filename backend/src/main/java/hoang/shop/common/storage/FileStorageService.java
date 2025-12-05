package hoang.shop.common.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootPath;

    public FileStorageService(
            @Value("${file.upload-dir}") String uploadDir
    ) {
        this.rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(rootPath);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create upload folder", e);
        }
    }

    public String save(MultipartFile file, String fileName) {
        try {
            Path target = rootPath.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("Save file error", e);
        }
    }
    public String saveReviewImage(Long userId, Long productId, MultipartFile file) {
        try {
            String folder = "reviews/user-" + userId + "/product-" + productId;
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path targetFolder = rootPath.resolve(folder);
            Files.createDirectories(targetFolder);

            Path target = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + folder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Cannot save review image", e);
        }
    }
    public String saveColorImage(Long productId, Long colorId, MultipartFile file) {
        try {
            String folder = "product-colors/product-" + productId + "/color-" + colorId;
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            Path targetFolder = rootPath.resolve(folder);
            Files.createDirectories(targetFolder);

            Path target = targetFolder.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + folder + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Cannot save color image", e);
        }
    }

}
