package com.moyorak.api.image.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.moyorak.api.image.ImageStore;
import com.moyorak.api.image.dto.ImageDeleteRequest;
import com.moyorak.api.image.dto.ImageDeleteRequestFixture;
import com.moyorak.config.exception.BusinessException;
import com.moyorak.infra.aws.s3.S3Adapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImageStoreTest {

    @InjectMocks private ImageStore imageStore;

    @Mock private S3Adapter s3Adapter;

    @Nested
    @DisplayName("이미지를 삭제할 때,")
    class remove {

        @Test
        @DisplayName("로그인 회원 ID와, 삭제하고자 하는 이미지의 회원 ID가 일치하지 않으면 오류가 발생합니다.")
        void isNotEqualsUserId() {
            // given
            final Long userId = 1L;
            final ImageDeleteRequest request = ImageDeleteRequestFixture.fixture(3L);

            // when & then
            assertThatThrownBy(() -> imageStore.remove(userId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("사용자 ID가 일치하지 않습니다.");
        }

        @Test
        @DisplayName("성공적으로 이미지를 삭제합니다.")
        void success() {
            // given
            final Long userId = 1L;
            final ImageDeleteRequest request = ImageDeleteRequestFixture.fixture(userId);

            // when & then
            assertDoesNotThrow(() -> imageStore.remove(userId, request));
        }
    }
}
