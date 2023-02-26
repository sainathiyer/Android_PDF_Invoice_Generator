package com.example.pdfgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.Manifest;

public class MainActivity extends AppCompatActivity {

    Button genPdfButton;
    Spinner pizzaOptions;
    EditText customerName, phone, quantity;
    Bitmap bmp, scaledbmp;
    int pageWidth = 1200;
    float prices[] = new float[]{0, 200, 450, 325};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genPdfButton = findViewById(R.id.genPdfButton);
        pizzaOptions = findViewById(R.id.pizzaOptions);
        customerName = findViewById(R.id.customerName);
        phone = findViewById(R.id.phone);
        quantity = findViewById(R.id.quantity);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pizza_head);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 1200,518, false);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        createPDF();
    }

    private void createPDF(){
        genPdfButton.setOnClickListener((view) -> {
            if(customerName.getText().toString().length() == 0
            || phone.getText().toString().length() == 0
            || quantity.getText().toString().length() == 0){
                Toast.makeText(MainActivity.this, "Some Fields are Left Unfilled", Toast.LENGTH_LONG).show();
            } else {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    PdfDocument myPdfDocument = new PdfDocument();
                    Paint myPaint = new Paint();
                    Paint titlePaint = new Paint();


                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                    Canvas myCanvas = myPage1.getCanvas();
                    myCanvas.drawBitmap(scaledbmp, 0, 0, myPaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                    titlePaint.setTextSize(70);
                    myCanvas.drawText("Invoice", pageWidth/2, 500, titlePaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(35f);
                    myPaint.setColor(Color.BLACK);
                    myCanvas.drawText("Customer Name: "+customerName.getText(), 20, 590, myPaint);
                    myCanvas.drawText("Contact No: "+phone.getText(), 20, 640, myPaint);

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    myCanvas.drawText("Invoice No: "+"23224", pageWidth-20, 590, myPaint);
                    myCanvas.drawText("Date: "+"26-02-2023", pageWidth-20, 640, myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(2);
                    myCanvas.drawRect(20, 780, pageWidth-20, 860, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setStyle(Paint.Style.FILL);
                    myCanvas.drawText("Sr No", 40, 830, myPaint);
                    myCanvas.drawText("Item Description:", 200, 830, myPaint);
                    myCanvas.drawText("Price", 700, 830, myPaint);
                    myCanvas.drawText("Qty", 900, 830, myPaint);
                    myCanvas.drawText("Total", 1050, 830, myPaint);

                    myCanvas.drawLine(180, 790, 180, 840, myPaint);
                    myCanvas.drawLine(680, 790, 680, 840, myPaint);
                    myCanvas.drawLine(880, 790, 880, 840, myPaint);
                    myCanvas.drawLine(1030, 790, 1030, 840, myPaint);

                    float total1 = 0;
                    if (pizzaOptions.getSelectedItemPosition() != 0) {
                        myCanvas.drawText("1.", 40, 950, myPaint);
                        myCanvas.drawText(pizzaOptions.getSelectedItem().toString(), 200, 950, myPaint);
                        myCanvas.drawText(String.valueOf(prices[pizzaOptions.getSelectedItemPosition()]), 700, 950, myPaint);
                        myCanvas.drawText(quantity.getText().toString(), 900, 950, myPaint);

                        total1 = Float.parseFloat(quantity.getText().toString())*prices[pizzaOptions.getSelectedItemPosition()];

                        myPaint.setTextAlign(Paint.Align.RIGHT);
                        myCanvas.drawText(String.valueOf(total1), pageWidth-40, 950, myPaint);

                        myPaint.setTextAlign(Paint.Align.LEFT);
                    }

                    float subTotal = total1+0;
                    myCanvas.drawLine(680, 1200, pageWidth-20, 1200, myPaint);
                    myCanvas.drawText("Sub Total", 700, 1250, myPaint);
                    myCanvas.drawText(":", 900, 1250, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    myCanvas.drawText(String.valueOf(subTotal), pageWidth-40, 1250, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myCanvas.drawText("Tax (12%)", 700, 1300, myPaint);
                    myCanvas.drawText(":", 900, 1300, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    myCanvas.drawText(String.valueOf(subTotal*12/100), pageWidth-40, 1300, myPaint);
                    myPaint.setTextAlign(Paint.Align.LEFT);

                    myPaint.setColor(Color.rgb(247, 147, 30));
                    myCanvas.drawRect(680, 1350, pageWidth-20, 1450, myPaint);

                    myPaint.setColor(Color.BLACK);
                    myPaint.setTextSize(50f);
                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myCanvas.drawText("Total", 700, 1415, myPaint);
                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    myCanvas.drawText(String.valueOf(subTotal+(subTotal*12/100)), pageWidth-40, 1415, myPaint);

                    myPdfDocument.finishPage(myPage1);

                    File file = new File(Environment.getExternalStorageDirectory(), "/hello.pdf");
                    try {
                        myPdfDocument.writeTo(new FileOutputStream(file));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    myPdfDocument.close();
                }
            }
        });
    }
}