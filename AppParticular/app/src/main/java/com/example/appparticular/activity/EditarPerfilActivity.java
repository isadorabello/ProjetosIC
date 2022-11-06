package com.example.appparticular.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appparticular.R;
import com.example.appparticular.helper.ConfiguracaoFirebase;
import com.example.appparticular.helper.UsuarioFirebase;
import com.example.appparticular.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editData, editComorbidade, editInformacao, editTelefone;
    private CircleImageView imagePerfil;
    private TextView textAlterarFoto;
    private Button buttonSalvar;
    private String idUsuario;

    private Usuario usuarioLogado;
    private final StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //recuperar os dados do usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editNome.setText(usuarioPerfil.getDisplayName());
        editEmail.setText(usuarioPerfil.getEmail());

        Uri uri = usuarioPerfil.getPhotoUrl();
        if (uri != null) {
            Glide.with(EditarPerfilActivity.this).load(uri).into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.perfil);
        }

        recuperarDados();

        buttonSalvar.setOnClickListener(v -> {

            String nomeAtualizado = editNome.getText().toString();
            String telefoneAtualizado = editTelefone.getText().toString();
            String comorbidadeAtualizada = editComorbidade.getText().toString();
            String dataAtualizada = editData.getText().toString();
            String informacaoAtualizada = editInformacao.getText().toString();


            Usuario usuario = UsuarioFirebase.getDadosUsuarioLogado();

            if (!nomeAtualizado.isEmpty()) {
                if (!telefoneAtualizado.isEmpty()) {
                    if (!dataAtualizada.isEmpty()) {

                        if(!comorbidadeAtualizada.isEmpty()){
                            usuario.setComorbidades(comorbidadeAtualizada);
                        }else{
                            usuario.setComorbidades("Não informado");
                        }
                        if(!informacaoAtualizada.isEmpty()){
                            usuario.setImportantes(informacaoAtualizada);
                        }else{
                            usuario.setImportantes("Não informado");
                        }

                        //atualizar nome no perfil
                        UsuarioFirebase.atulizarNomeUsuario(nomeAtualizado);

                        //atualizar nome no banco de dados

                        usuario.setNome(nomeAtualizado);
                        usuario.setNomeMinusculo(nomeAtualizado.toLowerCase());
                        usuario.setTelefone(telefoneAtualizado);
                        usuario.setDataNascimento(dataAtualizada);
                        usuario.atualizar();
                        finish();
                    } else {
                        Toast.makeText(EditarPerfilActivity.this, "Informe a data de nascimento", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditarPerfilActivity.this, "Informe o telefone de contato", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(EditarPerfilActivity.this, "Preencha o campo nome", Toast.LENGTH_SHORT).show();
            }

        });

        // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bitmap imagem;

                        try {

                            //seleção apenas da galeria
                            assert data != null;
                            Uri localImagemSelecionada = data.getData();
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                            //caso tenha sido escolhida uma imagem
                            if (imagem != null) {
                                imagePerfil.setImageBitmap(imagem);
                            }

                            //recuperar dados da imagem no firebase
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            assert imagem != null;
                            imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] dadosImagem = baos.toByteArray();

                            //salvar imagem no firebase
                            final StorageReference imageRef = storageRef.child("imagens").child("perfil").child(idUsuario + ".jpeg");

                            UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                            uploadTask.addOnFailureListener(e -> Toast.makeText(this, "Erro no upload", Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(taskSnapshot -> {
                                        imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                            Uri url = task.getResult();
                                            atualizarFoto(url);
                                        });
                                        Toast.makeText(this, "Sucesso no upload", Toast.LENGTH_SHORT).show();
                                    });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        textAlterarFoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager()) != null) {
                someActivityResultLauncher.launch(i);
            }
        });
    }

    private void atualizarFoto (Uri url){
        //arualizar foto no perfil
        UsuarioFirebase.atulizarFotoUsuario(url);

        //atualizar foto no firebase
        usuarioLogado.setCaminhoFoto(url.toString());
        usuarioLogado.atualizarFoto();

        Toast.makeText(this, "Sua foto foi atualizada", Toast.LENGTH_SHORT).show();
    }

    private void recuperarDados() {
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child("pacientes").child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Usuario usuario = snapshot.getValue(Usuario.class);
                assert usuario != null;
                editComorbidade.setText(usuario.getComorbidades());
                editData.setText(usuario.getDataNascimento());
                editInformacao.setText(usuario.getImportantes());
                editTelefone.setText(usuario.getTelefone());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editEmail.setFocusable(false);
        editComorbidade = findViewById(R.id.editComorbidade);
        editInformacao = findViewById(R.id.editInfo);
        editData = findViewById(R.id.editDataNasc);
        editTelefone = findViewById(R.id.editTelefone);
        imagePerfil = findViewById(R.id.imagePerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        buttonSalvar = findViewById(R.id.buttonSalvar);
    }

    @Override
    public boolean onSupportNavigateUp () {
        finish();
        return false;
    }
}