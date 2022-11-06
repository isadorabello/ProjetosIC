package com.example.appgerenciador.activity;

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
import com.example.appgerenciador.R;
import com.example.appgerenciador.helper.ConfiguracaoFirebase;
import com.example.appgerenciador.helper.UsuarioFirebase;
import com.example.appgerenciador.model.Paciente;
import com.google.firebase.auth.FirebaseAuth;
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

public class EditarPacienteActivity extends AppCompatActivity {

    private EditText editNome, editComorbidade, editDataN, editDataE, editAnotacoes, editFicha;
    private CircleImageView imagePerfil;
    private TextView textAlterarFoto;
    private Button buttonSalvar;
    private String idP;
    private String urlImagem;

    private final Paciente paciente = new Paciente();
    private final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private final DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
    private final StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_paciente);

        //configurar a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Paciente");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        String idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            idP = bundle.getString("id");
        }

        paciente.setIdPaciente(idP);
        paciente.setIdHospital(idUsuarioLogado);

        recuperarDados();

        buttonSalvar.setOnClickListener(v -> {

            String nomeAtualizado = editNome.getText().toString();
            String fichaAtualizada = editFicha.getText().toString();
            String comorbidadeAtualizada = editComorbidade.getText().toString();
            String dataNAtualizada = editDataN.getText().toString();
            String dataAAtualizada = editDataE.getText().toString();
            String anotacaoAtualizada = editAnotacoes.getText().toString();

            if(!nomeAtualizado.isEmpty()){
                if(!dataNAtualizada.isEmpty()){
                    if (!fichaAtualizada.isEmpty()){

                        paciente.setNomePaciente(nomeAtualizado);
                        paciente.setNomeMinusculo(nomeAtualizado.toLowerCase());
                        paciente.setDataNascimento(dataNAtualizada);
                        paciente.setFicha(fichaAtualizada);
                        paciente.setCaminhoFoto(urlImagem);

                        if (!comorbidadeAtualizada.isEmpty()){
                            paciente.setComorbidades(comorbidadeAtualizada);
                        }else {
                            paciente.setComorbidades("Não informado");
                        }

                        if (!anotacaoAtualizada.isEmpty()){
                            paciente.setAnotacoes(anotacaoAtualizada);
                        }else {
                            paciente.setAnotacoes("Não informado");
                        }

                        if (!dataAAtualizada.isEmpty()){
                            paciente.setDataEntrada(dataAAtualizada);
                        }else {
                            paciente.setDataEntrada("Não informado");
                        }

                        paciente.atualizar();
                        finish();
                    }else {
                        Toast.makeText(EditarPacienteActivity.this, "Informe a ficha médica do paciente", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(EditarPacienteActivity.this, "Informe a data de nascimento do paciente", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(EditarPacienteActivity.this, "Informe o nome do paciente", Toast.LENGTH_SHORT).show();
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
                            final StorageReference imageRef = storageRef.child("imagens").child("perfil").child("Hospitais/Asilos")
                                    .child(Objects.requireNonNull(Objects.requireNonNull(autenticacao.getCurrentUser())
                                            .getDisplayName())).child(idP + ".jpeg");

                            UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                            uploadTask.addOnFailureListener(e -> Toast.makeText(this, "Erro no upload", Toast.LENGTH_SHORT).show())
                                    .addOnSuccessListener(taskSnapshot -> {
                                        imageRef.getDownloadUrl().addOnCompleteListener(task -> {
                                            Uri url = task.getResult();
                                            assert url != null;
                                            //Toast.makeText(EditarPacienteActivity.this, url.toString(), Toast.LENGTH_SHORT).show();
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

    private void atualizarFoto(Uri url) {
        paciente.setCaminhoFoto(url.toString());
        paciente.atualizarFoto();
        Toast.makeText(EditarPacienteActivity.this, "A foto foi atualizada", Toast.LENGTH_SHORT).show();
    }

    private void recuperarDados() {
        DatabaseReference usuarioRef = firebaseRef.child("paciente_hospital")
                .child(Objects.requireNonNull(autenticacao.getCurrentUser()).getUid()).child(idP);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Paciente paciente = snapshot.getValue(Paciente.class);
                assert paciente != null;
                editNome.setText(paciente.getNomePaciente());
                editDataN.setText(paciente.getDataNascimento());
                editComorbidade.setText(paciente.getComorbidades());
                editAnotacoes.setText(paciente.getAnotacoes());
                editDataE.setText(paciente.getDataEntrada());
                editFicha.setText(paciente.getFicha());
                urlImagem = paciente.getCaminhoFoto();

                alterarFoto();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void alterarFoto() {
        String uri = urlImagem;
        if (uri != null) {
            Glide.with(EditarPacienteActivity.this).load(uri).into(imagePerfil);
        }else{
            imagePerfil.setImageResource(R.drawable.perfil);
        }
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.editNomeP);
        editComorbidade = findViewById(R.id.editComorbidade);
        editDataE = findViewById(R.id.editDataE);
        editAnotacoes = findViewById(R.id.editAnotacoes);
        editFicha = findViewById(R.id.editFicha);
        editDataN = findViewById(R.id.editDataN);
        imagePerfil = findViewById(R.id.imagePerfilP);
        textAlterarFoto = findViewById(R.id.textAlterarFotoPaciente);
        buttonSalvar = findViewById(R.id.buttonSalvarPaciente);
    }

    @Override
    public boolean onSupportNavigateUp () {
        finish();
        return false;
    }
}