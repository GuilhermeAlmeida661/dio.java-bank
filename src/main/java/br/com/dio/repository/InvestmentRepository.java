package br.com.dio.repository;

import br.com.dio.exception.WalletNotFoundException;
import br.com.dio.exception.InvestmentNotFoundException;
import br.com.dio.exception.AccountWithInvestmentException;
import br.com.dio.model.AccountWallet;
import br.com.dio.model.InvestmentWallet;
import br.com.dio.model.Investment;
import static br.com.dio.repository.CommonsRepository.checkFundsForTransaction;

import java.util.ArrayList;
import java.util.List;


public class InvestmentRepository {

    private long nextId;
    private final List<Investment> investments = new ArrayList<>();
    private final List<InvestmentWallet> wallets = new ArrayList<>();

    //Criação dos investmentos na carteira
    public Investment create(final long tax, final long initialFunds){
        this.nextId++;
        var investment = new Investment(this.nextId, tax, initialFunds);
        investments.add(investment);
        return investment;
    }

    //Investimento - depósito inicial na carteira de investimentos
    public InvestmentWallet initInvestment(final AccountWallet account, final long id){
        //verifica se a carteira de investimentos não está vazia
        if(!wallets.isEmpty()){
            var accountsInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();
            //verifica se a conta está já tem investimento
            if(accountsInUse.contains(account)){
                throw new AccountWithInvestmentException("A conta: '"+ account + "' já tem Investimentos.");
            }
        }
        var investment = findById(id);
        checkFundsForTransaction(account, investment.initialFunds());
        var wallet = new InvestmentWallet(investment, account, investment.initialFunds());
        wallets.add(wallet);
        return wallet;
    }

    //Investimentos na carteira - depósito
    public InvestmentWallet deposit( final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "Investimentos");
        return wallet;
    }

    //Saque da carteira
    public InvestmentWallet withDraw(final String pix, final long funds){
        var wallet = findWalletByAccountPix(pix);
        checkFundsForTransaction(wallet, funds);
        wallet.getAccount()
        .addMoney(wallet.reduceMoney(funds), wallet.getService(), "Saque de Investimentos");
        if(wallet.getFunds() == 0){
            wallets.remove(wallet); // remove a carteira vazia
        }

        return wallet;
    }

    //Atualiza os valores da carteira de Investimento
    public void updateAmount(){
        wallets.forEach(w-> w.updateAmount(w.getInvestment().tax()));
    }
    
    //Encontra pelo id da carteira
    public Investment findById(final long id){
        return investments.stream().filter(a-> a.id() == id).findFirst()
                .orElseThrow(()-> new InvestmentNotFoundException("'O investimento: '" + id + "' Não foi encontrado.'"));
    }

    //Encontra pela chave pix a carteira
    public InvestmentWallet findWalletByAccountPix(final String pix){
        return wallets.stream().filter(w -> w.getAccount().getPix()
                .contains(pix))
                .findFirst()
                .orElseThrow(()-> new WalletNotFoundException("A carteira de Investmentos não foi encontrada."));
    }

    //Listagem das carteiras e investimentos
    public List<InvestmentWallet> listWallets(){
        return this.wallets;
    }

    public List<Investment> list(){
        return this.investments;
    }

}
