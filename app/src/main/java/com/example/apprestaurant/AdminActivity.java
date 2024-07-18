package com.example.apprestaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.QuerySnapshot;
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
    private Spinner userSpinner, roleSpinner;
    private ArrayAdapter<String> userAdapter, roleAdapter;
    private ArrayList<String> userNames, userIds;
    private ArrayList<String> rolesList;

    private Uri categoryImageUri, itemImageUri;

    private static final String[] ROLES = {"user", "employee", "admin"};

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

        // Initialize user management components
        userSpinner = findViewById(R.id.user_spinner);
        roleSpinner = findViewById(R.id.role_spinner);

        userNames = new ArrayList<>();
        userIds = new ArrayList<>();
        rolesList = new ArrayList<>();

        userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userNames);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(userAdapter);

        roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rolesList);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        loadCategories();
        loadItems();
        loadUsers();
        loadRoles();

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

        Button setUserRoleButton = findViewById(R.id.set_user_role_button);
        EditText searchUserEditText = findViewById(R.id.search_user_edit_text);

        setUserRoleButton.setOnClickListener(v -> {
            String searchName = searchUserEditText.getText().toString().trim();
            int selectedIndex = userSpinner.getSelectedItemPosition();
            if (!TextUtils.isEmpty(searchName)) {
                setSelectedUserByName(searchName);
            } else if (selectedIndex != -1) {
                String selectedUserId = userIds.get(selectedIndex);
                String selectedUserRole = roleSpinner.getSelectedItem().toString();

                updateRole(selectedUserId, selectedUserRole);
            } else {
                Toast.makeText(this, "Please select a user or enter search text", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void setSelectedUserByName(String searchName) {
        for (int i = 0; i < userNames.size(); i++) {
            String userName = userNames.get(i);
            if (userName.toLowerCase().contains(searchName.toLowerCase())) {
                userSpinner.setSelection(i);
                return;
            }
        }
        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
    }

    private void updateRole(String userId, String role) {
        firestore.collection("users")
                .document(userId)
                .update("role", role)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User role updated successfully", Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error updating user role", Toast.LENGTH_SHORT).show());
    }

    private void loadCategories() {
        firestore.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> categoryIds = new ArrayList<>();
                        ArrayList<String> categoryNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String name = document.getString("name");
                            categoryIds.add(id);
                            categoryNames.add(name);
                        }
                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        categorySpinner.setAdapter(categoryAdapter);

                        ArrayAdapter<String> deleteCategoryAdapter = new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                        deleteCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        deleteCategorySpinner.setAdapter(deleteCategoryAdapter);
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadItems() {
        firestore.collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> itemNames = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            itemNames.add(name);
                        }
                        ArrayAdapter<String> deleteItemAdapter = new ArrayAdapter<>(AdminActivity.this, android.R.layout.simple_spinner_item, itemNames);
                        deleteItemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        deleteItemSpinner.setAdapter(deleteItemAdapter);
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUsers() {
        firestore.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userNames.clear();
                        userIds.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("username");
                            String role = document.getString("role");
                            String id = document.getId();
                            userNames.add(name + " - " + role);
                            userIds.add(id);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadRoles() {
        firestore.collection("roles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rolesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String roleName = document.getString("role");
                            rolesList.add(roleName);
                        }
                        roleAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(AdminActivity.this, "Failed to load roles", Toast.LENGTH_SHORT).show();
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
        String id = categoryIdEditText.getText().toString().trim();
        String name = categoryNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(id)) {
            categoryIdEditText.setError("Required");
            return;
        }

        Map<String, Object> category = new HashMap<>();
        category.put("name", name);

        firestore.collection("categories")
                .document(id)
                .set(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    categoryIdEditText.setText("");
                    categoryNameEditText.setText("");
                    categoryImageView.setImageDrawable(null);
                    loadCategories(); // Reîncarcă categoriile pentru a reflecta adăugarea noii categorii
                })
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Error adding category", Toast.LENGTH_SHORT).show());
    }

    private void addItem() {
        String name = itemNameEditText.getText().toString().trim();
        String price = itemPriceEditText.getText().toString().trim();
        String timing = itemTimingEditText.getText().toString().trim();
        String categoryId = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";
        String imageName = itemImageUri != null ? itemImageUri.getLastPathSegment() : "";

        if (TextUtils.isEmpty(name)) {
            itemNameEditText.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(price)) {
            itemPriceEditText.setError("Required");
            return;
        }

        if (TextUtils.isEmpty(timing)) {
            itemTimingEditText.setError("Required");
            return;
        }

        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("price", price);
        item.put("timing", timing);
        item.put("categoryId", categoryId);
        item.put("imageName", imageName);

        firestore.collection("items")
                .document(name)
                .set(item)
                .addOnSuccessListener(aVoid -> {
                    uploadItemImage(itemImageUri, name);
                    Toast.makeText(AdminActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    itemNameEditText.setText("");
                    itemPriceEditText.setText("");
                    itemTimingEditText.setText("");
                    itemImageView.setImageDrawable(null);
                    loadItems(); // Reîncarcă articolele pentru a reflecta adăugarea noului articol
                })
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Error adding item", Toast.LENGTH_SHORT).show());
    }

    private void deleteCategory() {
        String selectedCategory = deleteCategorySpinner.getSelectedItem() != null ? deleteCategorySpinner.getSelectedItem().toString() : "";

        firestore.collection("categories")
                .document(selectedCategory)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminActivity.this, "Category deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCategories(); // Reîncarcă categoriile pentru a reflecta ștergerea categoriei
                })
                .addOnFailureListener(e -> Toast.makeText(AdminActivity.this, "Error deleting category", Toast.LENGTH_SHORT).show());
    }

    private void deleteItem() {
        String selectedItem = deleteItemSpinner.getSelectedItem() != null ? deleteItemSpinner.getSelectedItem().toString() : "";

        firestore.collection("items")
                .whereEqualTo("name", selectedItem)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                        Toast.makeText(AdminActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                        loadItems(); // Reîncarcă articolele pentru a reflecta ștergerea articolului
                    } else {
                        Toast.makeText(AdminActivity.this, "Error deleting item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadItemImage(Uri imageUri, String itemName) {
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference().child("item_images/" + itemName);
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Successfully uploaded image
                    })
                    .addOnFailureListener(e -> {
                        // Failed to upload image
                    });
        }
    }
}
