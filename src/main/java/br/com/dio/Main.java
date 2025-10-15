package br.com.dio;

import java.util.Arrays;
import java.util.Scanner;

import br.com.dio.exception.AccountNotFoundException;
import br.com.dio.exception.NoFundsEnoughException;

import br.com.dio.model.AccountWallet;
import br.com.dio.repository.AccountRepository;
import br.com.dio.repository.InvestmentRepository;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class Main {

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static InvestmentRepository investmentRepository = new InvestmentRepository();
    
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args){
        System.out.println("Olá seja bem vindo ao DIO Bank!\n");
        while(true){
            System.out.println("Selecione a operação desejada");
            System.out.println("1 - Criar uma Conta");
            System.out.println("2 - Criar um Investimento");
            System.out.println("3 - Fazer um Investimento");
            System.out.println("4 - Realizar um Depósito na Conta Corrente");
            System.out.println("5 - Realizar um Saque da Conta Corrente");
            System.out.println("6 - Realizar uma Tranferência entre Contas");
            System.out.println("7 - Investir");
            System.out.println("8 - Realizar um Saque do Investimento");
            System.out.println("9 - Listar Contas");
            System.out.println("10 - Listar Investimentos");
            System.out.println("11 - Listar Carteiras de Investimento");
            System.out.println("12 - Atualizar Investimentos");
            System.out.println("13 - Histórico de Conta");
            System.out.println("14 - Sair");
            var option = scanner.nextInt();

            switch(option){
                case 1: createAccount(); break;
                case 2: createInvestment(); break;
                case 3: createWalletInvestment(); break;
                case 4: deposit(); break;
                case 5: withdraw(); break;
                case 6: transferToAccount(); break;
                case 7: incInvestment(); break;
                case 8: rescueInvestment(); break;
                case 9: accountRepository.list().forEach(System.out::println); break;
                case 10: investmentRepository.list().forEach(System.out::println); break;
                case 11: investmentRepository.listWallets().forEach(System.out::println); break;
                case 12: {
                    investmentRepository.updateAmount();
                    System.out.println("Investimentos Ajustados!");
                    break;
                }
                case 13: checkHistory(); break;
                case 14: System.exit(0); break;
                default: System.err.println("Opção Inválida"); break; 
            }
        }
    }

    //cria a conta "corrente"
    private static void createAccount(){
        System.out.println("Informe as chaves pix(separadas por ';'):");
        var pix = Arrays.stream(scanner.next().split(";")).toList();
        System.out.println("Informe o valor inicial de depósito:");
        var amount = scanner.nextLong();
        var wallet = accountRepository.create(pix, amount);
        System.out.println("Conta Criada: " + wallet);
    }

    //cria a conta de investimentos
    private static void createInvestment(){
        System.out.println("Informe a taxa do investimento:");
        var tax = scanner.nextInt();
        System.out.println("Informe o valor inicial de depósito:");
        var initialFunds = scanner.nextLong();
        var investment = investmentRepository.create(tax, initialFunds);
        System.out.println("Investimento Criado: " + investment);
    }

    //Depósito na conta 
    private static void deposit(){
        System.out.println("Informe a chave pix da conta para depósito:");
        var pix = scanner.next();
        System.out.println("Informe o valor que deseja depositar:");
        var amount = scanner.nextLong();
        try{
            accountRepository.deposit(pix, amount);
        }catch(AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }

    //Saque da conta 
    private static void withdraw(){
        System.out.println("Informe a chave pix da conta para realizar um Saque:");
        var pix = scanner.next();
        System.out.println("Informe o valor que deseja Sacar:");
        var amount = scanner.nextLong();
        try{
            accountRepository.withDraw(pix, amount);
        }catch(NoFundsEnoughException | AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }

    //Transferência entre contas
    private static void transferToAccount(){
        System.out.println("Informe a chave pix da conta de origem:");
        var source = scanner.next();
        System.out.println("Informe a chave pix da conta de destino:");
        var target = scanner.next();
        System.out.println("Informe o valor que deseja depositar:");
        var amount = scanner.nextLong();
        try{
            accountRepository.transferMoney(source, target, amount);
        }catch(NoFundsEnoughException | AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }

    //Criando uma carteira de investimento para a conta
    private static void createWalletInvestment(){
        System.out.println( "Informe a chave pix da conta para criar uma carteira de investimento:");
        var pix = scanner.next();
        var account = accountRepository.findByPix(pix);
        System.out.println("Informe o identificador do investimento:");
        var investmentId = scanner.nextLong();
        var investmentWallet = investmentRepository.initInvestment(account, investmentId);
        System.out.println("Conta de Investimento Criada: " + investmentWallet);
    }

    //Realizando um investimento
    private static void incInvestment(){
        System.out.println( "Informe a chave pix da conta para o investimento:");
        var pix = scanner.next();
        System.out.println("Informe o valor que deseja investir:");
        var amount = scanner.nextLong();
        try{
            investmentRepository.deposit(pix, amount);
        }catch(NoFundsEnoughException | AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }

    //Saque da Conta de Investimento
    private static void rescueInvestment(){
        System.out.println("Informe a chave pix da conta para resgatar um Investimento:");
        var pix = scanner.next();
        System.out.println("Informe o valor que deseja Resgatar:");
        var amount = scanner.nextLong();
        try{
            investmentRepository.withDraw(pix, amount);
        }catch(NoFundsEnoughException | AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }

    //Verifica o Histórico da Conta
    private static void checkHistory(){
        System.out.println("Informe a chave pix da conta para verificar o Extrato:");
        var pix = scanner.next();
        AccountWallet wallet;
        try{
            var sortedHistory = accountRepository.getHistory(pix);
            sortedHistory.forEach((k, v) -> {
                System.out.println(k.format(ISO_DATE_TIME));
                System.out.println(v.getFirst().transactionId());
                System.out.println(v.getFirst().description());
                System.out.println(v.size());
            });
        }catch(AccountNotFoundException ex){
            System.err.println(ex.getMessage());
        }
    }
}
