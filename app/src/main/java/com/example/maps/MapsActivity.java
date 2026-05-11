package com.example.maps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps.databinding.ActivityMapsBinding;

/**
 * Projet développé par Mohamed Douassi
 * Cette activité gère l'affichage de Google Maps et la localisation en temps réel.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Marker currentMarker; // Un seul marker pour éviter la pollution visuelle
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuration du View Binding pour Mohamed Douassi
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialisation du fragment de la carte
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialisation du service de localisation
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Méthode appelée quand la carte est prête.
     * Mohamed Douassi : Configuration initiale et demande de permissions.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Position par défaut : Sydney (au cas où la localisation échoue au début)
        LatLng defaultPos = new LatLng(-34, 151);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(defaultPos));
        Toast.makeText(this, "Carte prête - Mohamed Douassi", Toast.LENGTH_SHORT).show();

        // Vérification des permissions au runtime
        checkLocationPermissionAndStartUpdates();
    }

    /**
     * Vérifie les permissions et lance les mises à jour de position.
     */
    private void checkLocationPermissionAndStartUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Demander la permission si non accordée
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            // Permission déjà accordée, démarrer le suivi
            startTrackingLocation();
        }
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000, // minTime = 1 seconde
                    10,   // minDistance = 10 mètres pour plus de réactivité
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            updateMapWithNewLocation(location);
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            // Mohamed Douassi : Alerter si le GPS est éteint
                            buildAlertMessageNoGps();
                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {}

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}
                    }
            );
        }
    }

    /**
     * Met à jour le marker unique et la caméra.
     * Optimisé par Mohamed Douassi.
     */
    private void updateMapWithNewLocation(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());

        // Affichage des coordonnées (Toast de debug)
        Toast.makeText(getApplicationContext(), "Pos: " + pos.latitude + ", " + pos.longitude, Toast.LENGTH_SHORT).show();

        if (currentMarker == null) {
            // Créer le marker s'il n'existe pas
            currentMarker = mMap.addMarker(new MarkerOptions().position(pos).title("Ma Position - Mohamed Douassi"));
        } else {
            // Déplacer le marker existant
            currentMarker.setPosition(pos);
        }

        // Animation de la caméra fluide
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
    }

    /**
     * Affiche un message si la localisation est désactivée sur l'appareil.
     */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Votre GPS semble désactivé, voulez-vous l'activer ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée - Mohamed Douassi", Toast.LENGTH_SHORT).show();
                startTrackingLocation();
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_LONG).show();
            }
        }
    }
}
