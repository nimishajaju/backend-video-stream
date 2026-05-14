package com.application.video.stream.service.impl;

import ch.qos.logback.core.util.StringUtil;
import com.application.video.stream.entity.Video;
import com.application.video.stream.repository.VideoRepo;
import com.application.video.stream.service.VideoService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Value("${files.video}")
    String dir;

    @Value("${files.video.hls}")
    String DIR_HLS;

    @Autowired
    private VideoRepo videoRepo;

    @PostConstruct
    public void init(){
        File file = new File(dir);
        File file1= new File(DIR_HLS);

        if(!file1.exists()){
            file1.mkdir();
        }

        if(!file.exists()){
            file.mkdir();
            System.out.println("folder created");
        }else{
            System.out.println("folder already created");
        }
    }
    @Override
    public Video saveVideo(MultipartFile file, Video video) {
        try {
        //file name
    String filename = file.getOriginalFilename();
    String contentType = file.getContentType();
    InputStream inputStream = file.getInputStream();

    String cleanFileName= StringUtils.cleanPath(filename);

    // folder name
    String cleanFolderName = StringUtils.cleanPath(dir);

    // join folder and file name
    Path fullPath = Paths.get(cleanFolderName, cleanFileName);

            System.out.println(fullPath);

    //UPLOAD VIDEO
    Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);

    //metadata

            video.setFilePath(fullPath.toString());
            video.setContentType(contentType);

           Video uploadVideo= videoRepo.save(video);

//process video
            processVideo(uploadVideo.getVideoId());

// save mata data
         return  uploadVideo ;

        } catch (IOException e) {
    e.printStackTrace();
            return null;
}
    }

    @Override
    public Video getById(String videoId) {
     Video video=   videoRepo.findById(videoId).orElse(null);
        if(video==null){
            throw new RuntimeException("video not found");
        }
        return video ;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAll() {

        return videoRepo.findAll();
    }

    @Override
    public String processVideo(String videoId) {

        Video video = this.getById(videoId);

        String filePath = video.getFilePath();

        Path videoPath = Paths.get(filePath);


        //ffmpeg command
        try {
            Path outputPath = Paths.get(DIR_HLS, videoId);
            Files.createDirectories(outputPath);

            //  Build FFmpeg command

            // Run command using ProcessBuilder
            String ffmpegPath = "C:\\Users\\Nimisha\\Desktop\\ffmpeg\\ffmpeg-2026-04-26-git-4867d251ad-full_build\\bin\\ffmpeg.exe";

            ProcessBuilder processBuilder = new ProcessBuilder(
                    ffmpegPath,
                    "-i", videoPath.toString(),
                    "-c:v", "libx264",
                    "-c:a", "aac",
                    "-strict", "-2",
                    "-f", "hls",
                    "-hls_time", "10",
                    "-hls_list_size", "0",
                    "-hls_segment_filename", outputPath + "\\segment_%03d.ts",
                    outputPath + "\\master.m3u8"
            );


            // Show FFmpeg logs in console
            processBuilder.inheritIO();

            // Start process
            Process process = processBuilder.start();

            // Wait until FFmpeg completes
            int exitCode = process.waitFor();

            //  Check if success
            if (exitCode != 0) {
                throw new RuntimeException("Video processing failed!");
            }

            return videoId;


        }catch (IOException e){
            throw new RuntimeException("IO Error during video processing", e);
        }catch (InterruptedException e) {
            throw new RuntimeException("Process interrupted", e);
        }

    }
}
