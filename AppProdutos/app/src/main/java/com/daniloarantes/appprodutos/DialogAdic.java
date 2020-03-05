package com.daniloarantes.appprodutos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class DialogAdic extends AppCompatDialogFragment {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private DialogListener listener;
    public static String prod;

    int codprod, codadic;
    double totalv;
    boolean add = false;

    List itens;
    Spinner adics;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        adicionais(prod);

        adics = view.findViewById(R.id.Adicionais);

        itens = new ArrayList();

        builder.setView(view)
                .setTitle("Adicionais para " + prod)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        add = true;
                        prodAdics(prod, adics.getSelectedItem().toString());
                    }
                });

        return builder.create();
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

            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    if (add) {
                        prodAdics(object.getJSONArray("produtos"));
                        add = false;
                    } else {
                        spinnerAdics(object.getJSONArray("produtos"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void adicionais(String prod) {
        ConexaoDados request = new ConexaoDados(API.URL_LISTAR_ADICIONAIS + prod, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void prodAdics(String prod, String adic) {
        ConexaoDados request = new ConexaoDados(API.URL_BUSCA_ADICS + prod + "&adic=" + adic, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void spinnerAdics(JSONArray produtosArray) throws JSONException {
        itens.clear();

        for (int i = 0; i < produtosArray.length(); i++) {
            JSONObject obj = produtosArray.getJSONObject(i);

            itens.add(obj.getString("Adic"));

        }

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, itens);
        adics.setAdapter(adapter);

        adics.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ((TextView) adics.getSelectedView()).setTextColor(Color.BLACK);
                ((TextView) adics.getSelectedView()).setTextSize(20);
            }
        });
    }

    private void prodAdics(JSONArray produtosArray) throws JSONException {

        for (int i = 0; i < produtosArray.length(); i++) {
            JSONObject obj = produtosArray.getJSONObject(i);

            codprod = obj.getInt("Cod_Prod");
            codadic = obj.getInt("Cod_Adic");
            totalv = obj.getDouble("Valor") + obj.getDouble("Adic_Valor");

        }
        listener.prodAdics(codprod, codadic, totalv);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    public interface DialogListener {
        void prodAdics(int codp, int coda, double totalv);
    }
}
