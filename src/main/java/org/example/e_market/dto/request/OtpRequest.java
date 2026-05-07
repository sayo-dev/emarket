package org.example.e_market.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Builder;
import org.example.e_market.entity.enums.OtpPurpose;
import org.example.e_market.utils.views.OtpView;

@Builder
public record OtpRequest(
        @JsonView(OtpView.Optional.class) String otp,
        @JsonView(OtpView.Base.class) String email,
        @JsonIgnore OtpPurpose purpose) {
}
