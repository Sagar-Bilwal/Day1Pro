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
import android.widget.TextView;
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
    TextView progressPercent;
    File file;
    boolean check=true;
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
        dialog.setMessage("Choose the Option").setCancelable(false);
        LayoutInflater layoutInflater=LayoutInflater.from(this);
        final View popup_layout=layoutInflater.inflate(R.layout.popup_layout,null);
        progressBar=popup_layout.findViewById(R.id.progressBar);
        progressPercent=popup_layout.findViewById(R.id.progressPercent);
        progressPercent.setText("0%");
        dialog.setCancelable(false).setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        dialog.setNegativeButton("Stop Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(MainActivity.this, "Download Cancelled", Toast.LENGTH_SHORT).show();
                check=false;
                if(file!=null&&file.exists())
                {
                    file.delete();
                }
                return;
            }
        });
        dialog.setNeutralButton("Do In Background", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                check=true;
                return;
            }
        });
        dialog.setView(popup_layout);
        AlertDialog dialog1=dialog.create();
        dialog1.show();
        dialog1.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check=true;
                downloadPdf();
            }
        });
    }
    private void downloadPdf()
    {
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
        OutputStream outputStream = null;
        InputStream inputStream=new BufferedInputStream(body.byteStream());
        try {
            file = new File(this.getFilesDir(),"Hello");

            // write the inputStream to a FileOutputStream

            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while (((read = inputStream.read(bytes)) != -1)&& check) {
                downSize += read;
                //Log.e("tag1",""+total);
                //Log.e("tag2",""+((total*100)/2801192));
                outputStream.write(bytes, 0, read);
            }
            if(!check)
            {
                if(file!=null && file.exists())
                {
                    file.delete();
                }
                return;
            }
            downloadComplete(pdfLink);
        } catch (IOException e) {
            e.printStackTrace();
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
        progressPercent.setText("100%");
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
