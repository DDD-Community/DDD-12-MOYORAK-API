package com.moyorak.api.party.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.moyorak.api.party.domain.PartyAttendee;
import com.moyorak.api.party.domain.PartyAttendeeFixture;
import com.moyorak.api.party.dto.PartyAttendRequest;
import com.moyorak.api.party.repository.PartyAttendeeRepository;
import com.moyorak.config.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PartyAttendeeServiceTest {

    @InjectMocks private PartyAttendeeService partyAttendeeService;

    @Mock private PartyAttendeeRepository partyAttendeeRepository;

    @Nested
    @DisplayName("파티 참석 시")
    class Attend {

        final Long partyId = 1L;
        final Long userId = 1L;

        @Test
        @DisplayName("이미 파티에 참여 중이면 BusinessException이 발생한다.")
        void alreadyAttended() {
            // given
            final PartyAttendRequest request = new PartyAttendRequest(partyId, userId);
            final PartyAttendee existingAttendee =
                    PartyAttendeeFixture.fixture(1L, true, partyId, userId);

            given(partyAttendeeRepository.findByPartyIdAndUserIdAndUseTrue(partyId, userId))
                    .willReturn(Optional.of(existingAttendee));

            // when & then
            assertThatThrownBy(() -> partyAttendeeService.attend(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 파티에 참여하고 있습니다.");
        }
    }
}
