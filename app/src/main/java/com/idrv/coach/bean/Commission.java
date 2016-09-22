package com.idrv.coach.bean;

/**
 * time:2016/3/11
 * description:佣金
 *
 * @author sunjianfei
 */
public class Commission {
    private String commission;
    private String balance;
    private int credit;

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
