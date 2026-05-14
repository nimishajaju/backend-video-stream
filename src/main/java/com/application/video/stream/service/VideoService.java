package com.application.video.stream.service;

import com.application.video.stream.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    //SAVE
    Video saveVideo(MultipartFile file, Video video);

    //get by id
    Video getById(String Id);

    // get by title
    Video getByTitle(String title);

    //getAll
    List<Video> getAll();

    String processVideo(String videoId);
}
