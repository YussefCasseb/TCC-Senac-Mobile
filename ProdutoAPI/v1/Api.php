<?php

    // Acessando a classe dboperation para acessar seus métodos
    require_once '../includes/DbOperation.php';

    // Função para validar parâmetros recebidos
    function isTheseParametersAvailable($params){
        // Assumindo que todos os parâmetros foram recebidos
        $available = true;
        $missingparams = "";

        foreach($params as $param){
            if(!isset($_POST[$param]) || strlen($_POST[$param])<=0){
                $available = false;
                $missingparams = $missingparams . ", " . $param;
            }
        }

        // Se faltarem parâmetros
        if(!$available){
            $response = array();
            $response['error'] = true;
            $response['message'] = 'Parameters ' . substr($missingparams, 1, strlen($missingparams)) . ' missing';

            // Retornando mensagem de erro
            echo json_encode($response);

            // Interrompendo a execução do código
            die();
        }
    }

    // Definindo array para resposta
    $response = array();

    // Como este código tem como base atuar como uma api, uma chamada de execução
    // é definida através da URL. Abaixo é feita uma verificação para disparar
    // a execução da chamada enviada.
    if(isset($_GET['apicall'])){

        switch($_GET['apicall']){

            // Operações para cadastro de produtos
            // chamada de api com 'createhero'
            // Os parâmetros recebidos são inseridos no banco de dados
            case 'cadastrarmesa':
                // Verificando se os parâmetros necessários foram recebidos
                isTheseParametersAvailable(array('mesa','comanda','valor','observ'));

                // Criando uma nova operação para comunicação com o BD
                $db = new DbOperation();

                // Cadastrando o produdo no banco de dados
                $result = $db->cadastrarMesa(
                        utf8_decode($_POST['mesa']),
						$_POST['comanda'],
                        $_POST['valor'],
                        utf8_decode($_POST['observ'])
                );


                // Se cadastrado com sucesso, retornar mensagem
                if($result){
                        // false porque não houve erro ao cadastrar
                        $response['error'] = false;

                        // Mensagem de cadastro com sucesso
                        $response['message'] = 'Mesa adicionada com sucesso!';
                }else{

                        // Se não cadastrar ocorreu algum erro
                        $response['error'] = true;

                        // mensagem de erro
                        $response['message'] = 'Erro ao adicionar, verifique!';
                }

            break;

            // Operação de leitura
            case 'listarprodutosmesa':
            if(isset($_GET['comanda'])){
                $db = new DbOperation();
                $response['error'] = false;
                $response['message'] = 'Listagem de produtos obtida.';
                $response['produtos'] = $db->listarProdutosMesa($_GET['comanda']);
            }
            break;

            case 'listarprodutos':
            if (isset($_GET['tipo'])) {
                $db = new DbOperation();
                $response['error'] = false;
                $response['message'] = 'Listagem de produtos obtida.';
                $response['produtos'] = $db->listarProdutos($_GET['tipo']);
            }
            break;


            // Operação para atualização de um registro
            case 'atualizarmesa':
                isTheseParametersAvailable(array('codp','comanda','coda','mesa','valor'));
                $db = new DbOperation();
                $result = $db->atualizarMesa(
                    $_POST['codp'],
                    $_POST['comanda'],
					$_POST['coda'],
                    utf8_decode($_POST['mesa']),
					$_POST['valor']
                );

                if($result){
                    $response['error'] = false;
                    $response['message'] = 'Produto Inserido!';
                }else{
                    $response['error'] = true;
                    $response['message'] = 'Erro ao Inserir Produto!';
                }
            break;

            // Operação para deleção de um registro
            case 'buscaradicionais':
                if (isset($_GET['prod'])) {
                $db = new DbOperation();
                $response['error'] = false;
                $response['message'] = 'Listagem de adicionais obtida.';
                $response['produtos'] = $db->buscarAdicionais($_GET['prod']);
				}
            break;
			
			// Operação para deleção de um registro
            case 'listaradicionais':
                if (isset($_GET['prod']) && isset($_GET['adic'])) {
                $db = new DbOperation();
                $response['error'] = false;
                $response['message'] = 'Listagem de adicionais obtida.';
                $response['produtos'] = $db->listarAdicionais($_GET['prod'], $_GET['adic']);
				}
            break;

                // Operação para deleção de um registro
                case 'fecharmesa':

                    // Esta operação recebe o id do registro a ser removido através do GET
                    // desta forma, o parâmetro não vem encapsulado e é enviado pela URL
                    if(isset($_GET['comanda'])){
                        $db = new DbOperation();
                        if($db->fecharMesa($_GET['comanda'])){
                            $response['error'] = false;
                            $response['message'] = 'Mesa fechada.';
                        }else{
                            $response['error'] = true;
                            $response['message'] = 'Erro ao fechar mesa.';
                        }
                        }else{
                            $response['error'] = true;
                            $response['message'] = 'Nada a ser fechado.';
                        }
                    break;
            }

    }else{
        // Esta verificação será executada se for enviada uma operação
        // que não faz parte da API
        $response['error'] = true;
        $response['message'] = 'Invalid API Call';
    }

    // Retorno da resposta através de json
    echo json_encode($response);
