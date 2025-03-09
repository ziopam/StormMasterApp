package com.example.stormmasterclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.stormmasterclient.helpers.RoomDatabase.BrainstormRepository;
import com.example.stormmasterclient.helpers.dialogs.DeleteBrainStormDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Activity for viewing a specific brainstorm.
 */
public class BrainstormViewActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> createPDFLauncher;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     * down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     * Otherwise it is null.
     * @see DeleteBrainStormDialog
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brainstorm_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            // Set up the back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Initialize the TextView elements
        MaterialTextView titleTextView = findViewById(R.id.brainstormTitle);
        MaterialTextView dateTextView = findViewById(R.id.brainstormDate);
        MaterialTextView participantsTextView = findViewById(R.id.brainstormParticipants);
        MaterialTextView detailsTextView = findViewById(R.id.brainstormDetails);

        // Set the text of the TextView elements
        Intent intent = getIntent();
        titleTextView.setText(intent.getStringExtra("title"));
        dateTextView.setText(intent.getStringExtra("date"));
        participantsTextView.setText(intent.getStringExtra("participants"));
        String htmlText = intent.getStringExtra("details");
        htmlText = htmlText.replace("\n", "<br>");
        detailsTextView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));

        // Set up the delete button
        MaterialButton deleteButton = findViewById(R.id.deleteBrainstormButton);
        boolean isCreator = intent.getBooleanExtra("isCreator", false);
        if(!isCreator){
            // Hide the delete button if the user is not the creator of the brainstorm
            deleteButton.setVisibility(MaterialButton.GONE);
            LinearLayout buttonsLayout = findViewById(R.id.buttonsLayout);
            buttonsLayout.setWeightSum(4); // Change for better appearance
        }

        // Set up the delete button click listener
        deleteButton.setOnClickListener(v -> {
            DeleteBrainStormDialog dialog = new DeleteBrainStormDialog(intent.getIntExtra("id", 0), this);
            dialog.show();
        });

        // Set up the listener for creating a PDF file
        createPDFLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if ( result != null && result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    saveToFile(uri);
                }
            } else {
                Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the PDF button
        MaterialButton pdfButton = findViewById(R.id.createPDFButton);
        pdfButton.setOnClickListener(v -> {
            createFile();
        });
    }

    /**
     * Asks the user for the place to save the PDF file. Creates an empty file and returns the URI of the file.
     */
    private void createFile(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "brainstorm.pdf");
        createPDFLauncher.launch(intent);
    }

    /**
     * Saves the brainstorm details to a PDF file.
     *
     * @param uri The URI of the file to save the PDF to.
     */
    private void saveToFile(Uri uri){
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) return;

            // Get all necessary data
            Intent intent = getIntent();
            String title = intent.getStringExtra("title");
            String date = intent.getStringExtra("date");
            String participants = intent.getStringExtra("participants");
            String details = intent.getStringExtra("details");

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            PdfFont font = PdfFontFactory.createFont("assets/fonts/HSESans-Regular.otf", PdfEncodings.IDENTITY_H);

            // The title page
            document.add(new Paragraph(title)
                    .setFont(font).setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(300));

            document.add(new Paragraph("Дата: " + date)
                    .setFont(font).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Участники: " + participants)
                    .setFont(font).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n\n"));

            // Process the brainstorm details
            String[] ideas = details.split("<div style='text-align: center;'><h3>");
            for (String ideaBlock : ideas) {
                if (ideaBlock.trim().isEmpty()) continue;

                // Add a new page for each idea
                pdfDocument.addNewPage();
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                String formattedHtml = ideaBlock;
                if (!formattedHtml.contains("<h1>")) {
                    formattedHtml = "<div style='text-align: center;'><h2>" + ideaBlock;
                }
                formattedHtml = formattedHtml.replace("</h3></div><div style='text-align: center;'>", "<br>");
                formattedHtml = formattedHtml.replace("\n", "<br>");
                formattedHtml = formattedHtml.replace("</div><br>", "</h2></div><p>");
                formattedHtml += "</p>";

                formattedHtml = "<style>p, span { font-size: 22px; text-align: justify; }</style>" + formattedHtml;

                // Log.d("HTML", formattedHtml);

                // Set up the converter properties
                ConverterProperties converterProperties = new ConverterProperties();
                FontProvider fontProvider = new FontProvider();
                fontProvider.addFont("assets/fonts/HSESans-Regular.otf");
                fontProvider.addFont("assets/fonts/HSESans-Bold.otf");
                converterProperties.setFontProvider(fontProvider);

                // Add the HTML content to the PDF
                List<IElement> elements = HtmlConverter.convertToElements(formattedHtml, converterProperties);
                for (IElement element : elements) {
                    document.add((IBlockElement) element);
                }
            }

            document.close();
            outputStream.close();
            Toast.makeText(this, "Файл успешно создан", Toast.LENGTH_SHORT).show();
            openFile(uri);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка записи данных в PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    /**
     * Called when an options item is selected.
     *
     * @param item The selected menu item.
     * @return true if the item is selected successfully.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}