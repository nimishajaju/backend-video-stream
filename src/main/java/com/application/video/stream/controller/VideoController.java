package com.application.video.stream.controller;

import com.application.video.stream.AppConstant;
import com.application.video.stream.entity.Video;
import com.application.video.stream.payLoad.CustomMessage;
import com.application.video.stream.repository.VideoRepo;
import com.application.video.stream.service.VideoService;
import com.application.video.stream.service.impl.VideoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin("*")
public class VideoController {

    @Autowired
    private VideoServiceImpl videoServiceImpl;

    @Autowired
    private VideoService videoService;

    @PostMapping("/")
  public ResponseEntity<?> saveVideo(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @RequestParam String description
            ){
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoId(UUID.randomUUID().toString());

       Video savedVideo= videoServiceImpl.saveVideo(file, video);
       if( savedVideo!=null){
           return ResponseEntity
                   .status(HttpStatus.OK)
                   .body(video);
       }
       else{
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(CustomMessage.builder().message("video not uploded").success(false).build());
       }
        
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> stream(
            @PathVariable String videoId
    ){
       Video video= videoServiceImpl.getById(videoId);
      String contentType= video.getContentType();
      String filePath= video.getFilePath();

      Resource resource= new FileSystemResource(filePath);


      if(contentType==null){
          contentType="application/octet-stream";
      }

      return ResponseEntity
              .ok()
              .contentType(MediaType.parseMediaType(contentType))
              .body(resource);

    }

    @GetMapping ("/getAllVideo")
    public List<Video> getAllVideos(){
        return videoServiceImpl.getAll();
    }



@GetMapping("/stream/range/{videoId}")
public ResponseEntity<Resource> streamVideoRange(
        @PathVariable String videoId,
        @RequestHeader (value = "Range",required = false) String range
){
    System.out.println(range);

    Video video = videoService.getById(videoId);
    String contantType = video.getContentType();
    Path path = Paths.get(video.getFilePath());

    Resource resource = new FileSystemResource(path);

    if(contantType==null){
        contantType= "application/octet-stream";
    }

    //file length
  long  fileLength= path.toFile().length();

    //pehele jaisa code

    if( range == null){
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contantType))
        .body(resource);
    }

    long startRange;
    long endRange;

    String[] ranges = range.replace("bytes=", "").split("-");
    startRange = Long.parseLong(ranges[0]);

    endRange = startRange+ AppConstant.CHUNK_SIZE-1;

    if( endRange>= fileLength){
        endRange= fileLength-1;
    }

//    if( ranges.length>1){
//        endRange = Long.parseLong(ranges[1]);
//    }
//    else{
//        endRange = fileLength-1;
//    }
//    if( endRange> fileLength-1 ){
//        endRange = fileLength-1;
//    }

    System.out.println("start" + startRange);
    System.out.println("end"+ endRange);


    InputStream inputStream;

    try {
        inputStream = Files.newInputStream(path);

        inputStream.skip(startRange);
        long contentLength= endRange-startRange+1;

        byte[] data = new byte[(int) contentLength];
        int read = inputStream.read(data,0,data.length);
        System.out.println("read(num of byts)" + read);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Range","bytes "+startRange +"-"+ endRange+"/"+fileLength);
        httpHeaders.add("Cache-Control","no-cache, no-store, must-revalidate");
        httpHeaders.add("Pragma", "no-cache");
        httpHeaders.add("Expires", "0");
        httpHeaders.add("X-Content-Type-Options", "nosniff");
        httpHeaders.setContentLength(contentLength);

        return  ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(httpHeaders)
                .contentType(MediaType.parseMediaType(contantType))
                .body(new ByteArrayResource(data));
    }
    catch (IOException ex){
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    }

    // serve hls playlist
    // master.m3u8

    @Value("${files.video.hls}")
   private String DIR_HLS;

    @GetMapping("/{videoId}/master.m3u8")
    public ResponseEntity<Resource> serveMasterFile(
            @PathVariable String videoId
    ){
        Path path = Paths.get(DIR_HLS, videoId, "master.m3u8");

        System.out.println(path);

        if(!Files.exists(path)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE,"application/vnd.apple.mpequrl")
                .body(resource);

    }

    @GetMapping( "/{videoId}/{segment}.ts")
    public ResponseEntity<Resource> serveSegmrnts(
            @PathVariable String videoId,
            @PathVariable String segment
    ){
        Path path = Paths.get(DIR_HLS, videoId, segment+".ts");
        if( !Files.exists(path)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
        Resource resource = new FileSystemResource(path);
        return  ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,"video/mp2t")
                .body(resource);
    }
}

