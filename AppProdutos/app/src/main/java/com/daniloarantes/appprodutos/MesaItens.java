package com.daniloarantes.appprodutos;

public class MesaItens {
    private int cod_prod;
    private int qtde;
    private String nome, adicnome;
    private double valor;

    public MesaItens(int cod_prod, String nome, String adicnome, double valor, int qtde){
        this.cod_prod = cod_prod;
        this.nome = nome;
        this.adicnome = adicnome;
        this.valor = valor;
        this.qtde = qtde;
    }


    public int getCod_prod() {
        return cod_prod;
    }

    public int getQtde() {
        return qtde;
    }

    public String getNome() {
        return nome;
    }

    public String getAdicNome() {
        return adicnome;
    }

    public double getValor() {
        return valor;
    }
}
