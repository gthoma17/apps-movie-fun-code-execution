package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.Blob;
import org.superbiz.moviefun.BlobStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }


    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable Long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable Long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveAlbumCover(albumId, uploadedFile);

        return format("redirect:/albums/%d", albumId);
    }


    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable Long albumId) throws IOException, URISyntaxException {
        Path coverFilePath = getExistingCoverPath(albumId);
        byte[] imageBytes = readAllBytes(coverFilePath);
        HttpHeaders headers = createImageHttpHeaders(coverFilePath, imageBytes);

        return new HttpEntity<>(imageBytes, headers);
    }

    private void saveAlbumCover(@PathVariable Long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        blobStore.put(new Blob(albumId.toString(), uploadedFile.getInputStream(), uploadedFile.getContentType()));
    }

    private HttpHeaders createImageHttpHeaders(Path coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private Path getExistingCoverPath(@PathVariable Long albumId) throws URISyntaxException, IOException {
        Optional<Blob> optionalBlob = blobStore.get(albumId.toString());
        if (optionalBlob != null) {
            Blob coverBlob = optionalBlob.get();
            byte[] buffer = new byte[coverBlob.inputStream.available()];
            File coverFile = File.createTempFile("movie_","_fun");
            FileOutputStream fileOutputStream = new FileOutputStream(coverFile);

            coverBlob.inputStream.read(buffer);
            fileOutputStream.write(buffer);

            return coverFile.toPath();
        }
        return Paths.get(getSystemResource("default-cover.jpg").toURI());
    }
}
