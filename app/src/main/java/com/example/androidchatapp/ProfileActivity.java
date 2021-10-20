package com.example.androidchatapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private Button btnSignOut, btnUploadImage;
    private ImageView ivProfile;
    private Uri imagePath;
    FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorageInstance = FirebaseStorage.getInstance();
    FirebaseDatabase firebaseDatabaseInstance = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        ivProfile = findViewById(R.id.ivProfile);

        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuthInstance.signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
                finish();
            }
        });

        ActivityResultLauncher<Intent> imageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagePath = result.getData().getData();
                        getImageInImageView();
                    }
                }
            }
        );

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imageIntent = new Intent(Intent.ACTION_PICK);
                imageIntent.setType("image/*");
                imageActivityResultLauncher.launch(imageIntent);
            }
        });
    }

    public void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ivProfile.setImageBitmap(bitmap);
    }

    public void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        firebaseStorageInstance.getReference("images/"+ UUID.randomUUID().toString()).putFile(imagePath)
        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                updateProfilePicture(task.getResult().toString());
                            }
                        }
                    });
                    Toast.makeText(ProfileActivity.this, "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = 100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                progressDialog.setMessage(" Uploaded " + (int)progressPercentage + "%");
            }
        });
    }


    private void updateProfilePicture(String url) {
        firebaseDatabaseInstance.getReference("user/" + firebaseAuthInstance.getCurrentUser()
                .getUid() + "/profilePicture").setValue(url);
    }
}

















