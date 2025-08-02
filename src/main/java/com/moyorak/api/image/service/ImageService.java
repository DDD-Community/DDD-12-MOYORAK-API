package com.moyorak.api.image.service;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.image.dto.ImageDeleteRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListResponse;
import com.moyorak.api.image.dto.ImagePresignedUrlRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlResponse;
import com.moyorak.api.image.dto.ImageSaveRequest;
import com.moyorak.api.image.dto.ImageSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageStore imageStore;

    public ImageSaveResponse generateUrl(final ImageSaveRequest request) {
        return imageStore.generateUrl(request);
    }

    public void remove(final Long userId, final ImageDeleteRequest request) {
        imageStore.remove(userId, request);
    }

    public ImagePresignedUrlResponse getUrl(final ImagePresignedUrlRequest request) {
        return imageStore.getUrl(request);
    }

    public ImagePresignedUrlListResponse getUrls(final ImagePresignedUrlListRequest requests) {
        return imageStore.getUrls(requests);
    }
}
