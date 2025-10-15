package br.com.dio.repository;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.List;

import br.com.dio.model.Wallet;
import br.com.dio.model.Money;
import br.com.dio.model.MoneyAudit;
import br.com.dio.exception.NoFundsEnoughException;
import static br.com.dio.model.BankService.ACCOUNT;

@NoArgsConstructor(access = PRIVATE)
public final class CommonsRepository {

    //Verificação do saldo da conta
    public static void checkFundsForTransaction(final Wallet source, final long amount){
        if(source.getFunds() < amount){
            throw new NoFundsEnoughException("Sua conta não posssui dinheiro o suficiente para realizar essa transação");
        }
    }

    //Depósito na conta
    public static List<Money> generateMoney(final UUID transactionId, final long funds, final String description){
        var history = new MoneyAudit(transactionId, ACCOUNT, description, OffsetDateTime.now());
        return Stream.generate(()-> new Money(history)).limit(funds).toList();

    }
}
