package com.application.video.stream.payLoad;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomMessage {
    private String message;

    private boolean success= false;

}
