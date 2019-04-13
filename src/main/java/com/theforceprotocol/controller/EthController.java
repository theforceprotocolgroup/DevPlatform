package com.theforceprotocol.controller;

import com.alibaba.fastjson.JSON;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.theforceprotocol.blockchainrpc.USDTClient;
import com.theforceprotocol.blockchainrpc.ethclient.EthTransferClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author michael
 *
 */
@RestController
@RequestMapping(value = "/eth")
public class EthController {
    @RequestMapping("/transfer/{fromPrivateKey}/{toAddress}/{amount}")
    public String transferETHToAddress(@PathVariable String fromPrivateKey, @PathVariable String toAddress, @PathVariable String amount) throws Throwable{
        return JSON.toJSONString(EthTransferClient.EthInstance().transferETHToAddress(fromPrivateKey, toAddress, amount));
    }
}
