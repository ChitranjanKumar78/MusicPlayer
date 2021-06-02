package com.chitranjank.co.musicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;

    ArrayList<Songs> arrayList;
    ArrayList<Songs> newarrayList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lst_songs);

        final EditText editText = findViewById(R.id.search_bar);

        arrayList = new ArrayList<>();

        final SongsAdapter adapter = new SongsAdapter(arrayList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, Player.class);
                if (editText.getText().toString().trim().isEmpty()) {
                    intent.putExtra("Title", arrayList.get(i).getTitle())
                            .putExtra("Uri", arrayList.get(i).getUri().toString())
                            .putExtra("Pos", i);
                } else {
                    intent.putExtra("Title", newarrayList.get(i).getTitle())
                            .putExtra("Uri", newarrayList.get(i).getUri().toString())
                            .putExtra("Pos", i);
                }
                startActivity(intent);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                newarrayList = new ArrayList<>();
                for (Songs song : arrayList) {
                    if (song.getTitle().toLowerCase().contains(editable)) {
                        newarrayList.add(song);
                    }
                }
                adapter.FilterList(newarrayList);
            }
        });

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        getSongsFromStorage();
                        listView.setAdapter(adapter);
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"Read Storage Permission Denied, Please Allow permission",Toast.LENGTH_LONG).show();
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    public static class SongsAdapter extends BaseAdapter {
        ArrayList<Songs> songs;

        public SongsAdapter(ArrayList<Songs> songs) {
            this.songs = songs;
        }

        public void FilterList(ArrayList<Songs> list) {
            songs = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_items, viewGroup, false);
            TextView textView = view.findViewById(R.id.s_n);
            textView.setText(songs.get(i).getTitle());
            return view;
        }
    }

    private void getSongsFromStorage() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            title = title.replace("_", " ");
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            Uri song = Uri.withAppendedPath(uri, id);

            arrayList.add(new Songs(title, song));
        }
        cursor.close();
    }
}