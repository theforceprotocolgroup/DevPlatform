package com.theforceprotocol.blockchainrpc;

import com.googlecode.jsonrpc4j.Base64;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.theforceprotocol.util.CommonUtils;
import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.entity.Balance;
import info.blockchain.api.blockexplorer.entity.FilterType;
import info.blockchain.api.blockexplorer.entity.UnspentOutput;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

public class BitcoinClient {
    private static JsonRpcHttpClient client;
    private static BitcoinClient btcInstance;
    private static final String RPC_USER = "";
    private static final String RPC_PASSWORD = "";
    private static String BTC_IP = "";

    private static String BTC_WALLET_PASSPHRASE = "foo";

    private static BlockExplorer blockinfoclient = new BlockExplorer();
    private final static String BTC_COLD_WALLET_ADDRESS = "";
    private final static String BTC_COLD_WALLET_PRIVATEKEY = "";

    public JsonRpcHttpClient init() {
        String cred = Base64.encodeBytes((RPC_USER + ":" + RPC_PASSWORD).getBytes());
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic " + cred);
        try {
            client = new JsonRpcHttpClient(new URL(BTC_IP), headers);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return client;
    }

    /*
        从Blockchaininfo中获取Finalbalance
     */
    public long getBalance(String address) throws Throwable {
        Map<String, Balance> balances = blockinfoclient.getBalance(Arrays.asList(address), FilterType.All);
        return balances.get(address).getFinalBalance();
    }

    /*
    归集和提现都是交易，只是业务方便地址不同, toAddress为提现地址
 */
    public TransactionRecord transferBtc(String fromAddress, String toAddress, double amount, String fromPrivateKey) {
        try {
            JsonRpcHttpClient client = Instance();

            long coldBtxBalance = getBalance(fromAddress);
            System.out.print("coldBtxBalance: " + coldBtxBalance);
            List<UnspentOutput> coldBtcUtxo = blockinfoclient.getUnspentOutputs(fromAddress);

            //遍历UTXO，直到UXTO总值大于交易数量，记下此时UTXO数量，然后计算手续费，
            // 如果手续费+amount<=此时的UTXO，可用
            // 否则再次遍历UTXO，直到符合手续费+amout<UTXO条件
            BigDecimal totalValueInSatoshi = BigDecimal.ZERO;
            BigDecimal totalValueInBTC = BigDecimal.ZERO;

            BigDecimal currentFeeInSatoshi = BigDecimal.ZERO;
            BigDecimal currentFeeInBTC = BigDecimal.ZERO;

            BigDecimal currentTotalOutInSatoshi = BigDecimal.ZERO;
            BigDecimal currentTotalOutInBTC = BigDecimal.ZERO;

            List<Map> utxoListMap = new ArrayList<>();

            for (UnspentOutput utxo : coldBtcUtxo) {
                int tx_output_n = utxo.getN();
                String tx_hash = CommonUtils.littleEndian2BigEndianString(utxo.getTransactionHash());
                String script = utxo.getScript();
                long tx_index = utxo.getTransactionIndex();
                long value = utxo.getValue();
                long confirmations = utxo.getConfirmations();

                Map inputT = new HashMap<>();
                inputT.put("txid", tx_hash);
                inputT.put("vout", tx_output_n);
                inputT.put("scriptPubKey", script);
                BigDecimal valueInSatoshi = new BigDecimal(value);//以聪为单位
                BigDecimal valueInBTC = valueInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);//转换为以BTC为单位
                inputT.put("amount", valueInBTC);//冷钱包的UTXO，注意构造地址，将手续费从冷钱包中扣除后，找零地址仍为冷钱包地址。解决了，新版本amount 是必须输入的参数，官网api太老了

                if (isScriptAddress(fromAddress)) {
                    String reedemScript = getReedemScriptByAddrInfo(fromAddress, script);
                    System.out.println("reedemScript: " + reedemScript);
                    inputT.put("redeemScript", reedemScript);
                }


                utxoListMap.add(inputT);

                totalValueInBTC = totalValueInBTC.add(valueInBTC);
                totalValueInSatoshi = totalValueInSatoshi.add(valueInSatoshi);

                currentFeeInSatoshi = CommonUtils.calculationFee(utxoListMap.size());
                currentFeeInBTC = currentFeeInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);

                currentTotalOutInSatoshi = totalValueInSatoshi.add(currentFeeInSatoshi);
                currentTotalOutInBTC = currentTotalOutInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);

                if (currentTotalOutInBTC.compareTo(totalValueInBTC) < 0) {
                    break;
                }

            }

