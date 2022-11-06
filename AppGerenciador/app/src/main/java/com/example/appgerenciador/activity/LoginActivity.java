package com.example.appgerenciador.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appgerenciador.R;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextView textCadastrar;
    private EditText editEmail, editSenha;
    private Button buttonEntrar;
    
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        textCadastrar.setOnClickListener(this::abrirCadastro);

        buttonEntrar.setOnClickListener(v -> {
            String campoEmail = editEmail.getText().toString();
            String campoSenha = editSenha.getText().toString();

            if(!campoEmail.isEmpty()){
                if(!campoSenha.isEmpty()){
                    usuario = new Usuario();
                    usuario.setEmail(campoEmail);
                    usuario.setSenha(campoSenha);
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
        if(autenticacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void validarLogin(Usuario usuario) {
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }else{
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
        buttonEntrar = findViewById(R.id.buttonLogin);
        editEmail = findViewById(R.id.editLoginEmail);
        editSenha = findViewById(R.id.editLoginSenha);
    }

    private void abrirCadastro(View view){
        startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
    }
}