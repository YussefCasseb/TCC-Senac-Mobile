package com.daniloarantes.appprodutos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequisicoesPHP {

    // Método para enviar solicitações para a API
    // recebe dois argumentos: primeiro o tipo de operação a ser disparada na API
    // segundo os parâmetros que deverão ser enviados
    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        // Criando uma URL
        URL url;

        // Criando um StringBuilder que atuará como buffer para armazenar as mensagens do servidor
        StringBuilder sb = new StringBuilder();
        try {
            // Inicializando a URL
            url = new URL(requestURL);

            // Criando uma conexão do tipo http
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Configuração das propriedades da conexão
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Criando um canal para saída de dados
            OutputStream os = conn.getOutputStream();

            // Buffer para ler e armazenar as respostas do servidor
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                // Lendo as repsostas do servidor
                while ((response = br.readLine()) != null) {
                    sb.append(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    // Método para envio de requisições ao servidor utilizando GET
    public String sendGetRequest(String requestURL) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s + "\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    // Método para envio de requisições ao servidor utilizando POST
    // neste caso, os parâmetros são encapsulados na requisição
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
