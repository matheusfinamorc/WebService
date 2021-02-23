package com.example.webservicep3.sinc;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.webservicep3.ListaAlunosActivity;
import com.example.webservicep3.dao.AlunoDAO;
import com.example.webservicep3.dto.AlunoSync;
import com.example.webservicep3.event.AtualizaListaAlunoEvent;
import com.example.webservicep3.modelo.Aluno;
import com.example.webservicep3.preferences.AlunoPreferences;
import com.example.webservicep3.retrofit.RetrofitInicializador;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kotlin.reflect.KVariance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {
    private final Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);

    }

    public void buscaTodos(){
        if(preferences.temVersao()){
            buscaAlunosNovos(); //buscaNovos
        }else{
            buscaAlunos();
        }
    }

    private void buscaAlunosNovos() {
        String versao = preferences.getVersao();
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().novos(versao);

        call.enqueue(buscaAlunosCallBack());
    }

    private void buscaAlunos() {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();

        call.enqueue(buscaAlunosCallBack());
    }

    @NonNull
    private Callback<AlunoSync> buscaAlunosCallBack() {
        return new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                sincroniza(alunoSync);

                //Log.i("versao", preferences.getVersao());

                bus.post(new AtualizaListaAlunoEvent());
                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                bus.post(new AtualizaListaAlunoEvent());
            }
        };
    }

    public void sincroniza(AlunoSync alunoSync) {
        String versao = alunoSync.getMomentoDaUltimaModificacao();

        Log.i("versao externa", versao);

        if(temVersaoNova(versao)) {
            preferences.salvaVersao(versao);

            Log.i("versao atual", preferences.getVersao());

            AlunoDAO dao = new AlunoDAO(context);
            dao.sincroniza(alunoSync.getAlunos());
            dao.close();
        }
    }

    private boolean temVersaoNova(String versao) {
        if(!preferences.temVersao())
            return true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        try {
            Date dataExterna = format.parse(versao);
            String versaoInterna = preferences.getVersao();

            Log.i("versao interna", versaoInterna);

            Date dataInterna = format.parse(versaoInterna);

            return dataExterna.after(dataInterna); // verifica se a versão é mais nova pela data de publicação dela

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void sincronizaAlunosInternos(){
        final AlunoDAO dao = new AlunoDAO(context);
        final List<Aluno> alunos = dao.listaNaoSincronizados();
        dao.close();

        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().atualiza(alunos);

        call.enqueue(new Callback<AlunoSync>() {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response) {
                AlunoSync alunoSync = response.body();
                sincroniza(alunoSync);
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t) {

            }
        });
    }

    public void deleta(Aluno aluno) {
        Call<Void> call = new RetrofitInicializador().getAlunoService().deleta(aluno.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                AlunoDAO dao = new AlunoDAO(context);
                dao.deleta(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}