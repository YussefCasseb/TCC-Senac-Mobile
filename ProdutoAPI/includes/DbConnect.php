<?php 
    
    /*
    * Created by Belal Khan
    * website: www.simplifiedcoding.net 
    */
    
    // Classe DbConnect responsavel pela conexão com o banco
    class DbConnect {

        // Variável para armazenar link de conexão com BD
        private $con;
 
        // Construtor da classe
        function __construct() {
 
        }
 
        // Método para conexão com o BD
        function connect() {
            // Incluindo arquivo constants.php para acessar as constantes de conexão
            include_once dirname(__FILE__) . '/Constants.php';

            // Conectando com MySQL
            $this->con = new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);

            // Verificando erros na conexão
            if (mysqli_connect_errno()) {
                echo "Failed to connect to MySQL: " . mysqli_connect_error();
            }

            // Retornando a conexão 
            return $this->con;
            }
     
    }
