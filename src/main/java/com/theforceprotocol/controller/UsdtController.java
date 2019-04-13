package com.theforceprotocol.controller;

import com.alibaba.fastjson.JSON;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.theforceprotocol.blockchainrpc.BitcoinClient;
import com.theforceprotocol.blockchainrpc.USDTClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael
 *
 */
@RestController
@RequestMapping(value = "/usdt")
public class UsdtController {
    @RequestMapping("/withdraw/{toAddress}/{amount}")
    public String Withdraw(@PathVariable String toAddress, @PathVariable String amount) throws Throwable{
        return JSON.toJSONString(USDTClient.UsdtInstance().withdrawUsdt(toAddress, Double.parseDouble(amount)));
    }

    @RequestMapping("/transfer/{fromAddress}/{toAddress}/{feeAddress}/{amount}/{fromPrivateKey}")
    public String Transfer(@PathVariable String fromAddress, @PathVariable String toAddress, @PathVariable String feeAddress, @PathVariable String amount, @PathVariable String fromPrivateKey) throws Throwable{
        return JSON.toJSONString(USDTClient.UsdtInstance().transferUsdt(fromAddress, toAddress, feeAddress, Double.parseDouble(amount), fromPrivateKey));
    }
}
