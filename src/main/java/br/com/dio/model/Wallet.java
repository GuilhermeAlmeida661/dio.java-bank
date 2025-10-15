package br.com.dio.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import lombok.Getter;

public abstract class Wallet {

    @Getter
    private final BankService service;

    //cada item dessa classe aqui representa um centavo na conta//
    protected final List<Money> money;

    public Wallet(final BankService service){
        this.service = service;
        this.money = new ArrayList<>();
    }

    //método utilitário de geração de dinheiro
    protected List<Money> generateMoney(final long amount, final String description){
        var history = new MoneyAudit(UUID.randomUUID(), service, description, OffsetDateTime.now());
        return Stream.generate(()-> new Money(history)).limit(amount).toList();
    }

    // getter do saldo da conta
    public long getFunds(){
        return money.size();
    }

    //depósito de dinheiro na conta, "money deposit"
    public void addMoney(final List<Money> money, final BankService service, final String description){
        var history = new MoneyAudit(UUID.randomUUID(), service, description, OffsetDateTime.now());
        money.forEach(m-> m.addHistory(history));
        this.money.addAll(money);
    }

    //"cash withdrawal", remoção de dinheiro da conta
    public List<Money> reduceMoney(final long amount){
        List<Money> toRemove = new ArrayList<>();
        for(int i = 0; i < amount; i++){
            toRemove.add(this.money.removeFirst());
        }
        return toRemove;
    }

    //Extrato da conta
    public List<MoneyAudit> getFinancialTransactions(){
        return money.stream().flatMap(m -> m.getHistory().stream()).toList();
    }

    @Override
    public String toString(){
        return "Wallet{" +
        "service = " + service +
        ", funds = R$ " + money.size()/100.0 +
        '}';
    }

}
