package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.storage.dto.BaseReqDto;
import com.shinchik.cloudkeeper.storage.dto.RenameDto;
import com.shinchik.cloudkeeper.storage.dto.UploadDto;
import com.shinchik.cloudkeeper.storage.exception.MinioRepositoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Import(TestStorageConfig.class)
class StorageServiceTest {

    private static final String MINIO_IMAGE_NAME = "quay.io/minio/minio:latest";
    private static final DockerImageName MINIO_IMAGE = DockerImageName.parse("quay.io/minio/minio:latest");

    // @Container
    private GenericContainer<?> minioContainer = new FixedHostPortGenericContainer<>(MINIO_IMAGE_NAME)
            .withFixedExposedPort(9000, 9000)
            .withFixedExposedPort(9001, 9001)
            .withCommand("server", "/data", "--console-address", ":9001");


    @Autowired
    private MinioClientProperties minioClientProperties;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private StorageService storageService;

    private final User user = new User(1L, "user1", "pass1", "USER");
    private final String genericPath = "folder";


    @BeforeEach
    public void before() throws InterruptedException {
        minioContainer.withEnv("MINIO_ROOT_USER", minioClientProperties.getUser())
                .withEnv("MINIO_ROOT_PASSWORD", minioClientProperties.getPassword());
        minioContainer.start();
        Thread.sleep(2000); // needs for container to really get ready
        bucketService.createDefaultBucket();
    }

    @AfterEach
    public void after() {
        minioContainer.stop();
    }

    // TODO: extract common test data generation into @BeforeEach

