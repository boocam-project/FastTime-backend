package com.fasttime.docs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasttime.global.util.ResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest Api 에서 에러 관련을 고지할 목적으로 만든 API 입니다. 실제 API로는 제공이 되지 않습니다.
 */
@RestController
class CommonDocsController {

    @PostMapping("/common/docs")
    public ResponseDTO sample(@RequestBody @Valid SampleRequest request) {
        return ResponseDTO.res(HttpStatus.BAD_REQUEST, "공백이어서는 안됩니다.");
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SampleRequest {

        @NotEmpty(message = "공백이어서는 안됩니다.")
        @JsonProperty("name")
        private String name;

        @Email(message = "이메일 양식을 다시 확인해주세요")
        @JsonProperty("email")
        private String email;
    }
}
