package com.example.appparticular.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appparticular.R;
import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.example.appparticular.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView textCadastrar;
    private Button buttonEntrar;
    private EditText campoEmail, campoSenha;
    private ProgressBar progressBar;

    private static FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();

        textCadastrar.setOnClickListener(this::abrirCadastro);

        progressBar.setVisibility(View.GONE);
        buttonEntrar.setOnClickListener(v -> {
            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if(!email.isEmpty()){
                if(!senha.isEmpty()){
                    usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    validarLogin(usuario);
                }else{
                    Toast.makeText(LoginActivity.this, "Informe a senha", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LoginActivity.this, "Informe o email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void validarLogin(Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        progressBar.setVisibility(View.VISIBLE);
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }else{
                        progressBar.setVisibility(View.GONE);
                        String excecao;
                        try{
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthInvalidCredentialsException e){
                            excecao = "Email e senha não correspondem a um usuário cadastrado!";
                        }catch (FirebaseAuthInvalidUserException e){
                            excecao = "Usuário não está cadastrado.";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar ususário: " + e .getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void inicializarComponentes(){
        textCadastrar = findViewById(R.id.textCadastrar);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        buttonEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressLogin);
    }

    private void abrirCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }
}