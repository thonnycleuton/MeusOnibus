package com.thonnycleuton.meusonibus.inthegraAPI;

import android.content.Context;
import android.util.Log;

import com.equalsp.stransthe.CachedServiceFileHander;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by thonnycleuton on 19/09/16.
 */
public class FileHandler implements CachedServiceFileHander {
    private final String TAG = "FileHandler";
    private final Context mContext;

    public FileHandler(Context context){
        this.mContext = context;
    }

    @Override
    public String loadCacheFile() throws IOException {
        Log.d(TAG, "loadCacheFile Called");

        InputStream inputStream = mContext.getResources().openRawResource(
                mContext.getResources().getIdentifier("cachedinthegraservice",
                        "raw", mContext.getPackageName()));

        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        String str = new String(buffer);
        return str;
    }


    /**
     * Nenhuma operação deve ser feita nesse método, o arquivo do cache é fornecido junto com o app.
     * @param content, o conteúdo que deveria ser salvo no arquivo
     * @throws IOException, caso houvesse algum problema no salvamento do arquivo
     */
    @Deprecated
    @Override
    public void saveCacheFile(String content) throws IOException {
        Log.d(TAG, "saveCacheFile Called");
    }
}
