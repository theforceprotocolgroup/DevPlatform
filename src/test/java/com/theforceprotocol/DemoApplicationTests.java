package com.theforceprotocol;

import com.theforceprotocol.blockchainrpc.BitcoinClient;
import com.theforceprotocol.blockchainrpc.USDTClient;
import com.theforceprotocol.blockchainrpc.ethclient.EthTransferClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoApplicationTests {
    @Test
    public void withdrawBTCTest(){
        try {
            //https://www.blockchain.com/btc/tx/f5baeb37b71a3cf86bd084eb7111a173461350887e27e373f5604e5a98534223
            System.out.println(BitcoinClient.BtcInstance().withdrawBtc("3AqGr4neCJkEHZyYVhFk1pEGm3ZkXuQeGH", 0.0000056));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void withdrawUsdtTest(){
        try {
            //https://api.omniexplorer.info/v1/transaction/tx/e8572a7707afd0ef559e009c9d0e17fbd68a7b38b2bea4753e254e71fdcea42d
            System.out.println(USDTClient.UsdtInstance().withdrawUsdt("1F6bC8v7WoRSqNAC4kLotRzQJbRuW31c1s", 0.03));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Test
    public void withdrawEthTest(){
        try {
            System.out.println(EthTransferClient.EthInstance().transferETHToAddress("0xa080d9eb48ec8855340dd7a3cfc0e2745ec06960", new Double(0.0001).toString()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
