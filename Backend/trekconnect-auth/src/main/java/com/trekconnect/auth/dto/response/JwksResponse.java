package com.trekconnect.auth.dto.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwksResponse {

    private List<Map<String, Object>> keys;
}
