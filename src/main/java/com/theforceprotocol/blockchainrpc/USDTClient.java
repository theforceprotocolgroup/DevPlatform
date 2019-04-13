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

public class USDTClient {
    private static JsonRpcHttpClient client;
    private static USDTClient usdtInstance;
    private static String getnewaddress;
    private static final String RPC_USER = "";
    private static final String RPC_PASSWORD = "";
    private static String USDT_IP = "";

    private static BlockExplorer blockinfoclient = new BlockExplorer();

    private final static String USDT_COLD_WALLET_ADDRESS = "";
    private final static String USDT_COLD_WALLET_PRIVATEKEY = "";

    public JsonRpcHttpClient init() {
        String cred = Base64.encodeBytes((RPC_USER + ":" + RPC_PASSWORD).getBytes());
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Authorization", "Basic " + cred);
        try {
            client = new JsonRpcHttpClient(new URL(USDT_IP), headers);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return client;
    }

    public double getBalance(String address, int propertyid) throws Throwable {
        Map map = (Map) Instance().invoke("omni_getbalance", new Object[]{address, propertyid}, Object.class);
        return Double.parseDouble((String) map.get("balance"));
    }

    /*
    从Blockchaininfo中获取Finalbalance
 */
    public long getBalance(String address) throws Throwable {
        Map<String, Balance> balances = blockinfoclient.getBalance(Arrays.asList(address), FilterType.All);
        return balances.get(address).getFinalBalance();
    }

    /*
    归集和提现都是交易，只是业务方便地址不同
 */
    public TransactionRecord transferUsdt(String fromAddress, String toAddress, String feeAddress, double amount, String fromPrivateKey) {
        try {
            List<UnspentOutput> coldUsdtUtxo = blockinfoclient.getUnspentOutputs(fromAddress);
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

            for (UnspentOutput utxo : coldUsdtUtxo) {
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
                inputT.put("value", valueInBTC);//冷钱包的UTXO，注意构造地址，将手续费从冷钱包中扣除后，找零地址仍为冷钱包地址。

                utxoListMap.add(inputT);

                totalValueInBTC = totalValueInBTC.add(valueInBTC);
                totalValueInSatoshi = totalValueInSatoshi.add(valueInSatoshi);

                currentFeeInSatoshi = CommonUtils.calculationFee(utxoListMap.size());
                currentFeeInBTC = currentFeeInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);

                currentTotalOutInSatoshi = totalValueInSatoshi.add(currentFeeInSatoshi);
                currentTotalOutInBTC = currentTotalOutInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);

                if (currentFeeInBTC.compareTo(totalValueInBTC) < 0) {
                    break;
                }
            }

            BigDecimal keyCount = CommonUtils.calculationFee(utxoListMap.size());
            //将聪换算成BTC
            BigDecimal transferFee = keyCount.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);
            BigDecimal outValue = BigDecimal.valueOf(amount);//交易金额

            double bal = UsdtInstance().getBalance(fromAddress, 31);
            BigDecimal balBd = new BigDecimal(bal, MathContext.DECIMAL64);
            if (balBd.compareTo(BigDecimal.ZERO) <= 0 || balBd.compareTo(outValue) <= 0) {
                System.out.println("USDT提现金额：" + outValue.toString());
                System.out.println("USDT可用金额：" + balBd.toString());
                System.out.println("需付BTC手续费：" + transferFee.toString());
                System.out.println("实际金额小于提现或者小于0！不能提现！");
                return TransactionRecord.fail();
            }
            /**
             * 通过全节点构造原生交易
             */
            long fromAddressBalInSatoshi = getBalance(fromAddress);
            BigDecimal valueInSatoshi = new BigDecimal(fromAddressBalInSatoshi, MathContext.DECIMAL64);

            //BigDecimal valueInSatoshi = balBd;//以聪为单位
            BigDecimal valueInBTC = valueInSatoshi.divide(new BigDecimal("100000000"), 8, RoundingMode.HALF_UP);//转换为以BTC为单位

            BigDecimal changeValue = valueInBTC.subtract(transferFee);
            if (changeValue.compareTo(BigDecimal.ZERO) <= 0) {

                System.out.println("!可用BTC金额：" + valueInBTC.toString());
                System.out.println("!需付手续费：" + transferFee.toString());
                System.out.println("!实际金额小于提现或者小于0！不能提现！");
                return TransactionRecord.fail();
            }
            Map args2 = new HashMap<>();// {}
            Object result = (Object) client.invoke("createrawtransaction", new Object[]{
                    utxoListMap,
                    args2 // '{}'
            }, Object.class);
            String transaction = String.valueOf(result);
            /*//解锁钱包
            client.invoke("walletpassphrase", new Object[]{"xxxx", 100}, Object.class);*/

            //创建Usdt交易 31和数量
            String simplesendResult = (String) client.invoke("omni_createpayload_simplesend", new Object[]{31, outValue.toString()}, Object.class);
            //usdt交易附加到BTC交易上
            String opreturnResult = (String) client.invoke("omni_createrawtx_opreturn", new Object[]{transaction, simplesendResult}, Object.class);

            //设置提现地址，toAddress是提现地址
            String reference = (String) client.invoke("omni_createrawtx_reference", new Object[]{opreturnResult, toAddress}, Object.class);

            //填写手续费及找零地址
            String changeResult = (String) client.invoke("omni_createrawtx_change", new Object[]{
                    reference,
                    utxoListMap, //交易信息
                    feeAddress,//找零地址
                    transferFee.toString() // 手续费
            }, Object.class);

            //获取原生交易hex,需要添加私钥信息
            List<String> privateKeys = Arrays.asList(fromPrivateKey);

            Map signrawtransaction = (Map) client.invoke("signrawtransaction", new Object[]{changeResult, utxoListMap, privateKeys}, Object.class);
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

    public TransactionRecord withdrawUsdt(String toAddress, double amount) {
        return transferUsdt(USDT_COLD_WALLET_ADDRESS, toAddress, USDT_COLD_WALLET_ADDRESS, amount, USDT_COLD_WALLET_PRIVATEKEY);
    }

    public static JsonRpcHttpClient Instance() {
        JsonRpcHttpClient client = null;
        if (client == null) {
            USDTClient usdtClient = new USDTClient();
            client = usdtClient.init();
        }
        return client;
    }

    public static USDTClient UsdtInstance() {
        if (usdtInstance == null) {
            usdtInstance = new USDTClient();
            client = usdtInstance.init();
        }
        return usdtInstance;
    }
}
