package com.distributedLab.rarime.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
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
public class Registration extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_CERTIFICATESSMT = "certificatesSmt";

    public static final String FUNC_CHANGEICAOMASTERTREEROOT = "changeICAOMasterTreeRoot";

    public static final String FUNC_GETCERTIFICATEINFO = "getCertificateInfo";

    public static final String FUNC_GETPASSPORTINFO = "getPassportInfo";

    public static final String FUNC_ICAOMASTERTREEMERKLEROOT = "icaoMasterTreeMerkleRoot";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PASSPORTDISPATCHERS = "passportDispatchers";

    public static final String FUNC_REGISTERCERTIFICATE = "registerCertificate";

    public static final String FUNC_REGISTRATIONSMT = "registrationSmt";

    public static final String FUNC_REVOKECERTIFICATE = "revokeCertificate";

    public static final String FUNC_SIGNER = "signer";

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<String> certificatesSmt() {
        final Function function = new Function(FUNC_CERTIFICATESSMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
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

    public RemoteFunctionCall<CertificateInfo> getCertificateInfo(byte[] certificateKey_) {
        final Function function = new Function(FUNC_GETCERTIFICATEINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<CertificateInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, CertificateInfo.class);
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

    public RemoteFunctionCall<byte[]> icaoMasterTreeMerkleRoot() {
        final Function function = new Function(FUNC_ICAOMASTERTREEMERKLEROOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> passportDispatchers(byte[] param0) {
        final Function function = new Function(FUNC_PASSPORTDISPATCHERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerCertificate(List<byte[]> icaoMerkleProof_, byte[] icaoMemberKey_, byte[] icaoMemberSignature_, byte[] x509SignedAttributes_, BigInteger x509KeyOffset_, BigInteger x509ExpirationOffset_) {
        final Function function = new Function(
                FUNC_REGISTERCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(icaoMerkleProof_, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicBytes(icaoMemberKey_), 
                new org.web3j.abi.datatypes.DynamicBytes(icaoMemberSignature_), 
                new org.web3j.abi.datatypes.DynamicBytes(x509SignedAttributes_), 
                new org.web3j.abi.datatypes.generated.Uint256(x509KeyOffset_), 
                new org.web3j.abi.datatypes.generated.Uint256(x509ExpirationOffset_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> registrationSmt() {
        final Function function = new Function(FUNC_REGISTRATIONSMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeCertificate(byte[] certificateKey_) {
        final Function function = new Function(
                FUNC_REVOKECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> signer() {
        final Function function = new Function(FUNC_SIGNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Registration load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Registration load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, transactionManager, contractGasProvider);
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
}
