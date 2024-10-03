package com.example.demo.entity;

import com.example.demo.Enum.Source;
import com.example.demo.Enum.Symbol;
import jakarta.persistence.*;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "aggregated_price")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedPrice extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Source bidSource;

    @Enumerated(EnumType.STRING)
    private Source askSource;

    @Enumerated(EnumType.STRING)
    private Symbol symbol; // "ETHUSDT" or "BTCUSDT"

    private BigDecimal bidPrice;
    private BigDecimal bidQty;
    private BigDecimal askPrice;
    private BigDecimal askQty;

}
