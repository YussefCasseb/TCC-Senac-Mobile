<?php

class DbOperation {
    // Variável para conexão com o banco
    private $con;

    // Construtor da classe
    function __construct() {
        // Inclui o arquivo DbConnect.php para utilizar suas funções
        require_once dirname(__FILE__) . '/DbConnect.php';

        // Criando uma instãncia de DbConnect para se conectar com o BD
        $db = new DbConnect();

        // Inicializa uma nova conexão chamando o método connect
        $this->con = $db->connect();
    }

    // Método para inserir valores no banco de dados
    function cadastrarMesa($mesa, $comanda, $valort, $observ){
        $stmt = $this->con->prepare("CALL sp_pedido (?, ?, ?, ?)");
        $stmt->bind_param("sids", $mesa, $comanda, $valort, $observ);
        if($stmt->execute())
            return true;
        return false;
    }

    // Método para leitura de valores do BD
    function listarProdutosMesa($comanda){
        $stmt = $this->con->prepare("CALL sp_pegar_itens_adic (?)");
        $stmt->bind_param("i", $comanda);
        $stmt->execute();
        $stmt->bind_result($qtde, $codp, $nome, $adicnome, $valor, $adicvalor);

        $produtos = array();

        while($stmt->fetch()){
            $produto  = array();
            $produto['Qtde'] = $qtde;
            $produto['Cod_Prod'] = $codp;
            $produto['Nome'] = $nome;
            $produto['Adic_Nome'] = $adicnome;
            $produto['Valor'] = $valor;
            $produto['Adic_Valor'] = $adicvalor;

            array_push($produtos, $produto);
        }
        return $produtos;
	
    }

    // Método para leitura de valores do BD
    function listarProdutos($cat){
        $stmt = $this->con->prepare("SELECT * FROM vw_produtos WHERE Tipo = '$cat'");
        $stmt->execute();
        $stmt->bind_result($codp, $nome, $tipo, $valor, $adicionais);

        $produtos = array();

        while($stmt->fetch()){
            $produto  = array();
            $produto['Cod_Prod'] = $codp;
            $produto['Nome'] = $nome;
            $produto['Tipo'] = $tipo;
            $produto['Valor'] = $valor;
            $produto['Adicionais'] = $adicionais;

            array_push($produtos, $produto);
        }

        return $produtos;
    }

    // Método para atualização de valores no BD
    function atualizarMesa($codp, $comanda, $coda, $mesa, $valor){
        $stmt = $this->con->prepare("CALL sp_itens_app (?,?,?,?,?)");
        $stmt->bind_param("iiisd", $codp, $comanda, $coda, $mesa, $valor);
        if($stmt->execute())
            return true;
        return false;
    }


    // Método para leitura de valores do BD
    function buscarAdicionais($prod){
        $stmt = $this->con->prepare("SELECT Adic_Nome FROM vw_adicionais WHERE Produto = '$prod'");
        $stmt->execute();
        $stmt->bind_result($adic);

        $produtos = array();

        while($stmt->fetch()){
            $produto  = array();
            $produto['Adic'] = $adic;

            array_push($produtos, $produto);
        }

        return $produtos;
    }
	
	// Método para leitura de valores do BD
    function listarAdicionais($nprod, $nadic){
        $stmt = $this->con->prepare("SELECT prods.Cod_Prod, adics.Cod_Adic, prods.Nome, prods.Valor, adics.Adic_Valor  FROM vw_produtos prods INNER JOIN vw_adicionais adics ON prods.Nome = adics.Produto WHERE prods.Nome = '$nprod' AND adics.Adic_Nome = '$nadic'");
        $stmt->execute();
        $stmt->bind_result($codp, $coda, $nome, $valor, $avalor);

        $produtos = array();

        while($stmt->fetch()){
            $produto  = array();
            $produto['Cod_Prod'] = $codp;
            $produto['Cod_Adic'] = $coda;
            $produto['Nome'] = $nome;
            $produto['Valor'] = $valor;
            $produto['Adic_Valor'] = $avalor;

            array_push($produtos, $produto);
        }

        return $produtos;
    }

    // Método para atualizar valores do BD
    function fecharMesa($mesa){
        $stmt = $this->con->prepare("CALL sp_fechar (?)");
        $stmt->bind_param("i", $mesa);
        if($stmt->execute())
            return true;

        return false;
    }
}
