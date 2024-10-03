package com.example.demo.mapper;

import com.example.demo.entity.AggregatedPrice;
import com.example.demo.model.AggregatedPriceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AggregatedPriceMapper {

  AggregatedPrice toEntity(AggregatedPriceDTO dto);

  AggregatedPriceDTO toDto(AggregatedPrice entity);
}