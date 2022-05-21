package com.ecom.bookusinggooglesheet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.shockwave.pdfium.PdfDocument;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import javax.net.ssl.HttpsURLConnection;

public class pdfview extends AppCompatActivity {

    PDFView pdfView;
    Button btnprev;
    Button btnnext;
    Button btnzoomin;
    Button btnzoomout;
    Button btndefault;
    EditText edtno;
    int spacingval =0;
    // url of our PDF file.
    String pdfurl = "https://firebasestorage.googleapis.com/v0/b/home-collection-bac8b.appspot.com/o/Copy%20of%20Gandufen.pdf?alt=media&token=caaeeff7-44e5-40f4-9be9-858f88cf2282";
    public float zoomValue = 1;
    Integer pageNumber = 0;
    String pdfFileName;
    boolean setcheck=true;
    private static final String TAG = pdfview.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView = findViewById(R.id.idPDFView);
        new RetrivePDFfromUrl().execute(pdfurl);
        btnprev =(Button) findViewById(R.id.btnprev);
        btnnext =(Button) findViewById(R.id.btnnext);
        btnzoomin =(Button) findViewById(R.id.btnzoomin);
        btnzoomout =(Button) findViewById(R.id.btnzoomout);
        btndefault =(Button) findViewById(R.id.btndefault);
        edtno = findViewById(R.id.editTextNumber);
        edtno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!edtno.getText().toString().trim().equals("")) {
                    pdfView.jumpTo(Integer.parseInt(edtno.getText().toString().trim())-1);
                }


            }
        });
        SwitchMaterial sw = (SwitchMaterial) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    setcheck =true;
                   spacingval=0;

                } else {
                    // The toggle is disabled
                    setcheck=false;
                   spacingval=5;
                }
                new RetrivePDFfromUrl().execute(pdfurl);
            }
        });
                btnprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (android.net.ConnectivityManager) pdfview.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null) {
                    pdfView.jumpTo(pdfView.getCurrentPage() - 1, true);
                }
                else {
                    Toast.makeText(pdfview.this, "No internet connection, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (android.net.ConnectivityManager) pdfview.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null) {
                    pdfView.jumpTo(pdfView.getCurrentPage() + 1, true);

                }
                else {
                    Toast.makeText(pdfview.this, "No internet connection, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnzoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (android.net.ConnectivityManager) pdfview.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null) {
                   zoomIn();
                }
                else {
                    Toast.makeText(pdfview.this, "No internet connection, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnzoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (android.net.ConnectivityManager) pdfview.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null) {
                    zoomOut();
                }
                else {
                    Toast.makeText(pdfview.this, "No internet connection, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
        btndefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (android.net.ConnectivityManager) pdfview.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                if (activeNetwork != null) {
                    pdfView.zoomTo(1);
                    pdfView.loadPages();
                }
                else {
                    Toast.makeText(pdfview.this, "No internet connection, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void zoomOut() {

        if (zoomValue != 1) {
            --zoomValue;
            pdfView.zoomTo(zoomValue);
            pdfView.loadPages();
        }
    }



    public void zoomIn() {
        ++zoomValue;
        pdfView.zoomTo(zoomValue);
        pdfView.loadPages();

    }
    // create an async task class for loading pdf file from URL.
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream)
                    .defaultPage(0)
                    .autoSpacing(false)
                    .spacing(spacingval)
                    .swipeHorizontal(setcheck)
                    .pageSnap(setcheck)
                    .pageFling(setcheck)
                    .enableDoubletap(true)
                    .pageFitPolicy(FitPolicy.BOTH)
                    .enableSwipe(true)
                    .enableAntialiasing(true)
                    .enableAnnotationRendering(true)
                    .fitEachPage(true)
                    .load();
        }
    }


}