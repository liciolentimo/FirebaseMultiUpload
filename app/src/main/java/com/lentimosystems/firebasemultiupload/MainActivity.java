package com.lentimosystems.firebasemultiupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int RESULT_IMAGE = 10;
    private Button btnUpload;
    private RecyclerView mRecyclerView;
    private List<String> fileNameList;
    private List<String> fileDoneList;
    private UploadListAdapter mAdapter;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUpload = findViewById(R.id.btnUpload);
        mRecyclerView = findViewById(R.id.recycler_upload);
        fileNameList = new ArrayList<>();
        fileDoneList = new ArrayList<>();
        mAdapter = new UploadListAdapter(fileNameList,fileDoneList);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        mStorageReference = FirebaseStorage.getInstance().getReference();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null){
                //Toast.makeText(this, "Selected Multiple Files", Toast.LENGTH_SHORT).show();
                int totalItems = data.getClipData().getItemCount();
                for (int i = 0;i < totalItems; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    String fileName = getFileName(fileUri);
                    fileNameList.add(fileName);
                    fileDoneList.add("Uploading");
                    mAdapter.notifyDataSetChanged();

                    StorageReference fileToUpload = mStorageReference.child("Images").child(fileName);
                    final int final_i = i;
                    fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                            fileDoneList.remove(final_i);
                            fileDoneList.add(final_i,"Done");

                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else if (data.getData() != null){
                Toast.makeText(this, "Selected Single File", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getFileName(Uri uri){
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}