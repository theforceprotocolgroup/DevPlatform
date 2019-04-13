package com.theforceprotocol.controller;

import com.alibaba.fastjson.JSON;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.theforceprotocol.blockchainrpc.BitcoinClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.Map;

/**
 * @author michael
 *
 */
@RestController
@RequestMapping(value = "/btc")
public class BTCController {
    @RequestMapping("/withdraw/{toAddress}/{amount}")
    public String Withdraw(@PathVariable String toAddress, @PathVariable String amount) throws Throwable{
        return JSON.toJSONString(BitcoinClient.BtcInstance().withdrawBtc(toAddress, Double.parseDouble(amount)));
    }

    @RequestMapping("/transfer/{fromAddress}/{toAddress}/{amount}/{fromPrivateKey}")
    public String Transfer(@PathVariable String fromAddress, @PathVariable String toAddress, @PathVariable String amount, @PathVariable String fromPrivateKey) throws Throwable{
        return JSON.toJSONString(BitcoinClient.BtcInstance().transferBtc(fromAddress, toAddress, Double.parseDouble(amount), fromPrivateKey));
    }
}
