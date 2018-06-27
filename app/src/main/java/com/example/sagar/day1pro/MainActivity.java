package com.example.sagar.day1pro;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button download;
    EditText inputPdfLink;
    String pdfLink;
    int downSize=0;
    ProgressBar progressBar;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download=findViewById(R.id.download);
        inputPdfLink=findViewById(R.id.pdfLink);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBox();
                pdfLink=inputPdfLink.getText().toString();
            }
        });

    }

    private void showDialogBox()
    {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Download");
        dialog.setMessage("Choose the Option");
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        final View popup_layout=layoutInflater.inflate(R.layout.popup_layout,null);
        progressBar=popup_layout.findViewById(R.id.progressBar);
        dialog.setView(popup_layout);
        dialog.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                downloadPdf();
            }
        });
        dialog.setNegativeButton("Stop Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(file.exists())
                {
                    file.delete();
                }
                return;
            }
        });
        dialog.setNeutralButton("Do In Background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialog.show();
    }
    private void downloadPdf()
    {
        Toast.makeText(this, pdfLink, Toast.LENGTH_SHORT).show();
        Call<ResponseBody> call = ApiClient.getInstance().getPdfAPI().getPdf(pdfLink);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assert response.body() != null;
                writeResponseBodyToTheDisk(response.body());
            }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t)
                {
                    Toast.makeText(MainActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    Log.d("NIC","No Internet Connection");
                }
        });
    }

    private void writeResponseBodyToTheDisk(ResponseBody body)
    {
        Toast.makeText(MainActivity.this, "Reached Response", Toast.LENGTH_SHORT).show();
        OutputStream outputStream = null;
        InputStream inputStream=new BufferedInputStream(body.byteStream());
        try {

            file = new File(this.getFilesDir(),"Hello");

            // write the inputStream to a FileOutputStream

            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                downSize += read;
                //Log.e("tag1",""+total);
                //Log.e("tag2",""+((total*100)/2801192));
                outputStream.write(bytes, 0, read);
            }
            Toast.makeText(MainActivity.this, (file.toString())+"", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, file.getPath()+"", Toast.LENGTH_LONG).show();
            downloadComplete(pdfLink);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error try occured", Toast.LENGTH_SHORT).show();
        } finally {
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void downloadComplete(final String pdfLink)
    {
        Toast.makeText(this, "Reached Complete Download", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("File Downloaded");
        dialog.setMessage("Choose the Option");
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        final View popup_layout=layoutInflater.inflate(R.layout.popup_layout,null);
        dialog.setView(popup_layout);
        dialog.setPositiveButton("Open Pdf", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent=new Intent(MainActivity.this,PdfViewer.class);
                Bundle bundle=new Bundle();
                bundle.putString("PDFLINK",pdfLink);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        dialog.show();
    }
}
