package com.infinitum.bookingqba.view.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentSecondStepBinding;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.infinitum.bookingqba.view.profile.dialogitem.FormSelectorItem;
import com.infinitum.bookingqba.view.profile.dialogitem.SearchableSelectorModel;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.android.support.AndroidSupportInjection;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;

public class SecondStepFragment extends Fragment implements Step, View.OnClickListener {

    private FragmentSecondStepBinding binding;
    private CFAlertDialog alertDialog;
    private OnStepFormEnd onStepFormEnd;
    private Map<String, List<FormSelectorItem>> mapSelector;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> referenceStringList;
    private List<String> municipalitiesStringList;
    private List<SearchableSelectorModel> searchableSelectorModelLis;
    private CFAlertDialog.Builder builder;
    private String referenceZoneUuid;
    private String municipalityUuid;


    public SecondStepFragment() {
    }

    public static SecondStepFragment newInstance() {
        return new SecondStepFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_second_step, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        referenceStringList = new ArrayList<>();
        municipalitiesStringList = new ArrayList<>();
        binding.flReferenceZone.setOnClickListener(this);
        binding.flMunicipalities.setOnClickListener(this);
//        binding.spinnerMunicipalities.setEnabled(false);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
        if (context instanceof OnStepFormEnd) {
            onStepFormEnd = (OnStepFormEnd) context;
        }
    }

    @Override
    public void onDetach() {
        onStepFormEnd = null;
        super.onDetach();
    }

    public void setMapSelector(Map<String, List<FormSelectorItem>> mapSelector) {
        this.mapSelector = mapSelector;
        boolean hasMun = mapSelector.containsKey("municipalities") && mapSelector.get("municipalities").size() > 0;
        boolean hasRz = mapSelector.containsKey("referenceZone") && mapSelector.get("referenceZone").size() > 0;
        if (hasMun && hasRz) {
            binding.ivErrorMunicipalities.setVisibility(View.GONE);
            binding.ivErrorReferenceZone.setVisibility(View.GONE);
            binding.tvError.setText("");
        } else {
            binding.ivErrorMunicipalities.setVisibility(View.VISIBLE);
            binding.ivErrorReferenceZone.setVisibility(View.VISIBLE);
            binding.tvError.setText("Error al cargar formulario");
        }
    }

    public void pasiveUpdateInputs(String address, String referenceZone, String municipality) {
        binding.etAddress.setText(address);
        binding.tvReferenceZone.setText(getNameByUuid(referenceZone, mapSelector.get("referenceZone")));
        binding.tvHintSpinner.setText(getNameByUuid(municipality, mapSelector.get("municipalities")));
    }

    //--------------------------------- STEP ---------------------------
    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
//        String address = binding.etAddress.getText().toString();
//        if (!address.equals("")
//                && referenceZoneUuid != null && referenceZoneUuid.length() > 0
//                && municipalityUuid != null && municipalityUuid.length() > 0) {
//            onStepFormEnd.submitSecondForm(address, referenceZoneUuid, municipalityUuid);
//            return null;
//        } else {
//            return new VerificationError("Llene los campos requeridos");
//        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        binding.tvError.setText(error.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_reference_zone:
                if (mapSelector.containsKey("referenceZone") && mapSelector.get("referenceZone").size() > 0) {
                    new SimpleSearchDialogCompat<SearchableSelectorModel>(getActivity(), "Zona de Referencia",
                            "Puede buscar por nombre...", null, (ArrayList) mapSelector.get("referenceZone"),
                            (dialog, item, position) -> {
                                referenceZoneUuid = item.getUuid();
                                binding.tvReferenceZone.setText(item.getTitle());
                                dialog.dismiss();
                            }).show();
                }
                break;
            case R.id.fl_municipalities:
                if (mapSelector.get("municipalities") != null && mapSelector.get("municipalities").size() > 0) {
                    new SimpleSearchDialogCompat<SearchableSelectorModel>(getActivity(), "Municipios",
                            "Puede buscar por nombre...", null, (ArrayList) mapSelector.get("municipalities"),
                            (dialog, item, position) -> {
                                municipalityUuid = item.getUuid();
                                binding.tvHintSpinner.setText(item.getTitle());
                                dialog.dismiss();
                            }).show();
                }
                break;
        }
    }

    private String getNameByUuid(String uuid, List<FormSelectorItem> list) {
        String name = "";
        for (FormSelectorItem item : list) {
            if (item.getUuid().equals(uuid)) {
                name = item.getTitle();
                break;
            }
        }
        return name;
    }

    private int getPosByUuid(String uuid, List<FormSelectorItem> list) {
        int pos = -1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUuid().equals(uuid)) {
                pos = i;
                break;
            }
        }
        return pos;
    }
}
