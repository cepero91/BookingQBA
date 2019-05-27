package com.infinitum.bookingqba.view.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;



import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * ESTA CLASE FUE TOMADA DE LA BIBLIOTECA SEARCHABLESPINNER
 * autor: com.toptoche
 * SE HIZO ALGUNAS MODIFICACIONES PORQUE ESTABA DANDO UN LEAK DE MEMORIA
 */
public class SearchableSpinner extends android.support.v7.widget.AppCompatSpinner implements View.OnTouchListener,
        SearchableListDialog.SearchableItem {

    public static final int NO_ITEM_SELECTED = -1;
    // WEAKREFERENCE PARA EL CONTEXT PREVIENDO UNA FUGA DE MEMORIA
    private WeakReference<Context> _context;
    private List _items;

    private boolean _isDirty;
    private ArrayAdapter _arrayAdapter;
    private String _strHintText;
    private boolean _isFromInit;
    private String strTitle;
    private String strPositiveButtonText;
    private DialogInterface.OnClickListener onClickListener;
    private SearchableListDialog.OnSearchTextChanged onSearchTextChanged;

    public SearchableSpinner(Context context) {
        super(context);
        this._context = new WeakReference<>(context);
        init();
    }

    public SearchableSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this._context = new WeakReference<>(context);
        TypedArray a = context.obtainStyledAttributes(attrs, com.toptoche.searchablespinnerlibrary.R.styleable.SearchableSpinner);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            if (attr == com.toptoche.searchablespinnerlibrary.R.styleable.SearchableSpinner_hintText) {
                _strHintText = a.getString(attr);
            }
        }
        a.recycle();
        init();
    }

    public SearchableSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = new WeakReference<>(context);
        init();
    }

    private void init() {
        _items = new ArrayList();

        setOnTouchListener(this);

        _arrayAdapter = (ArrayAdapter) getAdapter();
        if (!TextUtils.isEmpty(_strHintText)) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(_context.get(), android.R.layout
                    .simple_list_item_1, new String[]{_strHintText});
            _isFromInit = true;
            setAdapter(arrayAdapter);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if (null != _arrayAdapter) {

                // Refresh content #6
                // Change Start
                // Description: The items were only set initially, not reloading the data in the
                // spinner every time it is loaded with items in the adapter.
                _items.clear();
                for (int i = 0; i < _arrayAdapter.getCount(); i++) {
                    _items.add(_arrayAdapter.getItem(i));
                }
                // Change end.
                //-------- EL CAMBIO FUNDAMENTAL FUE ESTE, CREAR EL DIALOGO DESPUES DE DISPARAR EN EVENTO ------//
                SearchableListDialog _searchableListDialog = SearchableListDialog.newInstance
                        (_items);
                _searchableListDialog.setOnSearchableItemClickListener(this);
                _searchableListDialog.setTitle(strTitle);
                _searchableListDialog.setPositiveButton(strPositiveButtonText);
                _searchableListDialog.setPositiveButton(strPositiveButtonText, onClickListener);
                _searchableListDialog.setOnSearchTextChangedListener(onSearchTextChanged);
                _searchableListDialog.show(((AppCompatActivity)_context.get()).getSupportFragmentManager(), "TAG");
            }
        }
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {

        if (!_isFromInit) {
            _arrayAdapter = (ArrayAdapter) adapter;
            if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
                ArrayAdapter arrayAdapter = new ArrayAdapter(_context.get(), android.R.layout
                        .simple_list_item_1, new String[]{_strHintText});
                super.setAdapter(arrayAdapter);
            } else {
                super.setAdapter(adapter);
            }

        } else {
            _isFromInit = false;
            super.setAdapter(adapter);
        }
    }

    @Override
    public void onSearchableItemClicked(Object item, int position) {
        setSelection(_items.indexOf(item));

        if (!_isDirty) {
            _isDirty = true;
            setAdapter(_arrayAdapter);
            setSelection(_items.indexOf(item));
        }
    }

    /**
     * TODOS ESTOS ATRIBUTOS SE CONVIRTIERON EN VARIABLES LOCALES
     * QUE LUEGO SON ASIGNADAS COMO
     * PROPIEDADES DEL DIALOGO
     */
    public void setTitle(String strTitle) {
        this.strTitle = strTitle;
    }

    public void setPositiveButton(String strPositiveButtonText) {
        this.strPositiveButtonText = strPositiveButtonText;
    }

    public void setPositiveButton(String strPositiveButtonText, DialogInterface.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnSearchTextChangedListener(SearchableListDialog.OnSearchTextChanged onSearchTextChanged) {
        this.onSearchTextChanged = onSearchTextChanged;
    }

    //-----------------------------------------------------------

    @Override
    public int getSelectedItemPosition() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return NO_ITEM_SELECTED;
        } else {
            return super.getSelectedItemPosition();
        }
    }

    @Override
    public Object getSelectedItem() {
        if (!TextUtils.isEmpty(_strHintText) && !_isDirty) {
            return null;
        } else {
            return super.getSelectedItem();
        }
    }
}
