package com.example.android.backdropsapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.backdropsapp.Fragments.CategoriesFragment;
import com.example.android.backdropsapp.Fragments.CollectionsFragment;
import com.example.android.backdropsapp.Fragments.ExploreFragment;
import com.example.android.backdropsapp.Fragments.UploadFragment;
import com.example.android.backdropsapp.Services.UserService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static com.example.android.backdropsapp.Utilities.Constants.PICK_IMAGE_REQUEST;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView userPhoto;
    private Uri selectedImage;
    private ProgressDialog progress;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private DatabaseReference myRef;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupNavigationDrawer();
    }

    private void setupNavigationDrawer() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        prepareNavHeader(navigationView);

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_explore));
    }

    private void prepareNavHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        userPhoto = headerView.findViewById(R.id.userPhoto);
        TextView username = headerView.findViewById(R.id.username);
        TextView userEmail = headerView.findViewById(R.id.user_email);
        progress = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("Users");
        loadProfileImage();

        if (UserService.getInstance().getFirebaseAuth() != null) {
            String userNameFromEmail = UserService.getInstance().getFirebaseAuth().getCurrentUser().getEmail();
            int index = userNameFromEmail.indexOf('@');
            userNameFromEmail = userNameFromEmail.substring(0, index);
            username.setText("Hello, " + userNameFromEmail);
            userEmail.setText(UserService.getInstance().getFirebaseAuth().getCurrentUser().getEmail());
        }
    }

    private void loadProfileImage() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child(UserService.getInstance().getFirebaseAuth().getCurrentUser().getUid()).child("ImageUrl").getValue(String.class);
                if (value != null){
                    Picasso.with(getApplicationContext()).load(value).into(userPhoto);
                }else {
                    Picasso.with(getApplicationContext()).load(R.mipmap.nav_bar_icon).into(userPhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        fragment = null;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_explore) {
            // Handle the camera action
            fragment = new ExploreFragment();
            getSupportActionBar().setTitle("Explore");
        } else if (id == R.id.nav_collections) {
            fragment = new CollectionsFragment();
            getSupportActionBar().setTitle("Collections");
        } else if (id == R.id.nav_upload) {
            fragment = new UploadFragment();
            getSupportActionBar().setTitle("Uploads");
        } else if (id == R.id.nav_categories) {
            fragment = new CategoriesFragment();
            getSupportActionBar().setTitle("Categories");
        } else if (id == R.id.nav_sign_out) {
            Logout();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction().replace(R.id.screenArea, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Logout() {
        UserService.getInstance().getFirebaseAuth().signOut();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
        startActivity(loginIntent);
    }

    public void fileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            Picasso.with(this).load(selectedImage).into(userPhoto);
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(HomeActivity.this, "Upload In Progress", Toast.LENGTH_LONG).show();
            } else {
                showDialog();
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExtension(selectedImage));

        uploadTask = reference.putFile(selectedImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String id = UserService.getInstance().getFirebaseAuth().getCurrentUser().getUid();
                        if (downloadUrl != null && id != null) {
                            myRef.child(id).child("ImageUrl").setValue(downloadUrl.toString());
                        }
                        hideDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                });

    }

    private String getExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void showDialog() {
        progress.setMessage("Loading....");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void hideDialog() {
        progress.dismiss();
    }

    @Override
    public void onBackPressed() {
        fragment = getSupportFragmentManager().findFragmentById(R.id.screenArea);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment instanceof ExploreFragment) {
                super.onBackPressed();
            }else if (fragment instanceof CollectionsFragment) {
                showHome();
            }else if (fragment instanceof UploadFragment){
                showHome();
            }else if (fragment instanceof CategoriesFragment) {
                showHome();
            }else if (!(fragment instanceof OnBackPressedListener) || !((OnBackPressedListener)fragment).onBackPressed()){
                super.onBackPressed();
            }
        }
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private void showHome(){
        fragment = new ExploreFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.screenArea, fragment).commit();
        getSupportActionBar().setTitle("Explore");
    }
}