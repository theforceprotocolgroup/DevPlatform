package com.theforceprotocol.controller;

import com.theforceprotocol.blockchainrpc.TransactionRecord;
import com.theforceprotocol.blockchainrpc.ethclient.AccountHelper;
import com.theforceprotocol.blockchainrpc.ethclient.EthTransferClient;
import com.theforceprotocol.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(path = "/airdrop")
public class AirdropController {
    @Autowired
    private AirdropRepository airdropRepository;

    @Autowired
    private AirdropResultRepository airdropResultRepository;
    @Autowired
    private TxInfoRepository txInfoRepository;

    BigInteger curEtherScanNonce = new BigInteger("0");
    private static int nonce = 0;

    @GetMapping(path = "/add")
    public @ResponseBody
    String addAirdropUser(@RequestParam String address, @RequestParam String count) {
        Forairdrop forairdrop = new Forairdrop();
        forairdrop.setAddress(address);
        forairdrop.setCount(count);
        airdropRepository.save(forairdrop);
        return "add " + "address: " + address + ",count:" + count;
    }

    @GetMapping(path = "/del")
    public @ResponseBody
    String addAirdropUser(@RequestParam String id) {
        int index = Integer.parseInt(id.trim());
        airdropRepository.deleteById(index);
        return "del " + "id: " + id;
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<Forairdrop> getAllUsers() {
        return airdropRepository.findAll();
    }

    @GetMapping(path = "/airdrop")
    public @ResponseBody
    List<TransactionRecord> airdrop() throws Exception {
        Iterable<Forairdrop> userList = airdropRepository.findAll();
        List<TransactionRecord> trs = new ArrayList<>();

        curEtherScanNonce = EthTransferClient.EthInstance().getAddressNonce();

        for (Forairdrop airdrop : userList) {
            TransactionRecord tr = EthTransferClient.EthInstance().transferERC20ByNonce(airdrop.getAddress(), airdrop.getCount() + "000000000000000000", curEtherScanNonce);
            trs.add(tr);

            ForairdropResult result = new ForairdropResult();
            result.setAddress(airdrop.getAddress());
            result.setCount(airdrop.getCount());
            result.setTxid(tr.getTxHash());

            TxInfo txInfo = new TxInfo();
            txInfo.setAmount(airdrop.getCount());

            txInfo.setNonce(curEtherScanNonce.intValue());
            txInfo.setToaddress(airdrop.getAddress());
            txInfo.setTxhash(tr.getTxHash());
            txInfoRepository.save(txInfo);

            airdropResultRepository.save(result);

            curEtherScanNonce = curEtherScanNonce.add(BigInteger.valueOf(1));
        }

        return trs;
    }

    @GetMapping(path = "/ethkeygen")
    public @ResponseBody
    List<Map<String, String>> keyGen(@RequestParam String cnt) throws Exception {
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(cnt); i++) {
            AccountHelper accountHelper = new AccountHelper();
            String[] tuple = accountHelper.newAccount();
            Map<String, String> one = new HashMap<>();
            one.put("PrivateKey", tuple[0]);
            one.put("Address", "0x" + tuple[2]);
            result.add(one);
        }
        return result;
    }
}
