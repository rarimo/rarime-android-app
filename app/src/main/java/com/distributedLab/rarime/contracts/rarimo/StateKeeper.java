package com.distributedLab.rarime.contracts.rarimo;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.5.3.
 */
@SuppressWarnings("rawtypes")
public class StateKeeper extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_ICAO_PREFIX = "ICAO_PREFIX";

    public static final String FUNC_MAGIC_ID = "MAGIC_ID";

    public static final String FUNC_P = "P";

    public static final String FUNC_REVOKED = "REVOKED";

    public static final String FUNC___STATEKEEPER_INIT = "__StateKeeper_init";

    public static final String FUNC_ADDBOND = "addBond";

    public static final String FUNC_ADDCERTIFICATE = "addCertificate";

    public static final String FUNC_CERTIFICATESSMT = "certificatesSmt";

    public static final String FUNC_CHAINNAME = "chainName";

    public static final String FUNC_CHANGEICAOMASTERTREEROOT = "changeICAOMasterTreeRoot";

    public static final String FUNC_CHANGESIGNER = "changeSigner";

    public static final String FUNC_GETCERTIFICATEINFO = "getCertificateInfo";

    public static final String FUNC_GETNONCE = "getNonce";

    public static final String FUNC_GETPASSPORTINFO = "getPassportInfo";

    public static final String FUNC_GETREGISTRATIONBYKEY = "getRegistrationByKey";

    public static final String FUNC_GETREGISTRATIONS = "getRegistrations";

    public static final String FUNC_ICAOMASTERTREEMERKLEROOT = "icaoMasterTreeMerkleRoot";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISREGISTRATION = "isRegistration";

    public static final String FUNC_MOCKCHANGEICAOMASTERTREEROOT = "mockChangeICAOMasterTreeRoot";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REGISTRATIONSMT = "registrationSmt";

    public static final String FUNC_REISSUEBONDIDENTITY = "reissueBondIdentity";

    public static final String FUNC_REMOVECERTIFICATE = "removeCertificate";

    public static final String FUNC_REVOKEBOND = "revokeBond";

    public static final String FUNC_SIGNER = "signer";

    public static final String FUNC_UPDATEREGISTRATIONSET = "updateRegistrationSet";

    public static final String FUNC_UPGRADETO = "upgradeTo";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final String FUNC_UPGRADETOANDCALLWITHPROOF = "upgradeToAndCallWithProof";

    public static final String FUNC_UPGRADETOWITHPROOF = "upgradeToWithProof";

    public static final String FUNC_USESIGNATURE = "useSignature";

    public static final String FUNC_USEDSIGNATURES = "usedSignatures";

    public static final Event ADMINCHANGED_EVENT = new Event("AdminChanged", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event BEACONUPGRADED_EVENT = new Event("BeaconUpgraded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    public static final Event BONDADDED_EVENT = new Event("BondAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event BONDIDENTITYREISSUED_EVENT = new Event("BondIdentityReissued", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event BONDREVOKED_EVENT = new Event("BondRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event CERTIFICATEADDED_EVENT = new Event("CertificateAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CERTIFICATEREMOVED_EVENT = new Event("CertificateRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
    ;

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
    ;

    public static final Event UPGRADED_EVENT = new Event("Upgraded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected StateKeeper(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected StateKeeper(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected StateKeeper(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected StateKeeper(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<AdminChangedEventResponse> getAdminChangedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<AdminChangedEventResponse> responses = new ArrayList<AdminChangedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AdminChangedEventResponse typedResponse = new AdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static AdminChangedEventResponse getAdminChangedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ADMINCHANGED_EVENT, log);
        AdminChangedEventResponse typedResponse = new AdminChangedEventResponse();
        typedResponse.log = log;
        typedResponse.previousAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<AdminChangedEventResponse> adminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getAdminChangedEventFromLog(log));
    }

    public Flowable<AdminChangedEventResponse> adminChangedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADMINCHANGED_EVENT));
        return adminChangedEventFlowable(filter);
    }

    public static List<BeaconUpgradedEventResponse> getBeaconUpgradedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BEACONUPGRADED_EVENT, transactionReceipt);
        ArrayList<BeaconUpgradedEventResponse> responses = new ArrayList<BeaconUpgradedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BeaconUpgradedEventResponse typedResponse = new BeaconUpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.beacon = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BeaconUpgradedEventResponse getBeaconUpgradedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BEACONUPGRADED_EVENT, log);
        BeaconUpgradedEventResponse typedResponse = new BeaconUpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.beacon = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BeaconUpgradedEventResponse> beaconUpgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBeaconUpgradedEventFromLog(log));
    }

    public Flowable<BeaconUpgradedEventResponse> beaconUpgradedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BEACONUPGRADED_EVENT));
        return beaconUpgradedEventFlowable(filter);
    }

    public static List<BondAddedEventResponse> getBondAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDADDED_EVENT, transactionReceipt);
        ArrayList<BondAddedEventResponse> responses = new ArrayList<BondAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondAddedEventResponse typedResponse = new BondAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondAddedEventResponse getBondAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDADDED_EVENT, log);
        BondAddedEventResponse typedResponse = new BondAddedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondAddedEventResponse> bondAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondAddedEventFromLog(log));
    }

    public Flowable<BondAddedEventResponse> bondAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDADDED_EVENT));
        return bondAddedEventFlowable(filter);
    }

    public static List<BondIdentityReissuedEventResponse> getBondIdentityReissuedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDIDENTITYREISSUED_EVENT, transactionReceipt);
        ArrayList<BondIdentityReissuedEventResponse> responses = new ArrayList<BondIdentityReissuedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondIdentityReissuedEventResponse typedResponse = new BondIdentityReissuedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondIdentityReissuedEventResponse getBondIdentityReissuedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDIDENTITYREISSUED_EVENT, log);
        BondIdentityReissuedEventResponse typedResponse = new BondIdentityReissuedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondIdentityReissuedEventResponse> bondIdentityReissuedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondIdentityReissuedEventFromLog(log));
    }

    public Flowable<BondIdentityReissuedEventResponse> bondIdentityReissuedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDIDENTITYREISSUED_EVENT));
        return bondIdentityReissuedEventFlowable(filter);
    }

    public static List<BondRevokedEventResponse> getBondRevokedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDREVOKED_EVENT, transactionReceipt);
        ArrayList<BondRevokedEventResponse> responses = new ArrayList<BondRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondRevokedEventResponse typedResponse = new BondRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondRevokedEventResponse getBondRevokedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDREVOKED_EVENT, log);
        BondRevokedEventResponse typedResponse = new BondRevokedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.identityKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondRevokedEventResponse> bondRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondRevokedEventFromLog(log));
    }

    public Flowable<BondRevokedEventResponse> bondRevokedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDREVOKED_EVENT));
        return bondRevokedEventFlowable(filter);
    }

    public static List<CertificateAddedEventResponse> getCertificateAddedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CERTIFICATEADDED_EVENT, transactionReceipt);
        ArrayList<CertificateAddedEventResponse> responses = new ArrayList<CertificateAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CertificateAddedEventResponse typedResponse = new CertificateAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.expirationTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CertificateAddedEventResponse getCertificateAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CERTIFICATEADDED_EVENT, log);
        CertificateAddedEventResponse typedResponse = new CertificateAddedEventResponse();
        typedResponse.log = log;
        typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.expirationTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<CertificateAddedEventResponse> certificateAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCertificateAddedEventFromLog(log));
    }

    public Flowable<CertificateAddedEventResponse> certificateAddedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CERTIFICATEADDED_EVENT));
        return certificateAddedEventFlowable(filter);
    }

    public static List<CertificateRemovedEventResponse> getCertificateRemovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CERTIFICATEREMOVED_EVENT, transactionReceipt);
        ArrayList<CertificateRemovedEventResponse> responses = new ArrayList<CertificateRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CertificateRemovedEventResponse typedResponse = new CertificateRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CertificateRemovedEventResponse getCertificateRemovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CERTIFICATEREMOVED_EVENT, log);
        CertificateRemovedEventResponse typedResponse = new CertificateRemovedEventResponse();
        typedResponse.log = log;
        typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CertificateRemovedEventResponse> certificateRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCertificateRemovedEventFromLog(log));
    }

    public Flowable<CertificateRemovedEventResponse> certificateRemovedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CERTIFICATEREMOVED_EVENT));
        return certificateRemovedEventFlowable(filter);
    }

    public static List<InitializedEventResponse> getInitializedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InitializedEventResponse getInitializedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INITIALIZED_EVENT, log);
        InitializedEventResponse typedResponse = new InitializedEventResponse();
        typedResponse.log = log;
        typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInitializedEventFromLog(log));
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventFlowable(filter);
    }

    public static List<UpgradedEventResponse> getUpgradedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UPGRADED_EVENT, transactionReceipt);
        ArrayList<UpgradedEventResponse> responses = new ArrayList<UpgradedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradedEventResponse typedResponse = new UpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UpgradedEventResponse getUpgradedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UPGRADED_EVENT, log);
        UpgradedEventResponse typedResponse = new UpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUpgradedEventFromLog(log));
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADED_EVENT));
        return upgradedEventFlowable(filter);
    }

    public RemoteFunctionCall<String> ICAO_PREFIX() {
        final Function function = new Function(FUNC_ICAO_PREFIX, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> MAGIC_ID() {
        final Function function = new Function(FUNC_MAGIC_ID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> P() {
        final Function function = new Function(FUNC_P, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> REVOKED() {
        final Function function = new Function(FUNC_REVOKED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> __StateKeeper_init(String signer_, String chainName_, String registrationSmt_, String certificatesSmt_, byte[] icaoMasterTreeMerkleRoot_) {
        final Function function = new Function(
                FUNC___STATEKEEPER_INIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, signer_), 
                new org.web3j.abi.datatypes.Utf8String(chainName_), 
                new org.web3j.abi.datatypes.Address(160, registrationSmt_), 
                new org.web3j.abi.datatypes.Address(160, certificatesSmt_), 
                new org.web3j.abi.datatypes.generated.Bytes32(icaoMasterTreeMerkleRoot_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addBond(byte[] passportKey_, byte[] identityKey_, BigInteger dgCommit_) {
        final Function function = new Function(
                FUNC_ADDBOND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(identityKey_), 
                new org.web3j.abi.datatypes.generated.Uint256(dgCommit_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addCertificate(byte[] certificateKey_, BigInteger expirationTimestamp_) {
        final Function function = new Function(
                FUNC_ADDCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_), 
                new org.web3j.abi.datatypes.generated.Uint256(expirationTimestamp_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> certificatesSmt() {
        final Function function = new Function(FUNC_CERTIFICATESSMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> chainName() {
        final Function function = new Function(FUNC_CHAINNAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeICAOMasterTreeRoot(byte[] newRoot_, BigInteger timestamp, byte[] proof_) {
        final Function function = new Function(
                FUNC_CHANGEICAOMASTERTREEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(newRoot_), 
                new org.web3j.abi.datatypes.generated.Uint256(timestamp), 
                new org.web3j.abi.datatypes.DynamicBytes(proof_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> changeSigner(byte[] newSignerPubKey_, byte[] signature_) {
        final Function function = new Function(
                FUNC_CHANGESIGNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicBytes(newSignerPubKey_), 
                new org.web3j.abi.datatypes.DynamicBytes(signature_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<CertificateInfo> getCertificateInfo(byte[] certificateKey_) {
        final Function function = new Function(FUNC_GETCERTIFICATEINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<CertificateInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, CertificateInfo.class);
    }

    public RemoteFunctionCall<BigInteger> getNonce(BigInteger methodId_) {
        final Function function = new Function(FUNC_GETNONCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(methodId_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<PassportInfo, IdentityInfo>> getPassportInfo(byte[] passportKey_) {
        final Function function = new Function(FUNC_GETPASSPORTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<PassportInfo>() {}, new TypeReference<IdentityInfo>() {}));
        return new RemoteFunctionCall<Tuple2<PassportInfo, IdentityInfo>>(function,
                new Callable<Tuple2<PassportInfo, IdentityInfo>>() {
                    @Override
                    public Tuple2<PassportInfo, IdentityInfo> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<PassportInfo, IdentityInfo>(
                                (PassportInfo) results.get(0), 
                                (IdentityInfo) results.get(1));
                    }
                });
    }

    public RemoteFunctionCall<String> getRegistrationByKey(String key_) {
        final Function function = new Function(FUNC_GETREGISTRATIONBYKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<List<String>, List<String>>> getRegistrations() {
        final Function function = new Function(FUNC_GETREGISTRATIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<Tuple2<List<String>, List<String>>>(function,
                new Callable<Tuple2<List<String>, List<String>>>() {
                    @Override
                    public Tuple2<List<String>, List<String>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<String>, List<String>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()), 
                                convertToNative((List<Address>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<byte[]> icaoMasterTreeMerkleRoot() {
        final Function function = new Function(FUNC_ICAOMASTERTREEMERKLEROOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> isRegistration(String registration_) {
        final Function function = new Function(FUNC_ISREGISTRATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registration_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> mockChangeICAOMasterTreeRoot(byte[] newRoot_) {
        final Function function = new Function(
                FUNC_MOCKCHANGEICAOMASTERTREEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(newRoot_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> registrationSmt() {
        final Function function = new Function(FUNC_REGISTRATIONSMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> reissueBondIdentity(byte[] passportKey_, byte[] identityKey_, BigInteger dgCommit_) {
        final Function function = new Function(
                FUNC_REISSUEBONDIDENTITY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(identityKey_), 
                new org.web3j.abi.datatypes.generated.Uint256(dgCommit_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeCertificate(byte[] certificateKey_) {
        final Function function = new Function(
                FUNC_REMOVECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeBond(byte[] passportKey_, byte[] identityKey_) {
        final Function function = new Function(
                FUNC_REVOKEBOND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(identityKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> signer() {
        final Function function = new Function(FUNC_SIGNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updateRegistrationSet(BigInteger methodId_, byte[] data_, byte[] proof_) {
        final Function function = new Function(
                FUNC_UPDATEREGISTRATIONSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(methodId_), 
                new org.web3j.abi.datatypes.DynamicBytes(data_), 
                new org.web3j.abi.datatypes.DynamicBytes(proof_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeTo(String newImplementation) {
        final Function function = new Function(
                FUNC_UPGRADETO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newImplementation)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCall(String newImplementation, byte[] data, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newImplementation), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCallWithProof(String newImplementation_, byte[] proof_, byte[] data_) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALLWITHPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newImplementation_), 
                new org.web3j.abi.datatypes.DynamicBytes(proof_), 
                new org.web3j.abi.datatypes.DynamicBytes(data_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToWithProof(String newImplementation_, byte[] proof_) {
        final Function function = new Function(
                FUNC_UPGRADETOWITHPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newImplementation_), 
                new org.web3j.abi.datatypes.DynamicBytes(proof_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> useSignature(byte[] sigHash_) {
        final Function function = new Function(
                FUNC_USESIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sigHash_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> usedSignatures(byte[] param0) {
        final Function function = new Function(FUNC_USEDSIGNATURES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static StateKeeper load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new StateKeeper(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static StateKeeper load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new StateKeeper(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static StateKeeper load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new StateKeeper(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static StateKeeper load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new StateKeeper(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class CertificateInfo extends StaticStruct {
        public BigInteger expirationTimestamp;

        public CertificateInfo(BigInteger expirationTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Uint64(expirationTimestamp));
            this.expirationTimestamp = expirationTimestamp;
        }

        public CertificateInfo(Uint64 expirationTimestamp) {
            super(expirationTimestamp);
            this.expirationTimestamp = expirationTimestamp.getValue();
        }
    }

    public static class PassportInfo extends StaticStruct {
        public byte[] activeIdentity;

        public BigInteger identityReissueCounter;

        public PassportInfo(byte[] activeIdentity, BigInteger identityReissueCounter) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(activeIdentity), 
                    new org.web3j.abi.datatypes.generated.Uint64(identityReissueCounter));
            this.activeIdentity = activeIdentity;
            this.identityReissueCounter = identityReissueCounter;
        }

        public PassportInfo(Bytes32 activeIdentity, Uint64 identityReissueCounter) {
            super(activeIdentity, identityReissueCounter);
            this.activeIdentity = activeIdentity.getValue();
            this.identityReissueCounter = identityReissueCounter.getValue();
        }
    }

    public static class IdentityInfo extends StaticStruct {
        public byte[] activePassport;

        public BigInteger issueTimestamp;

        public IdentityInfo(byte[] activePassport, BigInteger issueTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(activePassport), 
                    new org.web3j.abi.datatypes.generated.Uint64(issueTimestamp));
            this.activePassport = activePassport;
            this.issueTimestamp = issueTimestamp;
        }

        public IdentityInfo(Bytes32 activePassport, Uint64 issueTimestamp) {
            super(activePassport, issueTimestamp);
            this.activePassport = activePassport.getValue();
            this.issueTimestamp = issueTimestamp.getValue();
        }
    }

    public static class AdminChangedEventResponse extends BaseEventResponse {
        public String previousAdmin;

        public String newAdmin;
    }

    public static class BeaconUpgradedEventResponse extends BaseEventResponse {
        public String beacon;
    }

    public static class BondAddedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] identityKey;
    }

    public static class BondIdentityReissuedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] identityKey;
    }

    public static class BondRevokedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] identityKey;
    }

    public static class CertificateAddedEventResponse extends BaseEventResponse {
        public byte[] certificateKey;

        public BigInteger expirationTimestamp;
    }

    public static class CertificateRemovedEventResponse extends BaseEventResponse {
        public byte[] certificateKey;
    }

    public static class InitializedEventResponse extends BaseEventResponse {
        public BigInteger version;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }
}
