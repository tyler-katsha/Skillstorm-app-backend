package com.skillstorm.skillstorm.dto.extraDtos;

import com.skillstorm.skillstorm.dto.responses.SmallBadgeResponse;
import com.skillstorm.skillstorm.dto.responses.SmallQuizResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeSection {

    private List<SmallBadgeResponse> badgeSection;
    private List<SmallQuizResponse> quizSection;
}
