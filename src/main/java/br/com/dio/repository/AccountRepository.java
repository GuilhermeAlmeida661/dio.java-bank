package br.com.dio.repository;

import br.com.dio.exception.PixInUseException;
import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.MoneyAudit;
import java.time.OffsetDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

public class AccountRepository {

    private List<AccountWallet> accounts = new ArrayList<>();

    //cria a conta "corrente"
    public AccountWallet create(final List<String> pix, final long initialFunds){
        //verifica se a conta não está vazia
        if(!accounts.isEmpty()){
            var pixInUse = accounts.stream().flatMap(a -> a.getPix().stream()).toList();
            for(var p : pix){ //verifica se a chave está já em uso
                if(pixInUse.contains(p)){
                    throw new PixInUseException("O pix: '"+ p + "' já está em Uso.");
                }
            }
        }
        var newAccount = new AccountWallet(initialFunds, pix);
        accounts.add(newAccount);
        return newAccount;
    }

    //depósito na conta corrente
    public void deposit(final String pix, final long fundsAmount){
        var target = findByPix(pix);
        target.addMoney(fundsAmount, "Depósito na conta corrente");
    }

    //Saque na conta corrente
    public long withDraw(final String pix, final long amount){
        var source = findByPix(pix);
        checkFundsForTransaction(source, amount);
        source.reduceMoney(amount);
        return amount;
    }

    //Transferência via pix de uma conta para outra
    public void transferMoney(final String sourcePix, final String targetPix, final long amount){
        var source = findByPix(sourcePix);
        checkFundsForTransaction(source, amount);
        var target = findByPix(targetPix);
        var message = "Pix enviado de: '" + sourcePix + "' Para: '" + targetPix + "'";
        target.addMoney(source.reduceMoney(amount), source.getService(), message);
    }

    //busca a conta via chave pix
    public AccountWallet findByPix(final String pix){
        return accounts.stream()
        .filter(a -> a.getPix().contains(pix))
        .findFirst()
        .orElseThrow(()-> new AccountNotFoundException("A conta com a chave pix '" + pix + "' não existe ou foi encerrada"));
    }
    
    //listagem de contas-corrente
    public List<AccountWallet> list(){
        return this.accounts;
    }

    public Map<OffsetDateTime, List<MoneyAudit>> getHistory(final String pix){
        var wallet = findByPix(pix);
        var audit = wallet.getFinancialTransactions();
        return audit.stream().collect(Collectors.groupingBy(t -> t.createdAt().truncatedTo(SECONDS)));
    }

}
