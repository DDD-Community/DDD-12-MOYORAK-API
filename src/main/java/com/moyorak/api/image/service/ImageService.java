package com.moyorak.api.image.service;

import com.moyorak.api.image.dto.ImageDeleteRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlListResponse;
import com.moyorak.api.image.dto.ImagePresignedUrlRequest;
import com.moyorak.api.image.dto.ImagePresignedUrlResponse;
import com.moyorak.api.image.dto.ImageSaveRequest;
import com.moyorak.api.image.dto.ImageSaveResponse;
import com.moyorak.api.review.dto.PhotoPath;
import com.moyorak.api.review.dto.ReviewPhotoPath;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.infra.aws.s3.S3Adapter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Adapter s3Adapter;

    public ImageSaveResponse generateUrl(final ImageSaveRequest request) {
        final String uuid = UUID.randomUUID().toString();
        final String path =
                s3Adapter.createFilePath(LocalDateTime.now(), uuid, request.extensionName());
        final String url = s3Adapter.createPreSignUrl(path, request.extensionName());

        return ImageSaveResponse.from(url, path);
    }

    public void remove(final Long userId, final ImageDeleteRequest request) {
        if (!userId.equals(request.userId())) {
            throw new BusinessException("사용자 ID가 일치하지 않습니다.");
        }

        s3Adapter.delete(request.path());
    }

    public ImagePresignedUrlResponse getUrl(final ImagePresignedUrlRequest request) {
        final String url = s3Adapter.getPresignedUrl(request.path());

        return ImagePresignedUrlResponse.from(url);
    }

    public ImagePresignedUrlListResponse getUrls(final ImagePresignedUrlListRequest requests) {
        return ImagePresignedUrlListResponse.from(
                requests.paths().stream().map(this::getUrl).collect(Collectors.toList()));
    }

    public List<ReviewPhotoPath> getUrlsFomReview(final List<ReviewPhotoPath> reviewPhotoPaths) {
        return reviewPhotoPaths.stream()
                .map(
                        r ->
                                new ReviewPhotoPath(
                                        r.reviewId(),
                                        s3Adapter.getPresignedUrl(r.reviewPhotoPath())))
                .toList();
    }

    public Page<PhotoPath> getUrlsFomPhotoPaths(final Page<PhotoPath> photoPaths) {
        return photoPaths.map(p -> new PhotoPath(s3Adapter.getPresignedUrl(p.path())));
    }
}
