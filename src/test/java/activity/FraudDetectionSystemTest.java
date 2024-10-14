package activity;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import activity.FraudDetectionSystem;
import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


public class FraudDetectionSystemTest {


    // High Transaction Amount, `Verfication Required` should be True`
    @Test
    public void tc1(){
        double transactionAmount = 500;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();
        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 0;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
        assertEquals(expectedRiskScore,result.riskScore);
    }

    @Test
    public void tc2(){
        double transactionAmount = 10000.01;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();
        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = true;
        boolean expectedFraudFlag = true;
        int expectedRiskScore = 50;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
        assertEquals(expectedRiskScore,result.riskScore);
    }

    //Mais de 10 transacoes no ultima hora
    @Test
    public void tc3(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 1; i <= 15; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 2);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "Brazil";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 30;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    // Exatamente 10 transacoes na ultima hora
    @Test
    public void tc4(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 1; i <= 10; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 2);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "Brazil";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 0;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    //Previous transaction 20 minutes earlier in another location
    @Test
    public void tc5(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        LocalDateTime previousTransactionTime = transactionTime.minusMinutes(10);
        double previousTransactionAmount = 500.00;
        String previousTransactionLocation = "France";

        FraudDetectionSystem.Transaction previousTransaction = new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

        transactionList.add(previousTransaction);

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = true;
        boolean expectedFraudFlag = true;
        int expectedRiskScore = 20;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    //Previous transaction 90 minutes earlier in another location
    @Test
    public void tc6(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        LocalDateTime previousTransactionTime = transactionTime.minusMinutes(90);
        double previousTransactionAmount = 500.00;
        String previousTransactionLocation = "France";

        FraudDetectionSystem.Transaction previousTransaction = new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

        transactionList.add(previousTransaction);

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 0;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    // Blacklisted Country
    @Test
    public void tc7(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

//        LocalDateTime previousTransactionTime = transactionTime.minusMinutes(90);
//        double previousTransactionAmount = 500.00;
//        String previousTransactionLocation = "France";
//
//        FraudDetectionSystem.Transaction previousTransaction = new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);
//
//        transactionList.add(previousTransaction);

        List<String> blacklisted = new ArrayList<String>();
        blacklisted.add("Brazil");
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 100;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }


    // High Transaction Amount and Excessive Transactions in Short Time Frame:
    @Test
    public void tc8(){
        double transactionAmount = 10000.01;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 1; i <= 15; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 2);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "Brazil";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = true;
        boolean expectedFraudFlag = true;
        int expectedRiskScore = 80;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }


    // High Transaction Amount and Excessive Transactions in Short Time Frame and Location Change within Short Time Frame
    @Test
    public void tc9(){
        double transactionAmount = 10000.01;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 1; i <= 12; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 2);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "France";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = true;
        boolean expectedFraudFlag = true;
        int expectedRiskScore = 100;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    // Cover all lines at once
    @Test
    public void tc10(){
        double transactionAmount = 10000.01;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 1; i <= 12; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 2);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "France";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        blacklisted.add("Brazil");
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = true;
        boolean expectedFraudFlag = true;
        int expectedRiskScore = 100;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    //Transaction Amount is exactly 10000
    @Test
    public void tc11(){
        double transactionAmount = 10000;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();
        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 0;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
        assertEquals(expectedRiskScore,result.riskScore);
    }

    //Previous transaction exactly 30 minutes earlier in another location
    @Test
    public void tc12(){
        double transactionAmount = 800.00;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        LocalDateTime previousTransactionTime = transactionTime.minusMinutes(30);
        double previousTransactionAmount = 500.00;
        String previousTransactionLocation = "France";

        FraudDetectionSystem.Transaction previousTransaction = new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

        transactionList.add(previousTransaction);

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 0;
        boolean expectedBlockedStatus = false;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }

    //10 transactions, with the first one being exactly 60 minutes earlier
    @Test
    public void tc13(){
        double transactionAmount = 500;
        LocalDateTime transactionTime = LocalDateTime.parse("2024-04-20T10:00:00");
        String transactionLocation = "Brazil";
        List<Transaction> transactionList= new ArrayList<Transaction>();

        for (int i = 0; i <= 10; i++) {
            LocalDateTime previousTransactionTime = transactionTime.minusMinutes(i * 6);
            double previousTransactionAmount = 500.00;
            String previousTransactionLocation = "Brazil";

            FraudDetectionSystem.Transaction previousTransaction =
                    new FraudDetectionSystem.Transaction(previousTransactionAmount, previousTransactionTime, previousTransactionLocation);

            transactionList.add(previousTransaction);
        }

        List<String> blacklisted = new ArrayList<String>();
        FraudDetectionSystem system = new FraudDetectionSystem();
        FraudDetectionSystem.Transaction transaction = new FraudDetectionSystem.Transaction(transactionAmount,transactionTime,transactionLocation);
        FraudCheckResult result = system.checkForFraud(transaction,transactionList,blacklisted);

        boolean expectedVerification = false;
        boolean expectedFraudFlag = false;
        int expectedRiskScore = 30;
        boolean expectedBlockedStatus = true;
        assertEquals(expectedRiskScore,result.riskScore);
        assertEquals(expectedVerification,result.verificationRequired);
        assertEquals(expectedFraudFlag,result.isFraudulent);
        assertEquals(expectedBlockedStatus,result.isBlocked);
    }


}



