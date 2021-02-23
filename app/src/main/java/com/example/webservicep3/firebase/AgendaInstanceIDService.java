package com.example.webservicep3.firebase;

import android.util.Log;

import com.example.webservicep3.retrofit.RetrofitInicializador;
import com.google.firebase.messaging.FirebaseMessagingService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaInstanceIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("tokenFirebaseTeste", "Refreshed token: " + token);
        // TODO: Implement this method to send any registration to your app's servers.
        enviaTokenParaServidor(token);
    }

    private void enviaTokenParaServidor(final String token) {
        Call<Void> call = new RetrofitInicializador().getDispositivoService().enviaToken(token);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, Response<Void> response) {
                Log.i("token enviado", token);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("token falhou", t.getMessage());
            }
        });

















    }

}
