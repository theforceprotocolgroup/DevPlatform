== Blockchain ==
clearmempool
getbestblockhash
getblock "hash" ( verbose )
getblockchaininfo
getblockcount
getblockhash index
getblockheader "hash" ( verbose )
getchaintips
getdifficulty
getmempoolancestors txid (verbose)
getmempooldescendants txid (verbose)
getmempoolentry txid
getmempoolinfo
getrawmempool ( verbose )
gettxout "txid" n ( includemempool )
gettxoutproof ["txid",...] ( blockhash )
gettxoutsetinfo
verifychain ( checklevel numblocks )
verifytxoutproof "proof"

== Control ==
getinfo
help ( "command" )
stop

== Generating ==
generate numblocks ( maxtries )
generatetoaddress numblocks address (maxtries)

== Mining ==
getblocktemplate ( TemplateRequest )
getmininginfo
getnetworkhashps ( blocks height )
prioritisetransaction <txid> <priority delta> <fee delta>
submitblock "hexdata" ( "jsonparametersobject" )

== Network ==
addnode "node" "add|remove|onetry"
clearbanned
disconnectnode "node" 
getaddednodeinfo dummy ( "node" )
getconnectioncount
getnettotals
getnetworkinfo
getpeerinfo
listbanned
ping
setban "ip(/netmask)" "add|remove" (bantime) (absolute)

== Omni layer (configuration) ==
omni_setautocommit flag

== Omni layer (data retrieval) ==
omni_getactivations
omni_getactivecrowdsales
omni_getactivedexsells ( address )
omni_getallbalancesforaddress "address"
omni_getallbalancesforid propertyid
omni_getbalance "address" propertyid
omni_getbalanceshash propertyid
omni_getcrowdsale propertyid ( verbose )
omni_getcurrentconsensushash
omni_getfeecache ( propertyid )
omni_getfeedistribution distributionid
omni_getfeedistributions propertyid
omni_getfeeshare ( address ecosystem )
omni_getfeetrigger ( propertyid )
omni_getgrants propertyid
omni_getinfo
omni_getmetadexhash propertyId
omni_getorderbook propertyid ( propertyid )
omni_getpayload "txid"
omni_getproperty propertyid
omni_getseedblocks startblock endblock
omni_getsto "txid" "recipientfilter"
omni_gettrade "txid"
omni_gettradehistoryforaddress "address" ( count propertyid )
omni_gettradehistoryforpair propertyid propertyid ( count )
omni_gettransaction "txid"
omni_listblocktransactions index
omni_listpendingtransactions ( "address" )
omni_listproperties
omni_listtransactions ( "address" count skip startblock endblock )

== Omni layer (payload creation) ==
omni_createpayload_cancelalltrades ecosystem
omni_createpayload_canceltradesbypair propertyidforsale propertiddesired
omni_createpayload_canceltradesbyprice propertyidforsale "amountforsale" propertiddesired "amountdesired"
omni_createpayload_changeissuer propertyid
omni_createpayload_closecrowdsale propertyid
omni_senddexaccept propertyid "amount"
omni_createpayload_dexsell propertyidforsale "amountforsale" "amountdesired" paymentwindow minacceptfee action
omni_createpayload_disablefreezing propertyid
omni_createpayload_enablefreezing propertyid
omni_createpayload_freeze "toaddress" propertyid amount 
omni_createpayload_grant propertyid "amount" ( "memo" )
omni_createpayload_issuancecrowdsale ecosystem type previousid "category" "subcategory" "name" "url" "data" propertyiddesired tokensperunit deadline earlybonus issuerpercentage
omni_createpayload_issuancefixed ecosystem type previousid "category" "subcategory" "name" "url" "data" "amount"
omni_createpayload_issuancemanaged ecosystem type previousid "category" "subcategory" "name" "url" "data"
omni_createpayload_revoke propertyid "amount" ( "memo" )
omni_createpayload_sendall ecosystem
omni_createpayload_simplesend propertyid "amount"
omni_createpayload_sto propertyid "amount" ( distributionproperty )
omni_createpayload_trade propertyidforsale "amountforsale" propertiddesired "amountdesired"
omni_createpayload_unfreeze "toaddress" propertyid amount 

== Omni layer (raw transactions) ==
omni_createrawtx_change "rawtx" "prevtxs" "destination" fee ( position )
omni_createrawtx_input "rawtx" "txid" n
omni_createrawtx_multisig "rawtx" "payload" "seed" "redeemkey"
omni_createrawtx_opreturn "rawtx" "payload"
omni_createrawtx_reference "rawtx" "destination" ( amount )
omni_decodetransaction "rawtx" ( "prevtxs" height )

