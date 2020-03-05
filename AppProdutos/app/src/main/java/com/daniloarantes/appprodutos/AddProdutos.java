package com.daniloarantes.appprodutos;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

public class AddProdutos extends AppCompatActivity implements DialogAdic.DialogListener{

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    Button btcad;
    ListView listaDeProdutos;
    List<Produto> produtosLista;
    ProgressBar progressBar;
    EditText comanda;
    Spinner mesas, tipo;
    TextView tvvalor;
    EditText observ;
    double valor = 0;
    boolean abrem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produtos);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>CC - Commercial Controler</font>"));

        btcad = findViewById(R.id.BTCad);

        listaDeProdutos = findViewById(R.id.listaDeProdutos);

        produtosLista = new ArrayList<>();

        progressBar = findViewById(R.id.progressBar);

        comanda = findViewById(R.id.Comanda);

        mesas = findViewById(R.id.Mesas);

        tipo = findViewById(R.id.Tipo);

        tvvalor = findViewById(R.id.TVValor);

        observ = findViewById(R.id.Observ);

        String[] locali = getResources().getStringArray(R.array.mesas);
        String[] tipos = getResources().getStringArray(R.array.tipo);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locali);
        mesas.setAdapter(adapter);

        mesas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) mesas.getSelectedView()).setTextColor(Color.WHITE);
                ((TextView) mesas.getSelectedView()).setTextSize(20);
            }
        });

        ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tipos);
        tipo.setAdapter(adapter2);

        tipo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) tipo.getSelectedView()).setTextColor(Color.WHITE);
                ((TextView) tipo.getSelectedView()).setTextSize(20);
            }
        });

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!tipo.getSelectedItem().equals("Tipo de Produto")) {
                    if (tipo.getSelectedItem().equals("Porções")) {
                        listaProdutos("Porcoes");
                    } else {
                        listaProdutos(tipo.getSelectedItem().toString());
                    }
                } else {
                    produtosLista.clear();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        btcad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirMesa();
            }
        });

        comanda.setOnKeyListener(new AdapterView.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (comanda.getText().toString().isEmpty() || comanda.getText().toString().equals("0")){
                        Toast.makeText(getApplicationContext(), "Informe uma Comanda!", Toast.LENGTH_SHORT).show();
                    } else {
                        produtosLista.clear();
                        valor = 0;
                        tvvalor.setText("Valor Total: R$ 0.0");
                        tipo.setVisibility(View.INVISIBLE);
                        abrem = false;
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
                    if(abrem) {
                        atualizaListaProdutos(object.getJSONArray("produtos"));
                        abrem = false;
                    } else {
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void listaProdutos(String tipo) {
        abrem = true;
        AddProdutos.ConexaoDados request = new AddProdutos.ConexaoDados(API.URL_LISTAR_PRODUTOS + tipo, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void addProdutos(int codp, int coda, double val) {
        if(comanda.getText().toString().equals("0") || comanda.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Informe uma Comanda!", Toast.LENGTH_SHORT).show();
        } else {
            valor = valor + val;
            tvvalor.setText("Valor Total: R$ " + String.valueOf(valor));
            HashMap<String, String> params = new HashMap<>();
            params.put("codp", String.valueOf(codp));
            params.put("comanda", comanda.getText().toString());
            params.put("coda", String.valueOf(coda));
            params.put("mesa", mesas.getSelectedItem().toString());
            params.put("valor", String.valueOf(val));
            AddProdutos.ConexaoDados request = new AddProdutos.ConexaoDados(API.URL_ATUALIZAR_MESA, params, CODE_POST_REQUEST);
            request.execute();
        }
    }

    @Override
    public void prodAdics(int codp, int coda, double totalv) {
        addProdutos(codp, coda, totalv);
    }

    private void abrirMesa() {
        if(comanda.getText().toString().equals("0") || comanda.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Informe uma Comanda", Toast.LENGTH_SHORT).show();
        } else {
            tipo.setVisibility(View.VISIBLE);
            HashMap<String, String> params = new HashMap<>();
            params.put("mesa", mesas.getSelectedItem().toString());
            params.put("comanda", comanda.getText().toString());
            params.put("valor", String.valueOf(valor));
            params.put("observ", observ.getText().toString().trim());
            AddProdutos.ConexaoDados request = new AddProdutos.ConexaoDados(API.URL_CADASTRAR_MESA, params, CODE_POST_REQUEST);
            request.execute();
        }
    }

    private void atualizaListaProdutos(JSONArray produtosArray) throws JSONException {
        produtosLista.clear();

        for (int i = 0; i < produtosArray.length(); i++) {
            JSONObject obj = produtosArray.getJSONObject(i);

            produtosLista.add(new Produto(
                    obj.getInt("Cod_Prod"),
                    obj.getString("Nome"),
                    obj.getDouble("Valor"),
                    obj.getInt("Adicionais")
            ));
        }

        AddProdutos.ProdutoAdapter adapter = new AddProdutos.ProdutoAdapter(produtosLista);
        listaDeProdutos.setAdapter(adapter);
    }

    class ProdutoAdapter extends ArrayAdapter<Produto> {
        List<Produto> produtoLista;

        public ProdutoAdapter(List<Produto> produtoLista) {
            super(AddProdutos.this, R.layout.layout_lista_produtos_add, produtoLista);
            this.produtoLista = produtoLista;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_lista_produtos_add, null, true);

            final TextView Tvnome = listViewItem.findViewById(R.id.TVNome);
            TextView btadd = listViewItem.findViewById(R.id.TVAdd);
            final Produto produto = produtoLista.get(position);
            Tvnome.setText(produto.getNome());

            btadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (produto.getAdic() > 0){
                        DialogAdic Dialog = new DialogAdic();
                        DialogAdic.prod = produto.getNome();
                        Dialog.show(getSupportFragmentManager(), "Adicionais");
                    } else {
                        addProdutos(produto.getCod_prod(), 1, produto.getValor());
                    }
                }
            });

            return listViewItem;
        }
    }
}
