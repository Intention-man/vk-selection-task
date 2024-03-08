package com.intentionman.vkselectiontask.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumDto {
    private Long id;
    private String title;
    private Long userId;
}