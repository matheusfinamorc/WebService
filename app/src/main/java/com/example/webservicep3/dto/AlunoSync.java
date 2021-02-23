package com.example.webservicep3.dto;

import com.example.webservicep3.modelo.Aluno;

import java.util.List;

public class AlunoSync {

    private List<Aluno> alunos;
    private String momentoDaUltimaModificacao;

    public String getMomentoDaUltimaModificacao() {
        return momentoDaUltimaModificacao;
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }
}
