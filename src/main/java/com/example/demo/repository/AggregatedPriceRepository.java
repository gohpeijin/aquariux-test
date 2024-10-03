package com.example.demo.repository;

import com.example.demo.Enum.Symbol;
import com.example.demo.entity.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Long> {
    Optional<AggregatedPrice> findFirstBySymbolOrderByCreatedDateDesc(@Param("symbol") Symbol symbol);

    @Query("SELECT ap FROM AggregatedPrice ap WHERE ap.createdDate = (SELECT MAX(sub.createdDate) FROM AggregatedPrice sub WHERE sub.symbol = ap.symbol)")
    List<AggregatedPrice> findLatestPricesForAllSymbols();
}
