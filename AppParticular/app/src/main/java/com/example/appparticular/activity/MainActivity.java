package com.example.appparticular.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.appparticular.R;
import com.example.appparticular.fragments.AlertasFragment;
import com.example.appparticular.fragments.InicioFragment;
import com.example.appparticular.fragments.PerfilFragment;
import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.example.appparticular.helper.UsuarioFirebase;
import com.example.appparticular.model.Alerta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private final static String NOTI = "1";
    private final List<Alerta> lista = new ArrayList<>();
    private DatabaseReference alertaRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        alertaRef = firebaseRef.child("alertas").child("usuarios").child(idUsuarioLogado);

        //Intent serviseIntent = new Intent();
        //startService(serviseIntent);

        //startService(new Intent(this, Servico.class));

        createNotificationChannel();

        ExampleRunnable runnable = new ExampleRunnable();
        new Thread(runnable).start();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("App Particular");
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //configurar o bottomNavigationView
        configuraBottomNavigation();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new InicioFragment()).commit();

    }

    private void configuraBottomNavigation() {
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottom_navigation);

        //faz as configurações iniciais
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(true);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(true);

        habilitarNavegacao(bottomNavigationViewEx);
    }

    @SuppressLint("NonConstantResourceId")
    private void habilitarNavegacao(BottomNavigationViewEx viewEx) {
        viewEx.setOnNavigationItemSelectedListener(menuItem -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()){
                case R.id.menu_inicio:
                    fragmentTransaction.replace(R.id.viewPager, new InicioFragment()).commit();
                    return true;
                case R.id.menu_alertas:
                    fragmentTransaction.replace(R.id.viewPager, new AlertasFragment()).commit();
                    return true;
                case R.id.menu_perfil:
                    fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;
            case R.id.menu_editar:
                startActivity(new Intent(getApplicationContext(), EditarPerfilActivity.class));
                break;
            case R.id.menu_vinculo:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                //Configurar o titulo e mensagem
                dialog.setTitle("Vínculo Hospitalar");
                dialog.setMessage("Cliando aqui, aparecerá hospitais ou asilos que você possa ter vínculo." +
                        " Confirma a ação?");

                dialog.setPositiveButton("Sim", (dialog1, which) -> startActivity(new Intent(getApplicationContext(), PesquisaActivity.class)));

                dialog.setNegativeButton("Não", null);

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificação";
            String description = "Verifique o estado do paciente!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTI, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService( NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }


    class ExampleRunnable implements Runnable{
        @Override
        public void run() {

            while(!Thread.currentThread().isInterrupted()){

                alertaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        lista.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            Alerta alerta = ds.getValue(Alerta.class);
                            lista.add(alerta);
                        }

                        Handler threadHandler = new Handler(Looper.getMainLooper());

                        threadHandler.post(() -> {
                            String numero = Integer.toString(lista.size());
                            Toast.makeText(MainActivity.this, numero, Toast.LENGTH_SHORT).show();

                            //notficação esta funcionando, o problema esta na recuperação da lista de alertas!!!
                            if(lista.size()>0){
                                //Toast.makeText(MainActivity.this, "TESTE", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), NOTI)
                                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                                        .setContentTitle("Alerta de alteração")
                                        .setContentText("Verifique o estado do paciente!")
                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                NotificationManagerCompat nm = NotificationManagerCompat.from(getBaseContext());
                                nm.notify(1, builder.build());
                            }

                        });
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                //mudar o tempo!!!
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}