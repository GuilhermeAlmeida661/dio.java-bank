package br.com.dio.model;

import lombok.Getter;
import static br.com.dio.model.BankService.INVESTMENT;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;


@Getter
public class InvestmentWallet extends Wallet{

    private final Investment investment;
    private final AccountWallet account;

    public InvestmentWallet(final Investment investment, final AccountWallet account, final long amount){
        super(INVESTMENT);
        this.investment = investment;
        this.account = account;
        addMoney(account.reduceMoney(amount), getService(), "Investimento");
    }

    //Reajuste de juros rendidos na conta, simulação
    public void updateAmount(final long percent){
        var amount = getFunds() * percent / 100;
        var history = new MoneyAudit(UUID.randomUUID(), getService(), "Rendimentos", OffsetDateTime.now());
        var money = Stream.generate(() -> new Money(history)).limit(amount).toList();
        this.money.addAll(money);
    }

    @Override
    public String toString(){
        return super.toString() + "InvestmentWallet{" +
        "investment = " + investment +
        ", account = " + account +
        '}';
    }
}
