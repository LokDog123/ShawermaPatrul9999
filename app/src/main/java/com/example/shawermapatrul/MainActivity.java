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
    private boolean firstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация
        DataManager.init(this);

        initViews();
        setupRecyclerView();
        setupSortSpinner();
        setupSearch();

        // Загружаем данные
        shawarmaList = DataManager.getAllRecords();

        // Только при первом запуске добавляем тестовые
        if (firstStart && shawarmaList.isEmpty()) {
            addTestData();
            firstStart = false;
        }

        refreshList();

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        // Показываем сколько записей
        Toast.makeText(this, "Загружено: " + shawarmaList.size() + " записей", Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        spinnerSort = findViewById(R.id.spinner_sort);
        fabAdd = findViewById(R.id.fab_add);
        tvTotalCount = findViewById(R.id.tv_total_count);
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
        if (shawarmaList == null) return;
        List<Shawerma> sortedList = new ArrayList<>(shawarmaList);
        switch (sortType) {
            case 0:
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getName));
                break;
            case 1:
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getPlaceName));
                break;
            case 2:
                Collections.sort(sortedList, (a, b) -> Integer.compare(b.getRating(), a.getRating()));
                break;
            case 3:
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getRating));
                break;
            case 4:
                Collections.sort(sortedList, Comparator.comparing(Shawerma::getPrice));
                break;
        }
        adapter.updateList(sortedList);
    }

    private void setupSearch() {
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                List<Shawerma> filtered = new ArrayList<>();
                for (Shawerma s : shawarmaList) {
                    if (s.getName().toLowerCase().contains(query.toLowerCase()) ||
                            s.getPlaceName().toLowerCase().contains(query.toLowerCase())) {
                        filtered.add(s);
                    }
                }
                adapter.updateList(filtered);
                if (filtered.isEmpty()) {
                    Toast.makeText(this, "Ничего не найдено!", Toast.LENGTH_SHORT).show();
                }
            } else {
                adapter.updateList(shawarmaList);
            }
        });
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

        builder.setTitle(shawarma == null ? "➕ Добавить шаурму" : "✏️ Редактировать")
                .setView(dialogView)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String placeName = etPlaceName.getText().toString().trim();
                    int rating = (int) ratingBar.getRating();
                    int price = 0;
                    try {
                        price = Integer.parseInt(etPrice.getText().toString().trim());
                    } catch (NumberFormatException e) {}
                    String address = etAddress.getText().toString().trim();
                    String comment = etComment.getText().toString().trim();

                    if (name.isEmpty() || placeName.isEmpty()) {
                        Toast.makeText(this, "Заполните название и заведение!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (shawarma == null) {
                            DataManager.addRecord(new Shawerma(0, name, placeName, rating, price,
                                    address.isEmpty() ? "Не указан" : address,
                                    comment.isEmpty() ? "Без комментария" : comment));
                            Toast.makeText(this, "✅ Добавлено!", Toast.LENGTH_SHORT).show();
                        } else {
                            shawarma.setName(name);
                            shawarma.setPlaceName(placeName);
                            shawarma.setRating(rating);
                            shawarma.setPrice(price);
                            shawarma.setAddress(address.isEmpty() ? "Не указан" : address);
                            shawarma.setCommet(comment.isEmpty() ? "Без комментария" : comment);
                            DataManager.updateRecord(shawarma);
                            Toast.makeText(this, "✅ Обновлено!", Toast.LENGTH_SHORT).show();
                        }
                        refreshList();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showDeleteDialog(Shawerma shawarma) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить")
                .setMessage("Удалить \"" + shawarma.getName() + "\"?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    DataManager.deleteRecord(shawarma.getId());
                    refreshList();
                    Toast.makeText(this, "✅ Удалено!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void refreshList() {
        shawarmaList = DataManager.getAllRecords();
        updateTotalCount();
        int currentSort = spinnerSort.getSelectedItemPosition();
        sortShawarma(currentSort);
    }

    private void updateTotalCount() {
        tvTotalCount.setText("🌯 Всего: " + shawarmaList.size());
    }

    private void addTestData() {
        /*DataManager.addRecord(new Shawerma(0, "Классическая", "Шаверма №1", 9, 250, "ул. Ленина, 10", "Очень вкусно!"));
        DataManager.addRecord(new Shawerma(0, "Острая", "Шаурмичная у дома", 8, 280, "пр. Мира, 25", "Хороший соус"));
        DataManager.addRecord(new Shawerma(0, "С сыром", "Быстро и Вкусно", 10, 300, "ул. Гагарина, 5", "Лучшая!"));
        DataManager.addRecord(new Shawerma(0, "Куриная", "Лавка Шавермы", 9, 220, "ул. Советская, 42", "Недорого"));
        DataManager.addRecord(new Shawerma(0, "Говяжья", "Premium Shawarma", 10, 400, "пр. Невский, 88", "Премиум"));
        DataManager.addRecord(new Shawerma(0, "Вегетарианская", "Зеленая Шаверма", 6, 200, "ул. Цветочная, 7", "Для вегетарианцев"));
        DataManager.addRecord(new Shawerma(0, "Острая с халапеньо", "Мексиканская", 8, 320, "ул. Солнечная, 12", "Очень остро!"));
    */}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataManager.saveData();
    }
}