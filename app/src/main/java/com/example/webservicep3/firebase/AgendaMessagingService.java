package com.example.webservicep3.firebase;

import android.util.Log;

import com.example.webservicep3.dao.AlunoDAO;
import com.example.webservicep3.dto.AlunoSync;
import com.example.webservicep3.event.AtualizaListaAlunoEvent;
import com.example.webservicep3.sinc.AlunoSincronizador;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;


public class AgendaMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("mensagem recebida", String.valueOf(mensagem));

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem) {
        String chaveDeAcesso = "alunoSync";
        if(mensagem.containsKey(chaveDeAcesso)){
            String json = mensagem.get(chaveDeAcesso);
            ObjectMapper mapper = new ObjectMapper();
            try {
                AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);
                new AlunoSincronizador(AgendaMessagingService.this).sincroniza(alunoSync);
                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunoEvent());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
