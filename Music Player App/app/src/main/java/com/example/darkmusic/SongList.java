package com.example.darkmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.util.ArrayList;

public class SongList extends AppCompatActivity {
    ListView listView;
    String[] items;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);
        listView = findViewById(R.id.listView);
        RuntimePermission();
    }
    public void RuntimePermission()
    {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                         Displaysongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    public ArrayList<File> FindSongs(File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for(File singlefile: files){
            if(singlefile.isDirectory() && !singlefile.isHidden())
            {
                arrayList.addAll(FindSongs(singlefile));
            }
            else {
                if(singlefile.getName().endsWith(".mp3") || singlefile.getName().endsWith(".wav"))
                {
                    arrayList.add(singlefile);
                }
            }
        }
        return arrayList;
    }
    void Displaysongs()
    {
        final ArrayList<File> mysongs = FindSongs(Environment.getExternalStorageDirectory());
        items = new String[mysongs.size()];
        for(int i=0;i<mysongs.size();i++){
            items[i] = mysongs.get(i).getName().toString().replace(".mp3","").replace(".wav","");
        }
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songTitle = (String) listView.getItemAtPosition(i);
                startActivity(new Intent(getApplicationContext(),MainActivity2.class)
                .putExtra("songsName",mysongs)
                .putExtra("songTitle",songTitle)
                .putExtra("position",i));
            }
        });
    }
}