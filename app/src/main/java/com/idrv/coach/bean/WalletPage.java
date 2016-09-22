package com.idrv.coach.bean;

import java.util.List;

/**
 * time:2016/5/20
 * description:
 *
 * @author sunjianfei
 */
public class WalletPage {
    float balance;
    int credit;
    List<PurseDetails> InAccounts;

    public String getBalance() {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        return df.format(balance);
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public List<PurseDetails> getInAccounts() {
        return InAccounts;
    }

    public void setInAccounts(List<PurseDetails> inAccounts) {
        InAccounts = inAccounts;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
}
