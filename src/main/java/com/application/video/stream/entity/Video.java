package com.application.video.stream.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "Videos")
public class Video {

    @Id
     private String videoId;

    private String title;

    private String description;

    private String contentType;

    private String filePath;
}
