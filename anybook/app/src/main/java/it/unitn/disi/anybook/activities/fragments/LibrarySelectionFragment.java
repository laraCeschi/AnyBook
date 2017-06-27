package it.unitn.disi.anybook.activities.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import it.unitn.disi.anybook.R;
import it.unitn.disi.anybook.data.Author;
import it.unitn.disi.anybook.data.Book;
import it.unitn.disi.anybook.data.Category;
import it.unitn.disi.anybook.data.Library;
import it.unitn.disi.anybook.databaseUtil.DbHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

import static it.unitn.disi.anybook.data.StaticStrings.IN_POSSESSO;
import static it.unitn.disi.anybook.data.StaticStrings.WISHLIST;
import static it.unitn.disi.anybook.dataHandler.AuthorHandler.addAuthor;
import static it.unitn.disi.anybook.dataHandler.AuthorHandler.addAuthorToBook;
import static it.unitn.disi.anybook.dataHandler.AuthorHandler.existAuthor;
import static it.unitn.disi.anybook.dataHandler.AuthorHandler.getAuthorByName;
import static it.unitn.disi.anybook.dataHandler.BookHandler.addBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.deleteBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.existBook;
import static it.unitn.disi.anybook.dataHandler.BookHandler.getBookByISBN;
import static it.unitn.disi.anybook.dataHandler.CategoryHandler.addCategory;
import static it.unitn.disi.anybook.dataHandler.CategoryHandler.addCategoryToBook;
import static it.unitn.disi.anybook.dataHandler.CategoryHandler.existCategory;
import static it.unitn.disi.anybook.dataHandler.CategoryHandler.getCategoryByName;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.addBookToLibrary;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.deleteBookLibraryRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.existLibraryBookRelation;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getAllLibrary;
import static it.unitn.disi.anybook.dataHandler.LibraryHandler.getLibraryByName;

/**
 * Questa classe rappresenta il Fragment che viene utilizzato nella schermata di presentazione della
 * lista di librerie da selezionare nella presentazione di un libro.
 */
public class LibrarySelectionFragment extends Fragment {

    private static final String TAG = "LibrarySelectionFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected ArrayList<Library> mDataset;
    protected ArrayList<Boolean> mDataSetForMark;
    private Book book;
    protected ArrayList<RadioButton> radioButtons;
    protected ArrayList<CheckBox> checkBoxes;
    private AlertDialog.Builder alertDialog;
    private View view;

    /**
     * Questo metodo ritorna un nuovo LibrarySelectionFragment inserendo le informazioni passate per parametro
     * @param json il json del libro da cui estrapolare le librerie
     * @return il LibrarySelectionFragment costruito
     */
   public static LibrarySelectionFragment newInstanceLibrarySelectionFragment(String json) {
        LibrarySelectionFragment l = new LibrarySelectionFragment();
        Bundle args = new Bundle();
        args.putString("json", json);
        l.setArguments(args);
        return l;
   }

