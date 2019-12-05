package com.example.gasradar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class GasInformation extends AppCompatActivity {
    TextView GasTitle;
    TextView GasAddress;
    TextView GasNumber;
    TextView GasRating;
    TextView GasRegular;
    TextView GasMidgrade;
    TextView GasPremium;
    TextView GasDiesel;
    ImageView GasLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasinformation);

        GasTitle = findViewById(R.id.GasTitle);
        GasAddress = findViewById(R.id.GasAddress);
        GasNumber = findViewById(R.id.GasNumber);
        GasRating = findViewById(R.id.gasRating);
        GasRegular = findViewById(R.id.GasRegular);
        GasMidgrade = findViewById(R.id.gasMidgrade);
        GasPremium = findViewById(R.id.gasPremium);
        GasDiesel = findViewById(R.id.gasDiesel);
        GasLogo = findViewById(R.id.GasLogo);

        Intent intent = getIntent();
        String receivedGasName =  intent.getStringExtra("name");
        String receivedGasAddress = intent.getStringExtra("vicinity");
        String receivedGasRating = intent.getStringExtra("rating");
        String receivedGasNumber = intent.getStringExtra("phonenumber");
        String receivedGasMidgrade = intent.getStringExtra("midgrade");
        String receivedGasRegular = intent.getStringExtra("regular");
        String receivedGasPremium = intent.getStringExtra("premium");
        String receivedGasDiesel = intent.getStringExtra("diesel");
        String receivedGasLogo = intent.getStringExtra("logo");


        GasTitle.setText(receivedGasName);
        GasAddress.setText(receivedGasAddress);
        GasRating.setText(receivedGasRating);
        GasNumber.setText(receivedGasNumber);
        GasRegular.setText(receivedGasRegular);
        GasMidgrade.setText(receivedGasMidgrade);
        GasPremium.setText(receivedGasPremium);
        GasDiesel.setText(receivedGasDiesel);
        Picasso.get().load(receivedGasLogo).into(GasLogo);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
