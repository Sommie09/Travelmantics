package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrinterId;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class InsertActivity extends AppCompatActivity {
    public static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;
    TravelDeal deal;
    Button uploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        txtTitle = findViewById(R.id.title);
        txtPrice = findViewById(R.id.textPrice);
        txtDescription = findViewById(R.id.description);
        uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"),  PICTURE_RESULT);
            }
        });

        FirebaseUtil.openFbReference("traveldeals", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if(deal==null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);

        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_deal).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enabledText(true);
        }else{
            menu.findItem(R.id.delete_deal).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enabledText(false );
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                clean();
                backToList();
                return true;
            case R.id.delete_deal:
                deleteDeal();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                backToList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());

        if(deal.getId() == null){
            mDatabaseReference.push().setValue(deal);
        }else{
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

        mDatabaseReference.push().setValue(deal);
    }

    public void deleteDeal(){
        if(deal == null){
            Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
    }

    public void backToList(){
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void enabledText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICTURE_RESULT && resultCode ==RESULT_OK){
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());

            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final Uri downloadedUrl = uri;
                            deal.setImageUrl(downloadedUrl.toString());
                        }
                    });
                }
            });
        }
    }
}