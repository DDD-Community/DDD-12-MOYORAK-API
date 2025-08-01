package com.moyorak.api.restaurant.service;

import com.moyorak.api.restaurant.domain.Restaurant;
import com.moyorak.api.restaurant.domain.RestaurantSearch;
import com.moyorak.api.restaurant.dto.RestaurantSaveRequest;
import com.moyorak.api.restaurant.dto.RestaurantSaveResponse;
import com.moyorak.api.restaurant.repository.RestaurantRepository;
import com.moyorak.api.restaurant.repository.RestaurantSearchRepository;
import com.moyorak.config.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantSearchRepository restaurantSearchRepository;

    @Transactional
    public RestaurantSaveResponse save(final RestaurantSaveRequest restaurantSaveRequest) {

        final Restaurant restaurant = restaurantSaveRequest.toRestaurant();

        final boolean isSaved =
                restaurantRepository
                        .findByNameAndRoundedLongitudeAndRoundedLatitudeAndUseTrue(
                                restaurantSaveRequest.name(),
                                restaurant.getRoundedLongitude(),
                                restaurant.getRoundedLatitude())
                        .isPresent();

        if (isSaved) {
            throw new BusinessException("식당이 이미 등록되어 있습니다.");
        }

        final Restaurant saved = restaurantRepository.save(restaurant);
        restaurantSearchRepository.save(
                RestaurantSearch.create(saved.getId(), saved.getName(), saved.getRoadAddress()));

        return RestaurantSaveResponse.create(saved.getId());
    }
}
