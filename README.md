# Video Streaming Backend - Spring Boot + HLS

A backend-focused video streaming application built using Java, Spring Boot, MySQL, FFmpeg, and HLS (HTTP Live Streaming).

The project supports video upload, video processing, HLS conversion, and streaming using REST APIs.

---

# Features

- Upload and manage video files
- Process videos using FFmpeg
- Convert videos into HLS format
- Generate `.m3u8` playlists and `.ts` segments
- Implemented both HTTP byte-range streaming and HLS-based adaptive streaming
- Multipart file upload support
- MySQL database integration
- RESTful API architecture
- Layered backend architecture

---

# Tech Stack

## Backend
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- REST APIs

## Database
- MySQL

## Video Processing
- FFmpeg
- HLS Streaming

## Tools
- Maven
- Git & GitHub
- IntelliJ IDEA
- Postman

---

# Media Storage Structure

```bash
videos/        # Original uploaded videos
videos_hls/    # Processed HLS files
```
