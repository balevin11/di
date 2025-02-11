package com.example.proyectdi.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.proyectdi.adapters.GamesAdapter;
import com.example.proyectdi.R;
import com.example.proyectdi.viewmodels.DashboardViewModel;
import com.example.proyectdi.databinding.ActivityDashboardBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {
    private GamesAdapter gamesAdapter;
    private DashboardViewModel dashboardViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mostrar activity
        super.onCreate(savedInstanceState);
        ActivityDashboardBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        // Configuración del modo oscuro basado en SharedPreferences
        SharedPreferences sharedPrefs = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
        boolean darkModes = sharedPrefs.getBoolean("darkMode", false);
        if (darkModes) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Button button = binding.logout;

        // Inicializar el adapter con un listener que lanza DetailsActivity
        gamesAdapter = new GamesAdapter(new ArrayList<>(), (game, position) -> {
            // Crear el Intent para lanzar DetailsActivity
            Intent intent = new Intent(DashboardActivity.this, DetailsActivity.class);
            intent.putExtra("titulo", game.getTitulo());
            intent.putExtra("descripcion", game.getDescripcion());
            intent.putExtra("imagen", game.getImagen());
            intent.putExtra("gameIndex", position);
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(gamesAdapter);

        // Observa el LiveData para actualizar la lista de juegos
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.getGamesLiveData().observe(this, games -> {
            if (games != null) {
                gamesAdapter.setGames(games); // Actualiza la lista del RecyclerView
            } else {
                Toast.makeText(DashboardActivity.this, "Failed to load games.", Toast.LENGTH_SHORT).show();
            }
        });

        //cuando se pulse el boton logout cerrar la actividad y la sesion
        button.setOnClickListener(v -> {
            dashboardViewModel.logout();
            Toast.makeText(DashboardActivity.this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Acción para el botón de favoritos
        FloatingActionButton fav = binding.favorites;
        fav.setOnClickListener(view -> {
            Intent intent = new Intent(DashboardActivity.this, FavouritesActivity.class);
            startActivity(intent);
        });

        // Alterna el modo oscuro y recrea la actividad para aplicar el cambio
        FloatingActionButton darkMode = binding.darkMode;
        darkMode.setOnClickListener(view -> {
            // si darkmode está activado
            SharedPreferences sharedPref = getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
            boolean isDarkMode = sharedPref.getBoolean("darkMode", false);
            SharedPreferences.Editor editors = sharedPref.edit();
            editors.putBoolean("darkMode", !isDarkMode);
            editors.apply();
            recreate();
        });

    }


}