    /**
     * Questo metodo crea il Fragment e inizializza il dataset della RecycleView ospitata
     *
     * @param savedInstanceState contiene i dati più recenti forniti a onSaveInstanceState().
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        //qui ho cambiato metodo di passare gli oggetti tra fragment e activity, l'oggetto book l'ho
        //trasformato in un json con le librerie di google e poi viceversa
        String jsonBook = args.getString("json", "");
        if (!jsonBook.equals("")) {
            book = new Gson().fromJson(jsonBook, Book.class);
        }
        mDataSetForMark = new ArrayList<>();
        radioButtons = new ArrayList<>();
        checkBoxes = new ArrayList<>();
        initDataset();
    }

    /**
     * Questo metodo istanzia la grafica del Fragment.
     *
     * @param inflater           il LayoutInflater che viene utilizzato per "gonfiare" una view in un Fragment
     * @param container          la view gerarchicamente superiore in cui va inserito il Fragment
     * @param savedInstanceState l'eventuale stato precedente del Fragment
     * @return la View della grafica del Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.library_selection_fragment, container, false);
        rootView.setTag(TAG);
        final RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layout_check);
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getName().equals(WISHLIST) || mDataset.get(i).getName().equals(IN_POSSESSO)) {
                RadioButton radioButton = new RadioButton(getContext());
                radioButton.setText(mDataset.get(i).getName());
                radioButton.setTextSize(getResources().getDimension(R.dimen.radio_button_text));
                radioGroup.addView(radioButton);
                radioButton.setChecked(mDataSetForMark.get(i));
                RadioGroup.LayoutParams l = new RadioGroup.LayoutParams(rootView.getContext(), null);
                l.setMargins(0, 0, 0, 10);
                radioButton.setLayoutParams(l);
                radioButtons.add(radioButton);
            } else {
                CheckBox checkBox = new CheckBox(getContext());
                linearLayout.addView(checkBox);
                checkBoxes.add(checkBox);
                checkBox.setText(mDataset.get(i).getName());
                checkBox.setTextSize(getResources().getDimension(R.dimen.check_box_text));
                checkBox.setChecked(mDataSetForMark.get(i));
            }
        }


        rootView.findViewById(R.id.save_library_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book != null) {
                    int radioButtonId = radioGroup.getCheckedRadioButtonId();
                    if (radioButtonId < 0) {
                        Toast toast = Toast.makeText(getContext(), "seleziona anche \"wishlist\" o \"in possesso\" per permetterci di generare migliori consigli di lettura", Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        System.out.println(book.getiFrame());
                        DbHelper helper = new DbHelper(getContext());
                        if (!existBook(helper, book.getISBN())) {
                            addBook(helper, book);
                            if (book.getAuthors() != null) {
                                for (int i = 0; i < book.getAuthors().size(); i++) {
                                    Author author = new Author(book.getAuthors().get(i).getName());
                                    if (!addAuthor(helper, author)) {
                                        if (existAuthor(helper, author.getName())) {
                                            author = getAuthorByName(helper, author.getName());
                                        }
                                    }
                                    addAuthorToBook(helper, author, book);
                                }
                            }
                            if (book.getCategories() != null) {
                                for (int i = 0; i < book.getCategories().size(); i++) {
                                    Category category = new Category(book.getCategories().get(i).getName());
                                    if (!addCategory(helper, category)) {
                                        if (existCategory(helper, category)) {
                                            category = getCategoryByName(helper, book.getCategories().get(i).getName());
                                        }
                                    }
                                    addCategoryToBook(helper, book, category);
                                }
                            }
                        } else if (book.getID() == -1) {
                            Book book1 = getBookByISBN(helper, book.getISBN());
                            book.setID(book1.getID());
                        }
                        RadioButton radioButton = (RadioButton) rootView.findViewById(radioButtonId);
                        Library library = getLibraryByName(helper, radioButton.getText().toString());
                        if (book.getID() != -1) {
                            for (int i = 0; i < radioButtons.size(); i++) {
                                deleteBookLibraryRelation(helper, book, getLibraryByName(helper, radioButtons.get(i).getText().toString()));
                            }
                            addBookToLibrary(helper, book, library);

                            for (int i = 0; i < checkBoxes.size(); i++) {
                                deleteBookLibraryRelation(helper, book, getLibraryByName(helper, checkBoxes.get(i).getText().toString()));
                                if (checkBoxes.get(i).isChecked()) {
                                    addBookToLibrary(helper, book, getLibraryByName(helper, checkBoxes.get(i).getText().toString()));
                                }
                            }
                        }
                        Toast toast = Toast.makeText(getContext(), "modifiche salvate", Toast.LENGTH_SHORT);
                        toast.show();
                        helper.close();
                    }
                }
            }
        });

        initDialog();
        rootView.findViewById(R.id.delete_book_floating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView();
                alertDialog.setTitle("Confermare l'eliminazione");
                alertDialog.show();
            }
        });
        return rootView;
    }

    /**
     * Questo metodo rimuove il dialog dal suo parent
     */
    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * Questo metodo salva lo stato corrente.
     *
     * @param savedInstanceState il Bundle in cui salvare lo stato corrente.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * fatto: modificare questo metodo per generare il vero dataset
     * Questo metodo genera il dataset da inserire nella RecycleView
     */
    private void initDataset() {
        DbHelper helper = new DbHelper(getContext());
        mDataset = getAllLibrary(helper);
        if (book != null) {
            for (int i = 0; i < mDataset.size(); i++) {
                if (existLibraryBookRelation(helper, book, mDataset.get(i))) {
                    mDataSetForMark.add(i, Boolean.TRUE);
                } else {
                    mDataSetForMark.add(i, Boolean.FALSE);
                }
            }
        } else {
            for (int i = 0; i < mDataset.size(); i++) {
                mDataSetForMark.add(i, false);
            }
        }
        helper.close();
    }

    /**
     * Questo metodo performa l'eliminazione del libro dal database
     */
    private void removeBook() {
        DbHelper helper = new DbHelper(getContext());
        if (mDataset != null && book != null) {
            for (int i = 0; i < mDataset.size(); i++) {
                if (existLibraryBookRelation(helper, book, mDataset.get(i))) {
                    deleteBookLibraryRelation(helper, book, mDataset.get(i));
                }
            }
            deleteBook(helper, book);
            initDataset();
            for (int i = 0; i < radioButtons.size(); i++) {
                radioButtons.get(i).setChecked(false);
            }
            for (int i = 0; i < checkBoxes.size(); i++) {
                checkBoxes.get(i).setChecked(false);
            }
        }
    }

    /**
     * Questo metodo inizializza il dialog per la conferma (o l'annulllamento) dell'eliminazione
     */
    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this.getContext());
        view = getActivity().getLayoutInflater().inflate(R.layout.dialog_layout_wishlist, null);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("ELIMINA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeBook();
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });

    }
}