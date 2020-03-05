package com.daniloarantes.appprodutos;

public class Produto {
    private int cod_prod, adics;
    private String nome;
    private double valor;

    public Produto(int cod_prod, String nome, double valor, int adics){
        this.cod_prod = cod_prod;
        this.nome = nome;
        this.valor = valor;
        this.adics = adics;
    }


    public int getCod_prod() { return cod_prod; }

    public String getNome() { return nome; }

    public double getValor() { return valor; }

    public int getAdic() { return adics; }
}
