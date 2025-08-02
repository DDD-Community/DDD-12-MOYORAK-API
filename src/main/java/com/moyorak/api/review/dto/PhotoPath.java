package com.moyorak.api.review.dto;

import com.moyorak.api.image.ImageStore;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

public record PhotoPath(
        @Schema(description = "사진 경로", example = "https://somepath/review.jpg") String path) {

    public static Page<PhotoPath> convertPathUrl(
            final ImageStore imageStore, final Page<PhotoPath> photoPaths) {
        return photoPaths.map(p -> new PhotoPath(imageStore.getUrlFromStringPath(p.path)));
    }
}
