package com.daniloarantes.appprodutos;

public class API {

    // Endereço para o servidor
    // No android studio é utilizado o IP 10.0.2.2 que faz referência para localhost
    private static final String ROOT_URL = "http://10.0.2.2/ProdutoAPI/v1/Api.php?apicall=";

    // Constantes utilizadas para a comunicação com a API
    public static final String URL_CADASTRAR_MESA = ROOT_URL + "cadastrarmesa";
    public static final String URL_LISTAR_PRODUTOS_MESA = ROOT_URL  + "listarprodutosmesa&comanda=";
    public static final String URL_LISTAR_PRODUTOS = ROOT_URL  + "listarprodutos&tipo=";
    public static final String URL_ATUALIZAR_MESA = ROOT_URL + "atualizarmesa";
    public static final String URL_LISTAR_ADICIONAIS = ROOT_URL  + "buscaradicionais&prod=";
    public static final String URL_FECHAR_MESA = ROOT_URL  + "fecharmesa&comanda=";
    public static final String URL_BUSCA_ADICS = ROOT_URL  + "listaradicionais&prod=";

}
