package com.daniloarantes.appprodutos;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity{

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    Button btcad, btfecha;
    ProgressBar progressBar;
    ListView listaDeProdutos;
    List<MesaItens> produtosLista;
    EditText comanda;
    TextView tvvalor;
    double valor = 0;
    boolean fecha = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>CC - Commercial Controler</font>"));

        listaDeProdutos = findViewById(R.id.listaDeProdutos);

        progressBar = findViewById(R.id.progressBar);

        btcad = findViewById(R.id.BTCad);
        btfecha = findViewById(R.id.BTFecha);

        comanda = findViewById(R.id.Comanda);

        produtosLista = new ArrayList<>();

        tvvalor = findViewById(R.id.TVValor);

        btcad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrar();
            }
        });

        btfecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecharMesa();
            }
        });

        comanda.setOnKeyListener(new AdapterView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (comanda.getText().toString().isEmpty() || comanda.getText().toString().equals("0")){
                        produtosLista.clear();
                        valor = 0;
                        tvvalor.setText("Valor Total: R$ 0.0");
                    } else {
                        listaProdutosMesa(Integer.valueOf(comanda.getText().toString()));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private class ConexaoDados extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestCode;

        ConexaoDados(String url, HashMap<String, String> params, int requestCode) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequisicoesPHP requisitaPHP = new RequisicoesPHP();

            if (requestCode == CODE_POST_REQUEST) {
                return requisitaPHP.sendPostRequest(url, params);
            }

            if (requestCode == CODE_GET_REQUEST) {
                return requisitaPHP.sendGetRequest(url);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);


            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if(!fecha) {
                        atualizaListaProdutos(object.getJSONArray("produtos"));
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                        fecha = false;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void listaProdutosMesa(int comanda) {
        ConexaoDados request = new ConexaoDados(API.URL_LISTAR_PRODUTOS_MESA + comanda, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void cadastrar() {
        Intent intent = new Intent(getApplicationContext(), AddProdutos.class);
        startActivity(intent);
    }

    /*private void excluir(int codp, int comanda) {
        ConexaoDados request = new ConexaoDados(API.URL_EXCLUIR_PRODUTO + "codp=" + codp + "&comanda=" + comanda, null, CODE_GET_REQUEST);
        request.execute();
        listaProdutosMesa(comanda);
    }*/

    private void fecharMesa() {
        if(comanda.getText().toString().equals("0") || comanda.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Informe uma Comanda!", Toast.LENGTH_SHORT).show();
        } else {
            fecha = true;
            ConexaoDados request = new ConexaoDados(API.URL_FECHAR_MESA + comanda.getText().toString(), null, CODE_GET_REQUEST);
            request.execute();
            produtosLista.clear();
            valor = 0;
            tvvalor.setText("Valor Total: R$ 0.0");
        }
    }

    private void atualizaListaProdutos(JSONArray produtosArray) throws JSONException {
        produtosLista.clear();

        valor = 0;

        for (int i = 0; i < produtosArray.length(); i++) {
            JSONObject obj = produtosArray.getJSONObject(i);

            produtosLista.add(new MesaItens(
                    obj.getInt("Cod_Prod"),
                    obj.getString("Nome"),
                    obj.getString("Adic_Nome"),
                    obj.getDouble("Valor"),
                    obj.getInt("Qtde")
            ));

            for (int v = 0; v < obj.getInt("Qtde"); v++){
                valor = valor + obj.getDouble("Valor") + obj.getDouble("Adic_Valor");
            }

        }

        tvvalor.setText("Valor Total: R$ " + valor);

        ProdutoAdapter adapter = new ProdutoAdapter(produtosLista);
        listaDeProdutos.setAdapter(adapter);
    }

    class ProdutoAdapter extends ArrayAdapter<MesaItens> {
        List<MesaItens> produtoLista;

        public ProdutoAdapter(List<MesaItens> produtoLista) {
            super(MainActivity.this, R.layout.layout_lista_produtos, produtoLista);
            this.produtoLista = produtoLista;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_lista_produtos, null, true);

            final TextView Tvnome = listViewItem.findViewById(R.id.TVNome);
            final TextView Tvqtde = listViewItem.findViewById(R.id.TVQtde);
            final TextView Tvadic = listViewItem.findViewById(R.id.TVAdic);
            TextView btn_deleta = listViewItem.findViewById(R.id.TVDeleta);
            final MesaItens mitens = produtoLista.get(position);
            Tvnome.setText(mitens.getNome());
            Tvqtde.setText("Qtde: " + mitens.getQtde());
            Tvadic.setText(mitens.getAdicNome());


            /*btn_deleta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle(Html.fromHtml("<font color='#000000'>Excluir " + mitens.getNome() +"</font>"))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(Html.fromHtml("<font color='#000000'>Deseja realmente excluir ?</font>"))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    excluir(mitens.getCod_prod(), Integer.valueOf(mesas.getSelectedItem().toString()));
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            });*/
            return listViewItem;
        }
    }
}
