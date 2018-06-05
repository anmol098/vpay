package com.aapreneur.vpay.Resources;

/**
 * Created by anmol on 14-03-2018.
 */

public class MyDataModel {

    private String date;
    private String time;
    private String UTR;
    private String transactionId;
    private String amount;
    private String orderId;
    private String paytmId;
    private String mode;
    private String remarks;
    private String fees;
    private String payback;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date=date;
    }
    public String getTime(){
        return time;
    }
    public void setTime(String time){
        this.time=time;
    }
    public String getUTR(){
        return UTR;
    }
    public void setUTR(String UTR)
    {
        this.UTR=UTR;
    }
    public String getTransactionId(){
        return transactionId;
    }
    public void setTransactionId(String transactionId){
        this.transactionId=transactionId;
    }
    public String getAmount(){
        return amount;
    }
    public void setAmount(String amount){
        this.amount=amount;
    }
    public String getOrderId(){
        return orderId;
    }
    public void setOrderId(String orderId){
        this.orderId=orderId;
    }

    public String getPaytmId(){
        return paytmId;
    }
    public void setPaytmId(String paytmId){
        this.paytmId=paytmId;
    }

    public String getMode(){
        return mode;
    }
    public void setMode(String mode){
        this.mode=mode;
    }

    public String getRemarks(){
        return remarks;
    }
    public void setRemarks(String remarks){
        this.remarks=remarks;
    }

    public String getFees(){
        return fees;
    }
    public void setFees(String fees){
        this.fees=fees;
    }

    public String getPayback(){
        return payback;
    }
    public void setPayback(String payback){
        this.payback=payback;
    }

}
