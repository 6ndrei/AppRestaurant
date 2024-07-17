package com.example.apprestaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private static final int PICK_CATEGORY_IMAGE_REQUEST = 1;
    private static final int PICK_ITEM_IMAGE_REQUEST = 2;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    private EditText categoryIdEditText, categoryNameEditText;
    private EditText itemNameEditText, itemPriceEditText, itemTimingEditText;
    private Button addCategoryButton, addItemButton, selectCategoryImageButton, selectItemImageButton, deleteCategoryButton, deleteItemButton, logOutButton;
    private ImageView categoryImageView, itemImageView;
    private Spinner categorySpinner, deleteCategorySpinner, deleteItemSpinner;

    private ArrayList<String> categoryIds;
    private ArrayList<String> categoryNames;
    private ArrayList<String> itemNames;
    private ArrayAdapter<String> categoryAdapter, deleteCategoryAdapter, deleteItemAdapter;

    private Uri categoryImageUri, itemImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        categoryIdEditText = findViewById(R.id.category_id_edit_text);
        categoryNameEditText = findViewById(R.id.category_name_edit_text);
        itemNameEditText = findViewById(R.id.item_name_edit_text);
        itemPriceEditText = findViewById(R.id.item_price_edit_text);
        itemTimingEditText = findViewById(R.id.item_timing_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        deleteCategorySpinner = findViewById(R.id.delete_category_spinner);
        deleteItemSpinner = findViewById(R.id.delete_item_spinner);

        categoryImageView = findViewById(R.id.category_image_view);
        itemImageView = findViewById(R.id.item_image_view);

        addCategoryButton = findViewById(R.id.add_category_button);
        addItemButton = findViewById(R.id.add_item_button);
        selectCategoryImageButton = findViewById(R.id.select_category_image_button);
        selectItemImageButton = findViewById(R.id.select_item_image_button);
        deleteCategoryButton = findViewById(R.id.delete_category_button);
        deleteItemButton = findViewById(R.id.delete_item_button);
        logOutButton = findViewById(R.id.sign_out_admin);

        categoryIds = new ArrayList<>();
        categoryNames = new ArrayList<>();
        itemNames = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        deleteCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
        deleteCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteCategorySpinner.setAdapter(deleteCategoryAdapter);
        deleteItemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemNames);
        deleteItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteItemSpinner.setAdapter(deleteItemAdapter);

        loadCategories();
        loadItems();

        selectCategoryImageButton.setOnClickListener(v -> openFileChooser(PICK_CATEGORY_IMAGE_REQUEST));
        selectItemImageButton.setOnClickListener(v -> openFileChooser(PICK_ITEM_IMAGE_REQUEST));

        addCategoryButton.setOnClickListener(v -> addCategory());
        addItemButton.setOnClickListener(v -> addItem());

        deleteCategoryButton.setOnClickListener(v -> deleteCategory());
        deleteItemButton.setOnClickListener(v -> deleteItem());

        logOutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadCategories() {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryIds.clear();
                        categoryNames.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            categoryIds.add(id);
                            categoryNames.add(name);
                        }
                        categoryAdapter.notifyDataSetChanged();
                        deleteCategoryAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadItems() {
        firestore.collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemNames.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            itemNames.add(name);
                        }
                        deleteItemAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if (requestCode == PICK_CATEGORY_IMAGE_REQUEST) {
                categoryImageUri = imageUri;
                categoryImageView.setImageURI(categoryImageUri);
            } else if (requestCode == PICK_ITEM_IMAGE_REQUEST) {
                itemImageUri = imageUri;
                itemImageView.setImageURI(itemImageUri);
            }
        }
    }

    private void addCategory() {
        String docId = categoryIdEditText.getText().toString();
        String name = categoryNameEditText.getText().toString();

        if (TextUtils.isEmpty(docId) || TextUtils.isEmpty(name) || categoryImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = storage.getReference("categories").child(docId);
        storageReference.putFile(categoryImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Map<String, Object> category = new HashMap<>();
                    category.put("name", name);
                    category.put("image", imageUrl);

                    firestore.collection("categories")
                            .document(docId)
                            .set(category)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                                loadCategories();
                                clearCategoryFields();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error adding category", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show());
    }

    private void addItem() {
        String name = itemNameEditText.getText().toString();
        String price = itemPriceEditText.getText().toString();
        String timing = itemTimingEditText.getText().toString();
        String categoryId = categoryIds.get(categorySpinner.getSelectedItemPosition());

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(timing) || itemImageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = storage.getReference("items").child(name);
        storageReference.putFile(itemImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", name);
                    item.put("image", imageUrl);
                    item.put("price", price);
                    item.put("timing", timing);
                    item.put("categoryId", categoryId);

                    firestore.collection("items")
                            .add(item)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                                loadItems();
                                clearItemFields();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error adding item", Toast.LENGTH_SHORT).show());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show());
    }

    private void clearCategoryFields() {
        categoryIdEditText.setText("");
        categoryNameEditText.setText("");
        categoryImageView.setImageURI(null);
        categoryImageUri = null;
    }

    private void clearItemFields() {
        itemNameEditText.setText("");
        itemPriceEditText.setText("");
        itemTimingEditText.setText("");
        itemImageView.setImageURI(null);
        itemImageUri = null;
    }

    private void deleteCategory() {
        String selectedCategoryId = categoryIds.get(deleteCategorySpinner.getSelectedItemPosition());

        firestore.collection("categories")
                .document(selectedCategoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String imageUrl = documentSnapshot.getString("image");
                        if (imageUrl != null) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                            storageReference.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        deleteCategoryDocument(selectedCategoryId);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error deleting image from storage", Toast.LENGTH_SHORT).show());
                        } else {
                            deleteCategoryDocument(selectedCategoryId);
                        }
                    } else {
                        Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching category details", Toast.LENGTH_SHORT).show());
    }

    private void deleteCategoryDocument(String categoryId) {
        firestore.collection("categories")
                .document(categoryId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                    loadCategories();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting category", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem() {
        String selectedItemName = itemNames.get(deleteItemSpinner.getSelectedItemPosition());

        firestore.collection("items")
                .whereEqualTo("name", selectedItemName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String docId = task.getResult().getDocuments().get(0).getId();
                        String imageUrl = task.getResult().getDocuments().get(0).getString("image");

                        if (imageUrl != null) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                            storageReference.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        deleteItemDocument(docId);
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error deleting image from storage", Toast.LENGTH_SHORT).show());
                        } else {
                            deleteItemDocument(docId);
                        }
                    } else {
                        Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteItemDocument(String itemId) {
        firestore.collection("items")
                .document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
                    loadItems();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show());
    }
}

