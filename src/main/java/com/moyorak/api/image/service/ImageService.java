package com.moyorak.api.image.service;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.image.dto.ImageDeleteRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListResponse;
import com.moyorak.api.image.dto.ImagePresignedUrlRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlResponse;
import com.moyorak.api.image.dto.ImageSaveRequest;
import com.moyorak.api.image.dto.ImageSaveResponse;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // TODO 리뷰 도메인쪽으로 이동해야함
    public List<ReviewPhotoPath> getUrlsFomReview(final List<ReviewPhotoPath> reviewPhotoPaths) {
        return imageStore.getUrlsFomReview(reviewPhotoPaths);
    }

    public Page<PhotoPath> getUrlsFomPhotoPaths(final Page<PhotoPath> photoPaths) {
        return imageStore.getUrlsFomPhotoPaths(photoPaths);
    }
}
