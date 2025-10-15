package br.com.dio.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
public class Money {
    
    private final List<MoneyAudit> history = new ArrayList<>();

    //toda vez que for criar o dim dim ele atualiza o histórico
    public Money(final MoneyAudit history){
        this.history.add(history);
    }

    // adiciona no histórico
    public void addHistory(final MoneyAudit history){
        this.history.add(history);
    }
}
