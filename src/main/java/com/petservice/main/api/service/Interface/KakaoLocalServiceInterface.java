package com.petservice.main.api.service.Interface;

import com.petservice.main.api.database.dto.Kakao.GeolocationResponse;
import com.petservice.main.api.database.dto.Kakao.KakaoLocalRequest;
import com.petservice.main.api.database.dto.Kakao.KakaoPlaceDTO;
import org.springframework.data.domain.Page;

public interface KakaoLocalServiceInterface {

  public GeolocationResponse geolocate();
  public Page<KakaoPlaceDTO> getLocalService(KakaoLocalRequest request);
}
