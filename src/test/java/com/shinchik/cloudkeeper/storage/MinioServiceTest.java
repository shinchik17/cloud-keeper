package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.storage.exception.service.NoSuchObjectException;
import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.RenameDto;
import com.shinchik.cloudkeeper.storage.model.UploadDto;
import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.Role;
import com.shinchik.cloudkeeper.user.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles({"test", "minio"})
class MinioServiceTest {

    private final GenericContainer<?> minioContainer = new FixedHostPortGenericContainer<>("minio/minio:latest")
            .withFixedExposedPort(9000, 9000)
            .withFixedExposedPort(9001, 9001)
            .withCommand("server", "/data", "--console-address", ":9001");

    @Autowired
    private MinioClientProperties minioClientProperties;

    @Autowired
    private MinioRepository minioRepository;

    @Autowired
    private MinioService minioService;

    private final User user = new User(1L, "user1", "pass1", Role.USER);
    private final String genericPath = "folder";
    private static final int NUM_OBJ_TO_UPLOAD = 3;
    private final List<MultipartFile> mockFilesAndFolder = generateMockMultipartFilesAndFolder();
    private final List<MultipartFile> mockSingleFile = generateMockMultipartFiles(1);
    private final UploadDto filesAndFolderUploadDto = new UploadDto(user, genericPath, mockFilesAndFolder);
    private final UploadDto singleFileUploadDto = new UploadDto(user, genericPath, mockSingleFile);


    @BeforeEach
    public void before() throws InterruptedException {
        minioContainer.withEnv("MINIO_ROOT_USER", minioClientProperties.getUser())
                .withEnv("MINIO_ROOT_PASSWORD", minioClientProperties.getPassword());
        minioContainer.start();
        Thread.sleep(2000); // needs for container to really get ready
        minioRepository.createDefaultBucket();
    }

    @AfterEach
    public void after() {
        minioContainer.stop();
    }


    @Test
    @DisplayName("Upload file")
    public void uploadSingleFile_objCreated() {
        minioService.upload(singleFileUploadDto);
        String filename = singleFileUploadDto.getFiles().get(0).getOriginalFilename();
        BaseReqDto checkDto = new BaseReqDto(user, genericPath, filename);
        assertTrue(minioService.isObjectExist(checkDto), "File have not been uploaded");
    }

    @Test
    @DisplayName("Upload multiple files")
    public void uploadMultipleFiles_objectsCreated() {
        minioService.upload(filesAndFolderUploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, genericPath, "");
        assertEquals(NUM_OBJ_TO_UPLOAD, minioService.list(checkDto).size(), "Files have not been uploaded");
    }

    @Test
    @DisplayName("Download existent file")
    public void downloadExistentFile() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        minioService.upload(uploadDto);

