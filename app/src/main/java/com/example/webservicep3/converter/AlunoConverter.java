package com.example.webservicep3.converter;

import com.example.webservicep3.modelo.Aluno;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

public class AlunoConverter {
    public String converteParaJSON(List<Aluno> alunos) {
        JSONStringer js = new JSONStringer();

        try {
            js.object().key("list").array().object().key("aluno").array();
            for (Aluno aluno : alunos) {
                js.object();
                js.key("nome").value(aluno.getNome());
                js.key("nota").value(aluno.getNota());
                js.endObject();
            }
            js.endArray().endObject().endArray().endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return js.toString();
    }

}
