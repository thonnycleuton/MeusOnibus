package com.thonnycleuton.meusonibus.inthegraAPI.AsyncTasks;

import com.equalsp.stransthe.Veiculo;

import java.util.List;

/**
 * Created by thonnycleuton on 19/09/16.
 */
public interface InthegraVeiculosAsyncResponse {
    void processFinish(List<Veiculo> veiculos);
}