        BaseReqDto downloadDto = new BaseReqDto(user, genericPath, files.get(0).getOriginalFilename());
        InputStreamResource downloadedFile = minioService.download(downloadDto);
        assertDoesNotThrow(() -> downloadedFile.getInputStream().available(), "Downloaded file is invalid");
    }

    @Test
    @DisplayName("Download existent folder with files and empty folder inside -> must create zip archive")
    public void downloadExistentFolder_zipArchiveSaved(@TempDir(cleanup = CleanupMode.ON_SUCCESS) Path tempDir) {
        minioService.upload(filesAndFolderUploadDto);

        BaseReqDto downloadDto = new BaseReqDto(user, "", "folder");
        InputStreamResource downloadedZip = minioService.download(downloadDto);

        File zipFile = new File(tempDir.toFile(), "test.zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(downloadedZip.getContentAsByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(zipFile.exists(), "Zip archive have not been created successfully");
    }

    @Test
    @DisplayName("Download nonexistent object")
    public void downloadNonExistentObj_thenThrow() {
        assertThrows(NoSuchObjectException.class, () -> minioService.download(singleFileUploadDto),
                "Downloaded nonexistent file");
    }

    @Test
    @DisplayName("Rename file")
    public void renameFile() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        minioService.upload(uploadDto);

        String oldName = files.get(0).getOriginalFilename();
        String newName = "RenamedFile";
        RenameDto renameDto = new RenameDto(user, genericPath, oldName, newName);
        minioService.rename(renameDto);

        BaseReqDto oldFileDto = new BaseReqDto(user, genericPath, oldName);
        BaseReqDto newFileDto = new BaseReqDto(user, genericPath, newName + ".txt");
        assertFalse(minioService.isObjectExist(oldFileDto), "Object with old name still exists");
        assertTrue(minioService.isObjectExist(newFileDto), "Object with new name does not exist");
    }

    @Test
    @DisplayName("Rename folder with file inside")
    public void renameFolder() {
        String oldFolderName = "folder2";
        UploadDto uploadDto = new UploadDto(user, genericPath + "/" + oldFolderName, mockSingleFile);
        minioService.upload(uploadDto);

        String fileName = mockSingleFile.get(0).getOriginalFilename();
        String newFolderName = "renamedFolder";
        RenameDto renameDto = new RenameDto(user, genericPath, oldFolderName, newFolderName);
        minioService.rename(renameDto);

        BaseReqDto oldFolderDto = new BaseReqDto(user, genericPath, oldFolderName);
        BaseReqDto newFileDto = new BaseReqDto(user, genericPath, newFolderName + "/" + fileName);
        assertFalse(minioService.isObjectExist(oldFolderDto), "Folder with old name still exists");
        assertTrue(minioService.isObjectExist(newFileDto), "File inside renamed folder does not exist");
    }


    @Test
    @DisplayName("Delete file")
    public void deleteFile() {
        minioService.upload(singleFileUploadDto);

        String filename = singleFileUploadDto.getFiles().get(0).getOriginalFilename();
        BaseReqDto deleteDto = new BaseReqDto(user, genericPath, filename);
        minioService.delete(deleteDto);

        assertFalse(minioService.isObjectExist(deleteDto), "File have not been deleted");
    }


    @Test
    @DisplayName("Delete folder with files and folder inside")
    public void deleteFolder() {
        minioService.upload(filesAndFolderUploadDto);

        BaseReqDto deleteDto = new BaseReqDto(user, "", genericPath);
        minioService.delete(deleteDto);

        BaseReqDto checkDto = new BaseReqDto(user, genericPath);
        assertEquals(0, minioService.list(checkDto).size(), "Objects have not been deleted");
    }


    @Test
    @DisplayName("List objects inside directory")
    public void listDir() {
        minioService.upload(filesAndFolderUploadDto);

        UploadDto folderUploadDto = new UploadDto(user, "", List.of(createMockMultipartFolder()));
        minioService.upload(folderUploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, "");
        assertEquals(2, minioService.list(checkDto).size(),
                "Not all objects have been found or extra objects been found inside directory");
    }

    @Test
    @DisplayName("List all objects after prefix recursively")
    public void listRecursively() {
        minioService.upload(filesAndFolderUploadDto);

        BaseReqDto createFolderDto = new BaseReqDto(user, "", genericPath);
        minioService.createFolder(createFolderDto);

        BaseReqDto checkDto = new BaseReqDto(user, "");
        assertEquals(NUM_OBJ_TO_UPLOAD + 1, minioService.search(checkDto).size(),
                "Amount of listed objects does not equal to amount of upload");
    }

    @Test
    @DisplayName("Create folder and ensure that files can be loaded inwards")
    public void createFolder() {
        BaseReqDto createFolderDto = new BaseReqDto(user, "", genericPath);
        minioService.createFolder(createFolderDto);

        assertTrue(minioService.isDir(createFolderDto),
                "Folder have not been created or been created incorrectly");

        minioService.upload(filesAndFolderUploadDto);
        BaseReqDto listDto = new BaseReqDto(user, genericPath);
        assertEquals(NUM_OBJ_TO_UPLOAD, minioService.list(listDto).size(),
                "Expected amount of loaded into folder files does not equal to amount of preceded");
    }


    private static List<MultipartFile> generateMockMultipartFilesAndFolder() {
        List<MultipartFile> files = generateMockMultipartFiles(NUM_OBJ_TO_UPLOAD - 1);
        files.add(createMockMultipartFolder());
        return files;
    }

    private static List<MultipartFile> generateMockMultipartFiles(int amount) {
        ArrayList<MultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            multipartFiles.add(
                    new MockMultipartFile(
                            "name",
                            "MockName" + i + ".txt",
                            "plain/text",
                            new byte[]{1, 2, 3})
            );
        }

        return multipartFiles;

    }

    private static MultipartFile createMockMultipartFolder() {
        return new MockMultipartFile(
                "fakeFolder/",
                "fakeFolder/",
                "content",
                new byte[]{});
    }


}