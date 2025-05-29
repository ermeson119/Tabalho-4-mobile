package com.example.tabalho4.modelView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.tabalho4.models.entity.Estudante;
import com.example.tabalho4.models.entity.Turma;

import java.util.ArrayList;
import java.util.List;

public class TurmaViewModel extends ViewModel {
    private final MutableLiveData<Double> mediaTurmaLiveData    = new MutableLiveData<>();
    private final MutableLiveData<Double> mediaIdadeLiveData   = new MutableLiveData<>();
    private final MutableLiveData<String> maiorMediaLiveData   = new MutableLiveData<>();
    private final MutableLiveData<String> menorMediaLiveData   = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> aprovadosLiveData  = new MutableLiveData<>();
    private final MutableLiveData<List<Estudante>> reprovadosLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData        = new MutableLiveData<>();

    private final EstudanteViewModel estudanteViewModel;
    private final Turma turma = new Turma(new ArrayList<>());

    // Observer definido como campo para podermos removê-lo depois
    private final Observer<List<Estudante>> estudantesObserver = this::onEstudantesChanged;

    public TurmaViewModel() {
        this.estudanteViewModel = new EstudanteViewModel();
        // registra o observer
        estudanteViewModel.getEstudantes().observeForever(estudantesObserver);
    }

    public LiveData<Double> getMediaTurma()   { return mediaTurmaLiveData; }
    public LiveData<Double> getMediaIdade()   { return mediaIdadeLiveData; }
    public LiveData<String> getMaiorMedia()   { return maiorMediaLiveData; }
    public LiveData<String> getMenorMedia()   { return menorMediaLiveData; }
    public LiveData<List<Estudante>> getAprovados()  { return aprovadosLiveData; }
    public LiveData<List<Estudante>> getReprovados() { return reprovadosLiveData; }
    public LiveData<String> getError()        { return errorLiveData; }

    private void onEstudantesChanged(List<Estudante> estudantes) {
        if (estudantes != null && !estudantes.isEmpty()) {
            try {
                double totalMedia    = 0;
                double totalIdade    = 0;
                double maiorMedia    = Double.MIN_VALUE;
                double menorMedia    = Double.MAX_VALUE;
                Estudante emMaior     = null;
                Estudante emMenor     = null;
                List<Estudante> aprov = new ArrayList<>();
                List<Estudante> reprov = new ArrayList<>();

                for (Estudante e : estudantes) {
                    double m = turma.calcularMedia(e);
                    double f = turma.calcularFrequencia(e);
                    totalMedia += m;
                    totalIdade += e.getIdade();

                    if (m > maiorMedia) {
                        maiorMedia = m;
                        emMaior = e;
                    }
                    if (m < menorMedia) {
                        menorMedia = m;
                        emMenor = e;
                    }

                    if (m >= 7.0 && f >= 75.0) aprov.add(e);
                    else reprov.add(e);
                }

                double avgM = totalMedia / estudantes.size();
                double avgI = totalIdade / estudantes.size();

                mediaTurmaLiveData.postValue(avgM);
                mediaIdadeLiveData.postValue(avgI);
                maiorMediaLiveData.postValue(
                        String.format("%s (%.2f)", emMaior != null ? emMaior.getNome() : "N/A", maiorMedia)
                );
                menorMediaLiveData.postValue(
                        String.format("%s (%.2f)", emMenor != null ? emMenor.getNome() : "N/A", menorMedia)
                );
                aprovadosLiveData.postValue(aprov);
                reprovadosLiveData.postValue(reprov);

            } catch (Exception ex) {
                errorLiveData.postValue("Erro ao calcular estatísticas: " + ex.getMessage());
            }
        } else {
            // sem dados: zera tudo
            mediaTurmaLiveData.postValue(0.0);
            mediaIdadeLiveData.postValue(0.0);
            maiorMediaLiveData.postValue("N/A");
            menorMediaLiveData.postValue("N/A");
            aprovadosLiveData.postValue(new ArrayList<>());
            reprovadosLiveData.postValue(new ArrayList<>());
        }
    }

    @Override
    protected void onCleared() {
        // remove o observer para evitar vazamento
        estudanteViewModel.getEstudantes().removeObserver(estudantesObserver);
        super.onCleared();
    }
}
