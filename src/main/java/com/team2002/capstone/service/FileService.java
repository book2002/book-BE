package com.team2002.capstone.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${firebase.bucket-name}")
    private String firebaseBucket;

    private Storage getStorage() {
        return StorageClient.getInstance(FirebaseApp.getInstance()).bucket(firebaseBucket).getStorage();
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = folderName + "/" + UUID.randomUUID().toString() + extension;

        Storage storage = getStorage();

        BlobId blobId = BlobId.of(firebaseBucket, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());

        String baseUrl = "https://firebasestorage.googleapis.com/v0/b/" + firebaseBucket + "/o/";
        return baseUrl + fileName.replace("/", "%2F") + "?alt=media";
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String path = fileUrl.substring(fileUrl.indexOf("/o/") + 3);
            path = path.substring(0, path.indexOf("?"));

            String filePath = java.net.URLDecoder.decode(path, "UTF-8");

            Storage storage = getStorage();

            boolean deleted = storage.delete(firebaseBucket, filePath);

            if (deleted) {
                log.info("Firebase Storage 파일 삭제 성공: {}", filePath);
            } else {
                log.warn("Firebase Storage 파일 삭제 실패: 파일을 찾을 수 없습니다. 경로: {}", filePath);
            }

        } catch (StorageException | IOException e) {
            log.error("Firebase Storage 파일 삭제 중 오류 발생: {}", e.getMessage());
        }
    }
}
