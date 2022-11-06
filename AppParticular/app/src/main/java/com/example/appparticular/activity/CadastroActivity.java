package com.example.appparticular.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appparticular.R;
import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.example.appparticular.helper.UsuarioFirebase;
import com.example.appparticular.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private EditText editEmail, editSenha, editNome, editTelefone;
    private Button buttonCadastrar;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        buttonCadastrar.setOnClickListener(v -> {
            String textoNome = editNome.getText().toString();
            String textoEmail = editEmail.getText().toString();
            String textoSenha = editSenha.getText().toString();
            String textoTelefone = editTelefone.getText().toString();

            if(!textoNome.isEmpty()){
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        if(!textoTelefone.isEmpty()){
                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setNomeMinusculo(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            usuario.setComorbidades("Não informado");
                            usuario.setDataNascimento("Não informado");
                            usuario.setImportantes("Não informado");
                            usuario.setTelefone(textoTelefone);
                            cadastrar(usuario);
                        }else{
                            Toast.makeText(CadastroActivity.this, "Informe um telefone!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(CadastroActivity.this, "Informe uma senha!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CadastroActivity.this, "Informe o email!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(CadastroActivity.this, "Informe o nome!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cadastrar(Usuario usuario) {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                try{
                    String idUsuario = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    usuario.setId(idUsuario);
                    usuario.salvar();

                    //salvar dados no profile do firebse
                    UsuarioFirebase.atulizarNomeUsuario(usuario.getNome());

                    Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));

                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                String excecao;
                try{
                    throw Objects.requireNonNull(task.getException());
                }catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte!";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um email válido";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Esse email ja foi cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar ususário: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void inicializarComponentes(){
        editEmail = findViewById(R.id.editCadastroEmail);
        editSenha = findViewById(R.id.editCadastroSenha);
        editNome = findViewById(R.id.editCadastroNome);
        editTelefone = findViewById(R.id.editCadastroTelefone);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
    }
}