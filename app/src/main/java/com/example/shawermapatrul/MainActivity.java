package com.example.shawermapatrul;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ShawarmaAdapter adapter;
    private EditText etSearch;
    private Button btnSearch;
    private Spinner spinnerSort;
    private FloatingActionButton fabAdd;
    private TextView tvTotalCount;

    private List<Shawerma> shawarmaList;
    private int nextId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupSortSpinner();
        setupSearch();

        // Добавляем тестовые данные
        addTestData();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));
        updateTotalCount();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        spinnerSort = findViewById(R.id.spinner_sort);
        fabAdd = findViewById(R.id.fab_add);
        tvTotalCount = findViewById(R.id.tv_total_count);

        shawarmaList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new ShawarmaAdapter(
                shawarmaList,
                this::showAddEditDialog,
                this::showDeleteDialog
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSortSpinner() {
        String[] sortOptions = {"📌 По названию", "🏪 По заведению", "⭐ По оценке (высшие)",
                "⭐ По оценке (низшие)", "💰 По цене"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(spinnerAdapter);

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortShawarma(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sortShawarma(int sortType) {
        List<Shawerma> sortedList = new ArrayList<>(shawarmaList);

        switch (sortType) {
            case 0: // По названию
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getName));
                break;
            case 1: // По заведению
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getPlaceName));
                break;
            case 2: // По оценке (от высшей к низшей)
                Collections.sort(sortedList, (a, b) -> Integer.compare(b.getRating(), a.getRating()));
                break;
            case 3: // По оценке (от низшей к высшей)
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getRating));
                break;
            case 4: // По цене
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getPrice));
                break;
        }

        adapter.updateList(sortedList);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                searchShawarma(query);
            } else {
                adapter.updateList(shawarmaList);
            }
        });
    }

    private void searchShawarma(String query) {
        List<Shawerma> filteredList = new ArrayList<>();
        for (Shawerma shawarma : shawarmaList) {
            if (shawarma.getName().toLowerCase().contains(query.toLowerCase()) ||
                    shawarma.getPlaceName().toLowerCase().contains(query.toLowerCase()) ||
                    shawarma.getAddress().toLowerCase().contains(query.toLowerCase()) ||
                    shawarma.getCommet().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(shawarma);
            }
        }

        adapter.updateList(filteredList);

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "Ничего не найдено!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddEditDialog(Shawerma shawarma) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_shawarma, null);

        EditText etName = dialogView.findViewById(R.id.et_dialog_name);
        EditText etPlaceName = dialogView.findViewById(R.id.et_dialog_place_name);
        RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar);
        EditText etPrice = dialogView.findViewById(R.id.et_dialog_price);
        EditText etAddress = dialogView.findViewById(R.id.et_dialog_address);
        EditText etComment = dialogView.findViewById(R.id.et_dialog_comment);
        TextView tvRatingValue = dialogView.findViewById(R.id.tv_rating_value);

        ratingBar.setNumStars(10);
        ratingBar.setStepSize(1f);

        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) ->
                tvRatingValue.setText((int) rating + "/10")
        );

        if (shawarma != null) {
            etName.setText(shawarma.getName());
            etPlaceName.setText(shawarma.getPlaceName());
            ratingBar.setRating(shawarma.getRating());
            etPrice.setText(String.valueOf(shawarma.getPrice()));
            etAddress.setText(shawarma.getAddress());
            etComment.setText(shawarma.getCommet());
            tvRatingValue.setText(shawarma.getRating() + "/10");
        }

        builder.setTitle(shawarma == null ? "➕ Добавить шаурму" : "✏️ Редактировать шаурму")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String placeName = etPlaceName.getText().toString().trim();
                    int rating = (int) ratingBar.getRating();
                    String priceStr = etPrice.getText().toString().trim();
                    int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr);
                    String address = etAddress.getText().toString().trim();
                    String comment = etComment.getText().toString().trim();

                    if (name.isEmpty()) {
                        Toast.makeText(this, "Введите название шаурмы!", Toast.LENGTH_SHORT).show();
                    } else if (placeName.isEmpty()) {
                        Toast.makeText(this, "Введите название заведения!", Toast.LENGTH_SHORT).show();
                    } else if (rating == 0) {
                        Toast.makeText(this, "Поставьте оценку!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (shawarma == null) {
                            Shawerma newShawarma = new Shawerma(
                                    nextId++,
                                    name,
                                    placeName,
                                    rating,
                                    price,
                                    address.isEmpty() ? "Не указан" : address,
                                    comment.isEmpty() ? "Без комментария" : comment
                            );
                            shawarmaList.add(newShawarma);
                            Toast.makeText(this, "✅ Шаурма добавлена!", Toast.LENGTH_SHORT).show();
                        } else {
                            shawarma.setName(name);
                            shawarma.setPlaceName(placeName);
                            shawarma.setRating(rating);
                            shawarma.setPrice(price);
                            shawarma.setAddress(address.isEmpty() ? "Не указан" : address);
                            shawarma.setCommet(comment.isEmpty() ? "Без комментария" : comment);
                            Toast.makeText(this, "✅ Шаурма обновлена!", Toast.LENGTH_SHORT).show();
                        }
                        refreshList();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteDialog(Shawerma shawarma) {
        new AlertDialog.Builder(this)
                .setTitle("🗑️ Удалить шаурму")
                .setMessage("Вы уверены, что хотите удалить \"" + shawarma.getName() + "\" из " + shawarma.getPlaceName() + "?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    shawarmaList.remove(shawarma);
                    refreshList();
                    Toast.makeText(this, "✅ Шаурма удалена!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void refreshList() {
        int currentSort = spinnerSort.getSelectedItemPosition();
        sortShawarma(currentSort);
        updateTotalCount();
    }

    private void updateTotalCount() {
        tvTotalCount.setText("🌯 Всего шаурм: " + shawarmaList.size());
    }

    private void addTestData() {
        shawarmaList.add(new Shawerma(nextId++, "Классическая", "Шаверма №1", 9, 250, "ул. Ленина, 10", "Очень вкусно!"));
        shawarmaList.add(new Shawerma(nextId++, "Острая", "Шаурмичная у дома", 8, 280, "пр. Мира, 25", "Хороший соус"));
        shawarmaList.add(new Shawerma(nextId++, "С сыром", "Быстро и Вкусно", 10, 300, "ул. Гагарина, 5", "Лучшая в городе!"));
        shawarmaList.add(new Shawerma(nextId++, "Двойная", "Шаверма Кинг", 7, 350, "ул. Пушкина, 15", "Большая порция"));
        shawarmaList.add(new Shawerma(nextId++, "Куриная", "Лавка Шавермы", 9, 220, "ул. Советская, 42", "Недорого и сытно"));
        shawarmaList.add(new Shawerma(nextId++, "Говяжья", "Premium Shawarma", 10, 400, "пр. Невский, 88", "Премиум качество"));
        shawarmaList.add(new Shawerma(nextId++, "Вегетарианская", "Зеленая Шаверма", 6, 200, "ул. Цветочная, 7", "Для вегетарианцев"));
        shawarmaList.add(new Shawerma(nextId++, "Острая с халапеньо", "Мексиканская Шаверма", 8, 320, "ул. Солнечная, 12", "Очень остро!"));

        refreshList();
    }
}