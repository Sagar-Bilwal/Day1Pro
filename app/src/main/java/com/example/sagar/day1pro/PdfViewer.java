package com.example.sagar.day1pro;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewer extends AppCompatActivity
{
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView=findViewById(R.id.pdfView);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String pdfLink=bundle.getString("PDFLINK");
        File file = new File(getFilesDir(),"Hello");
        Toast.makeText(this, file.getPath()+"", Toast.LENGTH_SHORT).show();
//        try {
//            inputStream=new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
//        }
        pdfView.fromFile(file).load();
    }
}
