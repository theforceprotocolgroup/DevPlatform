package com.theforceprotocol.controller;

import com.theforceprotocol.blockchainrpc.TransactionRecord;
import com.theforceprotocol.blockchainrpc.ethclient.EthTransferClient;
import com.theforceprotocol.repository.AirdropRepository;
import com.theforceprotocol.repository.AirdropResultRepository;
import com.theforceprotocol.repository.Forairdrop;
import com.theforceprotocol.repository.ForairdropResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/airdrop")
public class AirdropController {
    @Autowired
    private AirdropRepository airdropRepository;

    @Autowired
    private AirdropResultRepository airdropResultRepository;

    @GetMapping(path = "/add")
    public @ResponseBody String addAirdropUser(@RequestParam String address, @RequestParam String count) {
        Forairdrop forairdrop = new Forairdrop();
        forairdrop.setAddress(address);
        forairdrop.setCount(count);
        airdropRepository.save(forairdrop);
        return "add " + "address: " + address + ",count:" + count;
    }

    @GetMapping(path = "/del")
    public @ResponseBody String addAirdropUser(@RequestParam String id) {
        int index = Integer.parseInt(id.trim());
        airdropRepository.deleteById(index);
        return "del " + "id: " + id;
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Forairdrop> getAllUsers() {
        return airdropRepository.findAll();
    }

    @GetMapping(path="/airdrop")
    public @ResponseBody List<TransactionRecord> airdrop() throws Exception {
        Iterable<Forairdrop> userList = airdropRepository.findAll();
        List<TransactionRecord> trs = new ArrayList<TransactionRecord>();

        for (Forairdrop airdrop : userList) {
            TransactionRecord tr = EthTransferClient.EthInstance().transferERC20(airdrop.getAddress(), airdrop.getCount());
            trs.add(tr);

            ForairdropResult result = new ForairdropResult();
            result.setAddress(airdrop.getAddress());
            result.setCount(airdrop.getCount());
            result.setTxid(tr.getTxHash());

            airdropResultRepository.save(result);
        }

        return trs;
    }
}
