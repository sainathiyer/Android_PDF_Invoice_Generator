package com.example.pdfgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.Manifest;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button genPdfButton;
    EditText gstPercentage, rate, quantity, material, hsnCode, customersContactNumber, customersPanNumber, customersAddress, numberOfBags, customersGstNumber,
            vehicleNumber, lrNumber, transport, challanNumber, date, invoiceNumber;
    Bitmap bmpHeader, scaledbmpHeader, bmpFooter, scaledbmpFooter, bmpLogo, scaledbmpLogo;
    int pageHeight = 1122;
    int pageWidth = 793;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        genPdfButton = findViewById(R.id.genPdfButton);
        gstPercentage = findViewById(R.id.gstPercentage);
        rate = findViewById(R.id.rate);
        quantity = findViewById(R.id.quantity);
        material = findViewById(R.id.material);
        hsnCode = findViewById(R.id.hsnCode);
        customersContactNumber = findViewById(R.id.customersContactNumber);
        customersPanNumber = findViewById(R.id.customersPanNumber);
        customersAddress = findViewById(R.id.customersAddress);
        numberOfBags = findViewById(R.id.numberOfBags);
        customersGstNumber = findViewById(R.id.customersGstNumber);
        vehicleNumber = findViewById(R.id.vehicleNumber);
        lrNumber = findViewById(R.id.lrNumber);
        transport = findViewById(R.id.transport);
        challanNumber = findViewById(R.id.challanNumber);
        date = findViewById(R.id.date);
        invoiceNumber = findViewById(R.id.invoiceNumber);
        bmpHeader = BitmapFactory.decodeResource(getResources(), R.drawable.header);
        scaledbmpHeader = Bitmap.createScaledBitmap(bmpHeader, pageWidth, 100, false);
        bmpFooter = BitmapFactory.decodeResource(getResources(), R.drawable.footer);
        scaledbmpFooter = Bitmap.createScaledBitmap(bmpFooter, pageWidth, 100, false);
        bmpLogo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        scaledbmpLogo = Bitmap.createScaledBitmap(bmpLogo, 200, 109, false);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        createPDF();
    }

    public static String convertToIndianCurrency(String num) {
        BigDecimal bd = new BigDecimal(num);
        long number = bd.longValue();
        long no = bd.longValue();
        int decimal = (int) (bd.remainder(BigDecimal.ONE).doubleValue() * 100);
        int digits_length = String.valueOf(no).length();
        int i = 0;
        ArrayList<String> str = new ArrayList<>();
        HashMap<Integer, String> words = new HashMap<>();
        words.put(0, "");
        words.put(1, "One");
        words.put(2, "Two");
        words.put(3, "Three");
        words.put(4, "Four");
        words.put(5, "Five");
        words.put(6, "Six");
        words.put(7, "Seven");
        words.put(8, "Eight");
        words.put(9, "Nine");
        words.put(10, "Ten");
        words.put(11, "Eleven");
        words.put(12, "Twelve");
        words.put(13, "Thirteen");
        words.put(14, "Fourteen");
        words.put(15, "Fifteen");
        words.put(16, "Sixteen");
        words.put(17, "Seventeen");
        words.put(18, "Eighteen");
        words.put(19, "Nineteen");
        words.put(20, "Twenty");
        words.put(30, "Thirty");
        words.put(40, "Forty");
        words.put(50, "Fifty");
        words.put(60, "Sixty");
        words.put(70, "Seventy");
        words.put(80, "Eighty");
        words.put(90, "Ninety");
        String digits[] = {"", "Hundred", "Thousand", "Lakh", "Crore"};
        while (i < digits_length) {
            int divider = (i == 2) ? 10 : 100;
            number = no % divider;
            no = no / divider;
            i += divider == 10 ? 1 : 2;
            if (number > 0) {
                int counter = str.size();
                String plural = (counter > 0 && number > 9) ? "s" : "";
                String tmp = (number < 21) ? words.get(Integer.valueOf((int) number)) + " " + digits[counter] + plural : words.get(Integer.valueOf((int) Math.floor(number / 10) * 10)) + " " + words.get(Integer.valueOf((int) (number % 10))) + " " + digits[counter] + plural;
                str.add(tmp);
            } else {
                str.add("");
            }
        }

        Collections.reverse(str);
        String Rupees = String.join(" ", str).trim();

        String paise = (decimal) > 0 ? " And Paise " + words.get(Integer.valueOf((int) (decimal - decimal % 10))) + " " + words.get(Integer.valueOf((int) (decimal % 10))) : "";
        return "Rupees " + Rupees + paise + " Only";
    }

    private void createPDF(){
        genPdfButton.setOnClickListener((view) -> {
            if(gstPercentage.getText().toString().length() == 0
                    || rate.getText().toString().length() == 0
                    || quantity.getText().toString().length() == 0
                    || material.getText().toString().length() == 0
                    || hsnCode.getText().toString().length() == 0
                    || customersContactNumber.getText().toString().length() == 0
                    || customersPanNumber.getText().toString().length() == 0
                    || customersAddress.getText().toString().length() == 0
                    || numberOfBags.getText().toString().length() == 0
                    || customersGstNumber.getText().toString().length() == 0
                    || vehicleNumber.getText().toString().length() == 0
                    || lrNumber.getText().toString().length() == 0
                    || transport.getText().toString().length() == 0
                    || challanNumber.getText().toString().length() == 0
                    || date.getText().toString().length() == 0
                    || invoiceNumber.getText().toString().length() == 0){
                Toast.makeText(MainActivity.this, "Please Fill All the Fields", Toast.LENGTH_LONG).show();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    PdfDocument myPdfDocument = new PdfDocument();
                    Paint myPaint = new Paint();
                    Paint titlePaint = new Paint();

                    PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
                    PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
                    Canvas myCanvas = myPage1.getCanvas();
                    myCanvas.drawBitmap(scaledbmpHeader, 0, 0, myPaint);
                    myCanvas.drawBitmap(scaledbmpFooter, 0, 1022, myPaint);

                    myPaint.setTextAlign(Paint.Align.LEFT);
                    myPaint.setTextSize(18f);
                    myPaint.setColor(Color.BLACK);
                    myCanvas.drawText("PGS Enterprises", 15, 130, myPaint);
                    myCanvas.drawText("E-405, DSK Sundarban, Malwadi Road, Nr. Amanora Towncentre,", 15, 150, myPaint);
                    myCanvas.drawText("Hadapsar, Pune – 411028.", 15, 170, myPaint);
                    myCanvas.drawText("Mobile No: 9623029294/8412018506", 15, 190, myPaint);
                    myCanvas.drawText("Email: pgsenterprisesindia@gmail.com", 15, 210, myPaint);

                    myPaint.setTextAlign(Paint.Align.RIGHT);
                    myCanvas.drawBitmap(scaledbmpLogo, 575, 109, myPaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(1.3f);
                    myCanvas.drawRect(18, 229, pageWidth - 18, 1010, myPaint);
                    myCanvas.drawLine(18f, 251.5f, pageWidth - 18, 251.5f, myPaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTextSize(20);
                    myCanvas.drawText("DEBIT", pageWidth / 4, 247, titlePaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTextSize(20);
                    myCanvas.drawText("TAX INVOICE", pageWidth / 2, 247, titlePaint);

                    titlePaint.setTextAlign(Paint.Align.CENTER);
                    titlePaint.setTextSize(20);
                    myCanvas.drawText("ORIGINAL", 594, 247, titlePaint);

                    titlePaint.setTextAlign(Paint.Align.LEFT);
                    titlePaint.setTextSize(15);
                    myCanvas.drawText("To,", 32, 270, titlePaint);

                    titlePaint.setTextSize(13);
                    myCanvas.drawText(customersAddress.getText().toString(), 32, 285, titlePaint);
                    myCanvas.drawText("Customer’s GST No: " + customersGstNumber.getText().toString(), 32, 330, titlePaint);
                    myCanvas.drawText("Customer’s PAN No: " + customersPanNumber.getText().toString(), 32, 345, titlePaint);
                    myCanvas.drawText("Customer's Mob No: " + customersContactNumber.getText().toString(), 32, 360, titlePaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(1.3f);
                    myCanvas.drawLine(570, 251.5f, 570, 367.5f, myPaint);
                    myCanvas.drawLine(18f, 315, 570, 315, myPaint);

                    titlePaint.setTextAlign(Paint.Align.LEFT);
                    titlePaint.setTextSize(13);
                    myCanvas.drawText("Date", 580, 270, titlePaint);
                    myCanvas.drawText(": " + date.getText().toString(), 660, 270, titlePaint);
                    myCanvas.drawText("Invoice No", 580, 285, titlePaint);
                    myCanvas.drawText(": " + invoiceNumber.getText().toString(), 660, 285, titlePaint);
                    myCanvas.drawText("Challan No", 580, 300, titlePaint);
                    myCanvas.drawText(": " + challanNumber.getText().toString(), 660, 300, titlePaint);
                    myCanvas.drawText("Transport", 580, 315, titlePaint);
                    myCanvas.drawText(": " + transport.getText().toString(), 660, 315, titlePaint);
                    myCanvas.drawText("L.R. No", 580, 330, titlePaint);
                    myCanvas.drawText(": " + lrNumber.getText().toString(), 660, 330, titlePaint);
                    myCanvas.drawText("Vehicle No", 580, 345, titlePaint);
                    myCanvas.drawText(": " + vehicleNumber.getText().toString(), 660, 345, titlePaint);
                    myCanvas.drawText("No of Bags", 580, 360, titlePaint);
                    myCanvas.drawText(": " + numberOfBags.getText().toString(), 660, 360, titlePaint);

                    myCanvas.drawLine(18f, 367.5f, pageWidth - 18, 367.5f, myPaint);
                    myCanvas.drawLine(18f, 389.5f, pageWidth - 18, 389.5f, myPaint);

                    titlePaint.setTextSize(12.5f);
                    myCanvas.drawText("Sr.No", 30, 383, titlePaint);
                    myCanvas.drawText("Particulars", 82, 383, titlePaint);
                    myCanvas.drawText("HSN Code", 440, 383, titlePaint);
                    myCanvas.drawText("Quantity", 510, 383, titlePaint);
                    myCanvas.drawText("Rate", 578, 383, titlePaint);
                    myCanvas.drawText("GST %", 635, 383, titlePaint);
                    myCanvas.drawText("Amount", 700, 383, titlePaint);

                    myCanvas.drawLine(18f, 600, pageWidth - 18, 600, myPaint);
                    myCanvas.drawLine(18f, 622, pageWidth - 18, 622, myPaint);

                    myCanvas.drawText("1.", 42, 410, titlePaint);
                    titlePaint.setTextSize(14);
                    myCanvas.drawText("Sub Total", 590, 615, titlePaint);

                    myPaint.setStyle(Paint.Style.STROKE);
                    myPaint.setStrokeWidth(1.3f);
                    myCanvas.drawLine(70, 367.5f, 70, 600, myPaint);
                    myCanvas.drawLine(436, 367.5f, 436, 600, myPaint);
                    myCanvas.drawLine(502, 367.5f, 502, 600, myPaint);
                    myCanvas.drawLine(564, 367.5f, 564, 881, myPaint);
                    myCanvas.drawLine(625, 367.5f, 625, 600, myPaint);
                    myCanvas.drawLine(679, 367.5f, 679, 881, myPaint);

                    titlePaint.setTextAlign(Paint.Align.LEFT);
                    titlePaint.setTextSize(12.5f);
                    myCanvas.drawText(material.getText().toString(), 80, 410, titlePaint);
                    myCanvas.drawText(hsnCode.getText().toString(), 446, 410, titlePaint);
                    myCanvas.drawText(quantity.getText().toString() + " Kg", 512, 410, titlePaint);
                    myCanvas.drawText(rate.getText().toString() + "/Kg", 574, 410, titlePaint);
                    myCanvas.drawText(gstPercentage.getText().toString() + "%", 635, 410, titlePaint);

                    float amount = 0;
                    amount = Float.parseFloat(quantity.getText().toString()) * Float.parseFloat(rate.getText().toString());
                    myCanvas.drawText(String.valueOf(amount), 689, 410, titlePaint);
                    myCanvas.drawText(String.valueOf(amount), 689, 615, titlePaint);

                    titlePaint.setTextSize(14);
                    myCanvas.drawText("PGS ENTERPRISES", 32, 638, titlePaint);
                    myCanvas.drawText("PAN NO", 32, 653, titlePaint);
                    myCanvas.drawText(": ABRPI2048E", 145, 653, titlePaint);
                    myCanvas.drawText("GSTIN / UIN NO", 32, 668, titlePaint);
                    myCanvas.drawText(": 27ABRPI2048E2ZN", 145, 668, titlePaint);
                    myCanvas.drawText("STATE CODE", 32, 683, titlePaint);
                    myCanvas.drawText(": 27", 145, 683, titlePaint);
                    myCanvas.drawText("STATE", 32, 698, titlePaint);
                    myCanvas.drawText(": MAHARASHTRA", 145, 698, titlePaint);
                    myCanvas.drawText("BANK DETAILS", 32, 713, titlePaint);
                    myCanvas.drawText(": INDIAN OVERSEAS BANK, Magarpatta Branch Pune", 145, 713, titlePaint);
                    myCanvas.drawText("A/C Number", 32, 728, titlePaint);
                    myCanvas.drawText(": 225702000000186", 145, 728, titlePaint);
                    myCanvas.drawText("IFSC CODE", 32, 743, titlePaint);
                    myCanvas.drawText(": IOBA0002257", 145, 743, titlePaint);
                    myCanvas.drawText("MICR CODE", 32, 758, titlePaint);
                    myCanvas.drawText(": 411020014", 145, 758, titlePaint);

                    myCanvas.drawLine(18f, 762, pageWidth - 18, 762, myPaint);
                    myCanvas.drawLine(564, 793, pageWidth - 18, 793, myPaint);
                    myCanvas.drawLine(564, 824, pageWidth - 18, 824, myPaint);

                    titlePaint.setTextSize(13);
                    myCanvas.drawText("Terms:", 32, 780, titlePaint);
                    myCanvas.drawText("* Being Reprocessed Granules there will always be Variation in Colour from Batch to Batch.", 24, 795, titlePaint);
                    myCanvas.drawText("* Any Complaint regarding the Bill should be Intimated in Writing within 7 Days.", 24, 810, titlePaint);
                    myCanvas.drawText("* All Transactions Subject to Pune Jurisdiction only. E.&.O.E.", 24, 825, titlePaint);
                    myCanvas.drawText("* The Seller is Not Responsible for Any Damage that happens during Transit.", 24, 840, titlePaint);

                    myCanvas.drawLine(18f, 855, pageWidth - 18, 855, myPaint);

                    float actualGst = Float.parseFloat(gstPercentage.getText().toString());

                    titlePaint.setTextSize(14);
                    myCanvas.drawText("SGST @ " + actualGst / 2 + " %", 575, 783, titlePaint);
                    myCanvas.drawText(String.valueOf((amount * (actualGst / 2)) / 100), 689, 783, titlePaint);
                    myCanvas.drawText("CGST @ " + actualGst / 2 + " %", 575, 813, titlePaint);
                    myCanvas.drawText(String.valueOf((amount * (actualGst / 2)) / 100), 689, 813, titlePaint);
                    myCanvas.drawText("IGST @ " + actualGst + " %", 576, 843, titlePaint);
                    //myCanvas.drawText(String.valueOf((amount*actualGst)/100), 689, 843, titlePaint);

                    myCanvas.drawLine(18f, 881, pageWidth - 18, 881, myPaint);

                    float grandTotal = 0;
                    float totalGst = 0;
                    totalGst = (amount * actualGst) / 100;
                    grandTotal = amount + totalGst;

                    myCanvas.drawText("Amount(in words):", 32, 897, titlePaint);
                    myCanvas.drawText(convertToIndianCurrency(String.valueOf(grandTotal)), 31, 914, titlePaint);

                    titlePaint.setTextSize(14);
                    myCanvas.drawText("Grand Total", 584, 873, titlePaint);
                    myCanvas.drawText(String.valueOf(grandTotal), 689, 873, titlePaint);

                    myCanvas.drawLine(18f, 925, pageWidth - 18, 925, myPaint);

                    titlePaint.setTextSize(20);
                    myCanvas.drawText("For PGS ENTERPRISES", 555, 947, titlePaint);
                    titlePaint.setTextSize(18);
                    myCanvas.drawText("Authorized Signatory", 574, 1000, titlePaint);
                    //---------------------------------------------------------------------------------------------

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