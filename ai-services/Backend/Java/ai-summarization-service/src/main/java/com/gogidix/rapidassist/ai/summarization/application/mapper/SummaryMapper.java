package com.gogidix.rapidassist.ai.summarization.application.mapper;

import com.gogidix.rapidassist.ai.summarization.application.dto.SummaryDto;
import com.gogidix.rapidassist.ai.summarization.domain.model.Summary;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface SummaryMapper {
    SummaryDto toDto(Summary summary);
    Summary toDomain(SummaryDto dto);
    List<SummaryDto> toDtoList(List<Summary> summaries);
}