== Omni layer (transaction creation) ==
omni_send "fromaddress" "toaddress" propertyid "amount" ( "redeemaddress" "referenceamount" )
omni_sendall "fromaddress" "toaddress" ecosystem ( "redeemaddress" "referenceamount" )
omni_sendcancelalltrades "fromaddress" ecosystem
omni_sendcanceltradesbypair "fromaddress" propertyidforsale propertiddesired
omni_sendcanceltradesbyprice "fromaddress" propertyidforsale "amountforsale" propertiddesired "amountdesired"
omni_sendchangeissuer "fromaddress" "toaddress" propertyid
omni_sendclosecrowdsale "fromaddress" propertyid
omni_senddexaccept "fromaddress" "toaddress" propertyid "amount" ( override )
omni_senddexsell "fromaddress" propertyidforsale "amountforsale" "amountdesired" paymentwindow minacceptfee action
omni_senddisablefreezing "fromaddress" propertyid
omni_sendenablefreezing "fromaddress" propertyid
omni_sendfreeze "fromaddress" "toaddress" propertyid amount 
omni_sendgrant "fromaddress" "toaddress" propertyid "amount" ( "memo" )
omni_sendissuancecrowdsale "fromaddress" ecosystem type previousid "category" "subcategory" "name" "url" "data" propertyiddesired tokensperunit deadline ( earlybonus issuerpercentage )
omni_sendissuancefixed "fromaddress" ecosystem type previousid "category" "subcategory" "name" "url" "data" "amount"
omni_sendissuancemanaged "fromaddress" ecosystem type previousid "category" "subcategory" "name" "url" "data"
omni_sendrawtx "fromaddress" "rawtransaction" ( "referenceaddress" "redeemaddress" "referenceamount" )
omni_sendrevoke "fromaddress" propertyid "amount" ( "memo" )
omni_sendsto "fromaddress" propertyid "amount" ( "redeemaddress" distributionproperty )
omni_sendtrade "fromaddress" propertyidforsale "amountforsale" propertiddesired "amountdesired"
omni_sendunfreeze "fromaddress" "toaddress" propertyid amount 

== Rawtransactions ==
createrawtransaction [{"txid":"id","vout":n},...] {"address":amount,"data":"hex",...} ( locktime )
decoderawtransaction "hexstring"
decodescript "hex"
fundrawtransaction "hexstring" ( options )
getrawtransaction "txid" ( verbose )
sendrawtransaction "hexstring" ( allowhighfees )
signrawtransaction "hexstring" ( [{"txid":"id","vout":n,"scriptPubKey":"hex","redeemScript":"hex"},...] ["privatekey1",...] sighashtype )

== Util ==
createmultisig nrequired ["key",...]
estimatefee nblocks
estimatepriority nblocks
estimatesmartfee nblocks
estimatesmartpriority nblocks
signmessagewithprivkey "privkey" "message"
validateaddress "bitcoinaddress"
verifymessage "bitcoinaddress" "signature" "message"

== Wallet ==
abandontransaction "txid"
addmultisigaddress nrequired ["key",...] ( "account" )
addwitnessaddress "address"
backupwallet "destination"
dumpprivkey "bitcoinaddress"
dumpwallet "filename"
encryptwallet "passphrase"
getaccount "bitcoinaddress"
getaccountaddress "account"
getaddressesbyaccount "account"
getbalance ( "account" minconf includeWatchonly )
getnewaddress ( "account" )
getrawchangeaddress
getreceivedbyaccount "account" ( minconf )
getreceivedbyaddress "bitcoinaddress" ( minconf )
gettransaction "txid" ( includeWatchonly )
getunconfirmedbalance
getwalletinfo
importaddress "address" ( "label" rescan p2sh )
importprivkey "bitcoinprivkey" ( "label" rescan )
importprunedfunds
importpubkey "pubkey" ( "label" rescan )
importwallet "filename"
keypoolrefill ( newsize )
listaccounts ( minconf includeWatchonly)
listaddressgroupings
listlockunspent
listreceivedbyaccount ( minconf includeempty includeWatchonly)
listreceivedbyaddress ( minconf includeempty includeWatchonly)
listsinceblock ( "blockhash" target-confirmations includeWatchonly)
listtransactions ( "account" count from includeWatchonly)
listunspent ( minconf maxconf  ["address",...] )
lockunspent unlock ([{"txid":"txid","vout":n},...])
move "fromaccount" "toaccount" amount ( minconf "comment" )
removeprunedfunds "txid"
sendfrom "fromaccount" "tobitcoinaddress" amount ( minconf "comment" "comment-to" )
sendmany "fromaccount" {"address":amount,...} ( minconf "comment" ["address",...] )
sendtoaddress "bitcoinaddress" amount ( "comment" "comment-to" subtractfeefromamount )
setaccount "bitcoinaddress" "account"
settxfee amount
signmessage "bitcoinaddress" "message"