    @Test
    @DisplayName("Upload file")
    public void uploadSingleFile_objCreated() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, genericPath, files.get(0).getOriginalFilename());
        assertTrue(storageService.isObjectExist(checkDto), "File have been uploaded successfully");
    }

    @Test
    @DisplayName("Upload multiple files")
    public void uploadMultipleFiles_objectsCreated() {
        List<MultipartFile> files = generateMockMultipartFiles(3);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, genericPath, "");
        assertEquals(3, storageService.list(checkDto).size(), "All files have been uploaded successfully");
    }

    @Test
    @DisplayName("Download existent file")
    public void downloadExistentFile() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto downloadDto = new BaseReqDto(user, genericPath, files.get(0).getOriginalFilename());
        InputStreamResource downloadedFile = storageService.download(downloadDto);
        assertDoesNotThrow(() -> downloadedFile.getInputStream().available(), "Input stream of downloaded file is valid");
    }

    @Test
    @DisplayName("Download existent folder with files and folder inside -> must create zip archive")
    public void downloadExistentFolder_zipArchiveSaved(@TempDir(cleanup = CleanupMode.ON_SUCCESS) Path tempDir) throws IOException {
        List<MultipartFile> files = generateMockMultipartFiles(3);
        files.add(createFakeFolder());
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto downloadDto = new BaseReqDto(user, "", "folder");
        InputStreamResource downloadedZip = storageService.download(downloadDto);

        File zipFile = new File(tempDir.toFile(), "test.zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile)) {
            fos.write(downloadedZip.getContentAsByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(zipFile.exists(), "Zip archive have been created successfully");
    }

    @Test
    @DisplayName("Download nonexistent object")
    public void downloadNonExistentObj_thenThrow() {
        BaseReqDto downloadDto = new BaseReqDto(user, genericPath, "dummyName");
        assertThrows(MinioRepositoryException.class, () -> storageService.download(downloadDto), "No such object exists");
    }

    @Test
    @DisplayName("Rename file")
    public void renameFile() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        String oldName = files.get(0).getOriginalFilename();
        String newName = "RenamedFile";
        RenameDto renameDto = new RenameDto(user, genericPath, oldName, newName);
        storageService.rename(renameDto);

        BaseReqDto oldFileDto = new BaseReqDto(user, genericPath, oldName);
        BaseReqDto newFileDto = new BaseReqDto(user, genericPath, newName + ".txt");
        assertFalse(storageService.isObjectExist(oldFileDto), "Object with old name does ot exist");
        assertTrue(storageService.isObjectExist(newFileDto), "Object with new name exists");
    }

    @Test
    @DisplayName("Rename folder with file inside")
    public void renameFolder() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath + "/folder2", files);
        storageService.upload(uploadDto);

        String fileName = files.get(0).getOriginalFilename();
        String oldFolderName = "folder2";
        String newFolderName = "renamedFolder";
        RenameDto renameDto = new RenameDto(user, genericPath, oldFolderName, newFolderName);
        storageService.rename(renameDto);

        // check whether old name folder exists and whether file is now in renamed folder
        BaseReqDto oldFolderDto = new BaseReqDto(user, genericPath, oldFolderName);
        BaseReqDto newFileDto = new BaseReqDto(user, genericPath, newFolderName + "/" + fileName);
        assertFalse(storageService.isObjectExist(oldFolderDto), "Folder with old name does not exist");
        assertTrue(storageService.isObjectExist(newFileDto), "File inside renamed folder exists");
    }


    @Test
    @DisplayName("Delete file")
    public void deleteFile() {
        List<MultipartFile> files = generateMockMultipartFiles(1);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto deleteDto = new BaseReqDto(user, genericPath, files.get(0).getOriginalFilename());
        storageService.delete(deleteDto);

        BaseReqDto checkDto = new BaseReqDto(user, genericPath, files.get(0).getOriginalFilename());
        assertFalse(storageService.isObjectExist(checkDto), "File have been deleted successfully");
    }


    @Test
    @DisplayName("Delete folder with files and folder inside")
    public void deleteFolder() throws IOException {
        List<MultipartFile> files = generateMockMultipartFiles(3);
        files.add(createFakeFolder());
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto deleteDto = new BaseReqDto(user, "", genericPath);
        storageService.delete(deleteDto);
        BaseReqDto checkDto = new BaseReqDto(user, genericPath);
        assertEquals(0, storageService.list(checkDto).size(), "All objects have been deleted");
    }


    @Test
    @DisplayName("List objects inside directory")
    public void listDir() throws IOException {
        List<MultipartFile> files = generateMockMultipartFiles(3);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        UploadDto fakeFolderUploadDto = new UploadDto(user, "", List.of(createFakeFolder()));
        storageService.upload(fakeFolderUploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, "");
        assertEquals(2, storageService.list(checkDto).size(),
                "All objects inside directory have been found, no extra objects been found");
    }

    @Test
    @DisplayName("List all objects after prefix recursively")
    public void listRecursively() throws IOException {
        List<MultipartFile> files = generateMockMultipartFiles(3);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        UploadDto fakeFolderUploadDto = new UploadDto(user, "", List.of(createFakeFolder()));
        storageService.upload(fakeFolderUploadDto);

        BaseReqDto checkDto = new BaseReqDto(user, "");
        assertEquals(4, storageService.listRecursively(checkDto).size(), "All objects have been found");
    }

    @Test
    @DisplayName("Create folder and ensure that files can be loaded inwards")
    public void createFolder() {
        BaseReqDto reqDto = new BaseReqDto(user, "", genericPath);
        storageService.createFolder(reqDto);

        BaseReqDto checkDto = new BaseReqDto(user, "", reqDto.getObjName());
        assertTrue(storageService.isDir(checkDto), "Folder created properly");

        List<MultipartFile> files = generateMockMultipartFiles(3);
        UploadDto uploadDto = new UploadDto(user, genericPath, files);
        storageService.upload(uploadDto);

        BaseReqDto listDto = new BaseReqDto(user, genericPath);
        assertEquals(3, storageService.list(listDto).size(), "Objects have been loaded into folder successfully");
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

    private static MultipartFile createFakeFolder() throws IOException {
        return new MockMultipartFile(
                "fakeFolder/",
                "fakeFolder/",
                "content",
                InputStream.nullInputStream());
    }


}