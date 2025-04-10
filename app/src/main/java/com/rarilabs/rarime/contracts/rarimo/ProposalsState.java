package com.rarilabs.rarime.contracts.rarimo;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.abi.datatypes.reflection.Parameterized;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.reactivex.Flowable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.6.0.
 */
@SuppressWarnings("rawtypes")
public class ProposalsState extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_MAGIC_ID = "MAGIC_ID";

    public static final String FUNC_MAXIMUM_CHOICES_PER_OPTION = "MAXIMUM_CHOICES_PER_OPTION";

    public static final String FUNC_MAXIMUM_OPTIONS = "MAXIMUM_OPTIONS";

    public static final String FUNC_P = "P";

    public static final String FUNC_GETPROPOSALINFO = "getProposalInfo";

    public static final String FUNC___PROPOSALSSTATE_INIT = "__ProposalsState_init";

    public static final String FUNC_ADDVOTING = "addVoting";

    public static final String FUNC_CHAINNAME = "chainName";

    public static final String FUNC_CHANGEPROPOSALCONFIG = "changeProposalConfig";

    public static final String FUNC_CHANGESIGNER = "changeSigner";

    public static final String FUNC_CREATEPROPOSAL = "createProposal";

    public static final String FUNC_GETNONCE = "getNonce";

    public static final String FUNC_GETPROPOSALCONFIG = "getProposalConfig";

    public static final String FUNC_GETPROPOSALEVENTID = "getProposalEventId";

    public static final String FUNC_GETPROPOSALSTATUS = "getProposalStatus";

    public static final String FUNC_GETVOTINGBYKEY = "getVotingByKey";

    public static final String FUNC_GETVOTINGS = "getVotings";

    public static final String FUNC_HIDEPROPOSAL = "hideProposal";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISVOTING = "isVoting";

    public static final String FUNC_LASTPROPOSALID = "lastProposalId";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PROPOSALSMTIMPL = "proposalSMTImpl";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REMOVEVOTING = "removeVoting";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SIGNER = "signer";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final String FUNC_UPGRADETO = "upgradeTo";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final String FUNC_UPGRADETOANDCALLWITHPROOF = "upgradeToAndCallWithProof";

    public static final String FUNC_UPGRADETOWITHPROOF = "upgradeToWithProof";

    public static final String FUNC_VOTE = "vote";

    public static final Event ADMINCHANGED_EVENT = new Event("AdminChanged",
            Arrays.asList(new TypeReference<Address>() {
            }, new TypeReference<Address>() {
            }));

    public static final Event BEACONUPGRADED_EVENT = new Event("BeaconUpgraded",
            List.of(new TypeReference<Address>(true) {
            }));

    public static final Event INITIALIZED_EVENT = new Event("Initialized",
            List.of(new TypeReference<Uint8>() {
            }));

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred",
            Arrays.asList(new TypeReference<Address>(true) {
            }, new TypeReference<Address>(true) {
            }));

    public static final Event PROPOSALCONFIGCHANGED_EVENT = new Event("ProposalConfigChanged",
            List.of(new TypeReference<Uint256>(true) {
            }));

    public static final Event PROPOSALCREATED_EVENT = new Event("ProposalCreated",
            Arrays.asList(new TypeReference<Uint256>(true) {
            }, new TypeReference<Address>() {
            }));

    public static final Event PROPOSALHIDDEN_EVENT = new Event("ProposalHidden",
            Arrays.asList(new TypeReference<Uint256>(true) {
            }, new TypeReference<Bool>() {
            }));

    public static final Event UPGRADED_EVENT = new Event("Upgraded",
            List.of(new TypeReference<Address>(true) {
            }));

    public static final Event VOTECAST_EVENT = new Event("VoteCast",
            Arrays.asList(new TypeReference<Uint256>(true) {
            }, new TypeReference<Uint256>() {
            }, new TypeReference<DynamicArray<Uint256>>() {
            }));

    @Deprecated
    protected ProposalsState(String contractAddress, Web3j web3j, Credentials credentials,
                             BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ProposalsState(String contractAddress, Web3j web3j, Credentials credentials,
                             ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ProposalsState(String contractAddress, Web3j web3j,
                             TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ProposalsState(String contractAddress, Web3j web3j,
                             TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<AdminChangedEventResponse> getAdminChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(ADMINCHANGED_EVENT, transactionReceipt);
        ArrayList<AdminChangedEventResponse> responses = new ArrayList<AdminChangedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            AdminChangedEventResponse typedResponse = new AdminChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static AdminChangedEventResponse getAdminChangedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(ADMINCHANGED_EVENT, log);
        AdminChangedEventResponse typedResponse = new AdminChangedEventResponse();
        typedResponse.log = log;
        typedResponse.previousAdmin = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.newAdmin = (String) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<AdminChangedEventResponse> adminChangedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getAdminChangedEventFromLog(log));
    }

    public Flowable<AdminChangedEventResponse> adminChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADMINCHANGED_EVENT));
        return adminChangedEventFlowable(filter);
    }

    public static List<BeaconUpgradedEventResponse> getBeaconUpgradedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BEACONUPGRADED_EVENT, transactionReceipt);
        ArrayList<BeaconUpgradedEventResponse> responses = new ArrayList<BeaconUpgradedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            BeaconUpgradedEventResponse typedResponse = new BeaconUpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.beacon = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BeaconUpgradedEventResponse getBeaconUpgradedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BEACONUPGRADED_EVENT, log);
        BeaconUpgradedEventResponse typedResponse = new BeaconUpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.beacon = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<BeaconUpgradedEventResponse> beaconUpgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBeaconUpgradedEventFromLog(log));
    }

    public Flowable<BeaconUpgradedEventResponse> beaconUpgradedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BEACONUPGRADED_EVENT));
        return beaconUpgradedEventFlowable(filter);
    }

    public static List<InitializedEventResponse> getInitializedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InitializedEventResponse getInitializedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INITIALIZED_EVENT, log);
        InitializedEventResponse typedResponse = new InitializedEventResponse();
        typedResponse.log = log;
        typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInitializedEventFromLog(log));
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventFlowable(filter);
    }

    public static List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnershipTransferredEventResponse getOwnershipTransferredEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
        OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
        typedResponse.log = log;
        typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnershipTransferredEventFromLog(log));
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public static List<ProposalConfigChangedEventResponse> getProposalConfigChangedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPOSALCONFIGCHANGED_EVENT, transactionReceipt);
        ArrayList<ProposalConfigChangedEventResponse> responses = new ArrayList<ProposalConfigChangedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ProposalConfigChangedEventResponse typedResponse = new ProposalConfigChangedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ProposalConfigChangedEventResponse getProposalConfigChangedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPOSALCONFIGCHANGED_EVENT, log);
        ProposalConfigChangedEventResponse typedResponse = new ProposalConfigChangedEventResponse();
        typedResponse.log = log;
        typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ProposalConfigChangedEventResponse> proposalConfigChangedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getProposalConfigChangedEventFromLog(log));
    }

    public Flowable<ProposalConfigChangedEventResponse> proposalConfigChangedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPOSALCONFIGCHANGED_EVENT));
        return proposalConfigChangedEventFlowable(filter);
    }

    public static List<ProposalCreatedEventResponse> getProposalCreatedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPOSALCREATED_EVENT, transactionReceipt);
        ArrayList<ProposalCreatedEventResponse> responses = new ArrayList<ProposalCreatedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ProposalCreatedEventResponse typedResponse = new ProposalCreatedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.proposalSMT = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ProposalCreatedEventResponse getProposalCreatedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPOSALCREATED_EVENT, log);
        ProposalCreatedEventResponse typedResponse = new ProposalCreatedEventResponse();
        typedResponse.log = log;
        typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.proposalSMT = (String) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ProposalCreatedEventResponse> proposalCreatedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getProposalCreatedEventFromLog(log));
    }

    public Flowable<ProposalCreatedEventResponse> proposalCreatedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPOSALCREATED_EVENT));
        return proposalCreatedEventFlowable(filter);
    }

    public static List<ProposalHiddenEventResponse> getProposalHiddenEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPOSALHIDDEN_EVENT, transactionReceipt);
        ArrayList<ProposalHiddenEventResponse> responses = new ArrayList<ProposalHiddenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ProposalHiddenEventResponse typedResponse = new ProposalHiddenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.hide = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ProposalHiddenEventResponse getProposalHiddenEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPOSALHIDDEN_EVENT, log);
        ProposalHiddenEventResponse typedResponse = new ProposalHiddenEventResponse();
        typedResponse.log = log;
        typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.hide = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<ProposalHiddenEventResponse> proposalHiddenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getProposalHiddenEventFromLog(log));
    }

    public Flowable<ProposalHiddenEventResponse> proposalHiddenEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPOSALHIDDEN_EVENT));
        return proposalHiddenEventFlowable(filter);
    }

    public static List<UpgradedEventResponse> getUpgradedEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UPGRADED_EVENT, transactionReceipt);
        ArrayList<UpgradedEventResponse> responses = new ArrayList<UpgradedEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            UpgradedEventResponse typedResponse = new UpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UpgradedEventResponse getUpgradedEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UPGRADED_EVENT, log);
        UpgradedEventResponse typedResponse = new UpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUpgradedEventFromLog(log));
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(DefaultBlockParameter startBlock,
                                                                 DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADED_EVENT));
        return upgradedEventFlowable(filter);
    }

    public static List<VoteCastEventResponse> getVoteCastEvents(
            TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = staticExtractEventParametersWithLog(VOTECAST_EVENT, transactionReceipt);
        ArrayList<VoteCastEventResponse> responses = new ArrayList<VoteCastEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            VoteCastEventResponse typedResponse = new VoteCastEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.userNullifier = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.vote = (List<BigInteger>) ((Array) eventValues.getNonIndexedValues().get(1)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static VoteCastEventResponse getVoteCastEventFromLog(Log log) {
        EventValuesWithLog eventValues = staticExtractEventParametersWithLog(VOTECAST_EVENT, log);
        VoteCastEventResponse typedResponse = new VoteCastEventResponse();
        typedResponse.log = log;
        typedResponse.proposalId = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.userNullifier = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.vote = (List<BigInteger>) ((Array) eventValues.getNonIndexedValues().get(1)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<VoteCastEventResponse> voteCastEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getVoteCastEventFromLog(log));
    }

    public Flowable<VoteCastEventResponse> voteCastEventFlowable(DefaultBlockParameter startBlock,
                                                                 DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(VOTECAST_EVENT));
        return voteCastEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> MAGIC_ID() {
        final Function function = new Function(FUNC_MAGIC_ID,
                List.of(),
                List.of(new TypeReference<Uint8>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAXIMUM_CHOICES_PER_OPTION() {
        final Function function = new Function(FUNC_MAXIMUM_CHOICES_PER_OPTION,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAXIMUM_OPTIONS() {
        final Function function = new Function(FUNC_MAXIMUM_OPTIONS,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> P() {
        final Function function = new Function(FUNC_P,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> __ProposalsState_init(String signer_,
                                                                        String chainName_, String proposalSMTImpl_) {
        final Function function = new Function(
                FUNC___PROPOSALSSTATE_INIT,
                Arrays.asList(new Address(160, signer_),
                        new Utf8String(chainName_),
                        new Address(160, proposalSMTImpl_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addVoting(String votingName_,
                                                            String votingAddress_) {
        final Function function = new Function(
                FUNC_ADDVOTING,
                Arrays.asList(new Utf8String(votingName_),
                        new Address(160, votingAddress_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> chainName() {
        final Function function = new Function(FUNC_CHAINNAME,
                List.of(),
                List.of(new TypeReference<Utf8String>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeProposalConfig(BigInteger proposalId_,
                                                                       ProposalConfig newProposalConfig_) {
        final Function function = new Function(
                FUNC_CHANGEPROPOSALCONFIG,
                Arrays.asList(new Uint256(proposalId_),
                        newProposalConfig_),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> changeSigner(byte[] newSignerPubKey_,
                                                               byte[] signature_) {
        final Function function = new Function(
                FUNC_CHANGESIGNER,
                Arrays.asList(new DynamicBytes(newSignerPubKey_),
                        new DynamicBytes(signature_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> createProposal(ProposalConfig proposalConfig_) {
        final Function function = new Function(
                FUNC_CREATEPROPOSAL,
                Collections.singletonList(proposalConfig_),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> getNonce(BigInteger methodId_) {
        final Function function = new Function(FUNC_GETNONCE,
                List.of(new Uint8(methodId_)),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<ProposalConfig> getProposalConfig(BigInteger proposalId_) {
        final Function function = new Function(FUNC_GETPROPOSALCONFIG,
                List.of(new Uint256(proposalId_)),
                List.of(new TypeReference<ProposalConfig>() {
                }));
        return executeRemoteCallSingleValueReturn(function, ProposalConfig.class);
    }

    public RemoteFunctionCall<BigInteger> getProposalEventId(BigInteger proposalId_) {
        final Function function = new Function(FUNC_GETPROPOSALEVENTID, List.of(new Uint256(proposalId_)), List.of(new TypeReference<Uint256>() {
        }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getProposalStatus(BigInteger proposalId_) {
        final Function function = new Function(FUNC_GETPROPOSALSTATUS,
                List.of(new Uint256(proposalId_)),
                List.of(new TypeReference<Uint8>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getVotingByKey(String key_) {
        final Function function = new Function(FUNC_GETVOTINGBYKEY,
                List.of(new Utf8String(key_)),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<List<String>, List<String>>> getVotings() {
        final Function function = new Function(FUNC_GETVOTINGS,
                List.of(),
                Arrays.asList(new TypeReference<DynamicArray<Utf8String>>() {
                }, new TypeReference<DynamicArray<Address>>() {
                }));
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

    public RemoteFunctionCall<TransactionReceipt> hideProposal(BigInteger proposalId_,
                                                               Boolean hide_) {
        final Function function = new Function(
                FUNC_HIDEPROPOSAL,
                Arrays.asList(new Uint256(proposalId_),
                        new Bool(hide_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> isVoting(String voting_) {
        final Function function = new Function(FUNC_ISVOTING,
                List.of(new Address(160, voting_)),
                List.of(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> lastProposalId() {
        final Function function = new Function(FUNC_LASTPROPOSALID,
                List.of(),
                List.of(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> proposalSMTImpl() {
        final Function function = new Function(FUNC_PROPOSALSMTIMPL,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID,
                List.of(),
                List.of(new TypeReference<Bytes32>() {
                }));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeVoting(String votingName_) {
        final Function function = new Function(
                FUNC_REMOVEVOTING,
                List.of(new Utf8String(votingName_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP,
                List.of(),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> signer() {
        final Function function = new Function(FUNC_SIGNER,
                List.of(),
                List.of(new TypeReference<Address>() {
                }));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final Function function = new Function(
                FUNC_TRANSFEROWNERSHIP,
                List.of(new Address(160, newOwner)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeTo(String newImplementation) {
        final Function function = new Function(
                FUNC_UPGRADETO,
                List.of(new Address(160, newImplementation)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCall(String newImplementation,
                                                                   byte[] data, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALL,
                Arrays.asList(new Address(160, newImplementation),
                        new DynamicBytes(data)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCallWithProof(
            String newImplementation_, byte[] proof_, byte[] data_) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALLWITHPROOF,
                Arrays.asList(new Address(160, newImplementation_),
                        new DynamicBytes(proof_),
                        new DynamicBytes(data_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToWithProof(String newImplementation_,
                                                                     byte[] proof_) {
        final Function function = new Function(
                FUNC_UPGRADETOWITHPROOF,
                Arrays.asList(new Address(160, newImplementation_),
                        new DynamicBytes(proof_)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> vote(BigInteger proposalId_,
                                                       BigInteger userNullifier_, List<BigInteger> vote_) {
        final Function function = new Function(
                FUNC_VOTE,
                Arrays.asList(new Uint256(proposalId_),
                        new Uint256(userNullifier_),
                        new DynamicArray<Uint256>(
                                Uint256.class,
                                org.web3j.abi.Utils.typeMap(vote_, Uint256.class))),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ProposalsState load(String contractAddress, Web3j web3j, Credentials credentials,
                                      BigInteger gasPrice, BigInteger gasLimit) {
        return new ProposalsState(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ProposalsState load(String contractAddress, Web3j web3j,
                                      TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ProposalsState(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ProposalsState load(String contractAddress, Web3j web3j, Credentials credentials,
                                      ContractGasProvider contractGasProvider) {
        return new ProposalsState(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ProposalsState load(String contractAddress, Web3j web3j,
                                      TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ProposalsState(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class ProposalConfig extends DynamicStruct {
        public BigInteger startTimestamp;

        public BigInteger duration;

        public BigInteger multichoice;

        public List<BigInteger> acceptedOptions;

        public String description;

        public List<String> votingWhitelist;

        public List<byte[]> votingWhitelistData;

        public ProposalConfig(BigInteger startTimestamp, BigInteger duration,
                              BigInteger multichoice, List<BigInteger> acceptedOptions, String description,
                              List<String> votingWhitelist, List<byte[]> votingWhitelistData) {
            super(new Uint64(startTimestamp),
                    new Uint64(duration),
                    new Uint256(multichoice),
                    new DynamicArray<Uint256>(
                            Uint256.class,
                            org.web3j.abi.Utils.typeMap(acceptedOptions, Uint256.class)),
                    new Utf8String(description),
                    new DynamicArray<Address>(
                            Address.class,
                            org.web3j.abi.Utils.typeMap(votingWhitelist, Address.class)),
                    new DynamicArray<DynamicBytes>(
                            DynamicBytes.class,
                            org.web3j.abi.Utils.typeMap(votingWhitelistData, DynamicBytes.class)));
            this.startTimestamp = startTimestamp;
            this.duration = duration;
            this.multichoice = multichoice;
            this.acceptedOptions = acceptedOptions;
            this.description = description;
            this.votingWhitelist = votingWhitelist;
            this.votingWhitelistData = votingWhitelistData;
        }

        public ProposalConfig(Uint64 startTimestamp, Uint64 duration, Uint256 multichoice,
                              @Parameterized(type = Uint256.class) DynamicArray<Uint256> acceptedOptions,
                              Utf8String description,
                              @Parameterized(type = Address.class) DynamicArray<Address> votingWhitelist,
                              @Parameterized(type = DynamicBytes.class) DynamicArray<DynamicBytes> votingWhitelistData) {
            super(startTimestamp, duration, multichoice, acceptedOptions, description, votingWhitelist, votingWhitelistData);
            this.startTimestamp = startTimestamp.getValue();
            this.duration = duration.getValue();
            this.multichoice = multichoice.getValue();
            this.acceptedOptions = acceptedOptions.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
            this.description = description.getValue();
            this.votingWhitelist = votingWhitelist.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
            this.votingWhitelistData = votingWhitelistData.getValue().stream().map(v -> v.getValue()).collect(Collectors.toList());
        }
    }

    public static class AdminChangedEventResponse extends BaseEventResponse {
        public String previousAdmin;

        public String newAdmin;
    }

    public static class BeaconUpgradedEventResponse extends BaseEventResponse {
        public String beacon;
    }

    public static class InitializedEventResponse extends BaseEventResponse {
        public BigInteger version;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class ProposalConfigChangedEventResponse extends BaseEventResponse {
        public BigInteger proposalId;
    }

    public static class ProposalCreatedEventResponse extends BaseEventResponse {
        public BigInteger proposalId;

        public String proposalSMT;
    }

    public static class ProposalHiddenEventResponse extends BaseEventResponse {
        public BigInteger proposalId;

        public Boolean hide;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }

    public static class VoteCastEventResponse extends BaseEventResponse {
        public BigInteger proposalId;

        public BigInteger userNullifier;

        public List<BigInteger> vote;
    }


    public RemoteCall<ProposalInfo> getProposalInfo(BigInteger proposalId) {
        final Function function = new Function(FUNC_GETPROPOSALINFO, List.of(new Uint256(proposalId)), List.of(new TypeReference<ProposalInfo>() {
        }));
        return executeRemoteCallSingleValueReturn(function, ProposalInfo.class);
    }

    public static class ProposalInfo extends DynamicStruct {
        public final Address proposalSMT;
        public final Uint8 status;
        public final ProposalConfig config;
        public final DynamicArray<DynamicArray<Uint256>> votingResults;

        public ProposalInfo(Address proposalSMT, Uint8 status, ProposalConfig config, @Parameterized(type = Uint256.class) DynamicArray<DynamicArray<Uint256>> votingResults) {
            super(proposalSMT, status, config, votingResults);
            this.proposalSMT = proposalSMT;
            this.status = status;
            this.config = config;
            this.votingResults = votingResults;
        }
    }


}