            //计算字节大小和费用(因为是归集BTC 所以我用最小的输入来降低手续费)
            BigDecimal keyCount = CommonUtils.calculationFee(utxoListMap.size());
            //将聪换算成BTC
            BigDecimal transferFee = keyCount.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);
            BigDecimal outValue = BigDecimal.valueOf(amount);//交易金额

            double bal = BtcInstance().getBalance(fromAddress);
            BigDecimal balBd = new BigDecimal(bal, MathContext.DECIMAL64);

            BigDecimal valueInSatoshi = balBd;//以聪为单位
            BigDecimal valueInBTC = valueInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);//转换为以BTC为单位

            if (valueInBTC.compareTo(BigDecimal.ZERO) <= 0 || valueInBTC.compareTo(outValue) <= 0) {
                System.out.println("提现金额：" + outValue.toString());
                System.out.println("可用金额：" + valueInBTC.toString());
                System.out.println("需付手续费：" + transferFee.toString());
                System.out.println("实际金额小于提现或者小于0！不能提现！");
                return TransactionRecord.fail();
            }
            /**
             * 通过全节点构造原生交易
             */
            //创建BTC交易
            BigDecimal changeValue = valueInBTC.subtract(outValue).subtract(transferFee);
            if (changeValue.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("!提现金额：" + outValue.toString());
                System.out.println("!可用金额：" + valueInBTC.toString());
                System.out.println("!需付手续费：" + transferFee.toString());
                System.out.println("!实际金额小于提现或者小于0！不能提现！");
                return TransactionRecord.fail();
            }

            Map sendKV = new HashMap<>();//
            sendKV.put(toAddress, outValue.toString());//发给用户的BTC

            Map changeKV = new HashMap<>();
            changeKV.put(fromAddress, changeValue.toString());//找零，发给自己的BTC

            Object result = (Object) client.invoke("createrawtransaction", new Object[]{
                    //argsOne,
                    utxoListMap,
                    new Object[]{sendKV, changeKV}
            }, Object.class);
            String transaction = String.valueOf(result);
            System.out.print("createrawtransaction: " + transaction);
            /*//解锁钱包
             */
            client.invoke("walletpassphrase", new Object[]{BTC_WALLET_PASSPHRASE, 3600}, Object.class);

            List<String> privateKeys = Arrays.asList(fromPrivateKey);

            Map signrawtransaction = (Map) client.invoke("signrawtransaction", new Object[]{transaction, utxoListMap, privateKeys}, Object.class);
            if ((boolean) signrawtransaction.get("complete")) {
                System.out.println("before sendrawtransaction: " + signrawtransaction);

                //广播交易
                String txId = (String) client.invoke("sendrawtransaction", new Object[]{signrawtransaction.get("hex")}, Object.class);
                System.out.println("txid:" + txId);
                return TransactionRecord.success(txId);
            } else {
                return TransactionRecord.fail();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
        return TransactionRecord.fail();
    }

    /*
        客户提现BTC，服务端从冷钱包发送BTC数量（amount）到客户地址（toAddress）
        1.从冷钱包读取utxo, 采用info.blockchain.api获取
     */
    public TransactionRecord withdrawBtc(String toAddress, double amount) {
        return transferBtc(BTC_COLD_WALLET_ADDRESS, toAddress, amount, BTC_COLD_WALLET_PRIVATEKEY);
    }

    public static JsonRpcHttpClient Instance() {
        JsonRpcHttpClient client = null;
        if (client == null) {
            BitcoinClient btcClient = new BitcoinClient();
            client = btcClient.init();
        }
        return client;
    }

    public static BitcoinClient BtcInstance() {
        if (btcInstance == null) {
            btcInstance = new BitcoinClient();
            client = btcInstance.init();
        }
        return btcInstance;
    }

    public List<Map> getReedemScript(int minconf, int maxconf, String address) throws Throwable {
        List<String> addrList = Arrays.asList(address);
        List<Map> utxo = (List<Map>) Instance().invoke("listunspent", new Object[]{minconf, maxconf, addrList.toArray()}, Object.class);
        return utxo;
    }

    public List<Map> getReedemScript(String address) throws Throwable {
        return getReedemScript(1, 99999999, address);
    }

    public Map getaddressinfo(String address) throws Throwable {
        return (Map) Instance().invoke("getaddressinfo", new Object[]{address}, Object.class);
    }

    public String getReedemScriptByAddrInfo(String address, String scriptPubKey) throws Throwable {
        String rs = "";
        Map m = getaddressinfo(address);
        String m_address = (String) m.get("address");
        String m_scriptPubKey = (String) m.get("scriptPubKey");
        if ((boolean) m.get("isscript") && m_address.equals(address) && m_scriptPubKey.equals(scriptPubKey)) {
            rs = (String) m.get("hex");
        }
        return rs;
    }

    public boolean isScriptAddress(String address) throws Throwable {
        Map m = getaddressinfo(address);
        return (boolean) m.get("isscript");
    }
}
