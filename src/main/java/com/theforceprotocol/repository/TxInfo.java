package com.theforceprotocol.repository;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Data
@Table(name="txinfo")
public class TxInfo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Pattern(regexp = "0x[a-fA-F\\d]{40}", message = "invalid eth address")
    private String toaddress;

    private String txhash;

    private Integer nonce;

    private String amount;
}
