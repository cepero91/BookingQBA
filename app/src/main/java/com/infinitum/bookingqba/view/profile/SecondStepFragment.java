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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.databinding.FragmentSecondStepBinding;
import com.infinitum.bookingqba.view.interaction.OnStepFormEnd;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.android.support.AndroidSupportInjection;

public class SecondStepFragment extends Fragment implements Step, View.OnClickListener {

    private FragmentSecondStepBinding binding;
    private CFAlertDialog alertDialog;
    private OnStepFormEnd onStepFormEnd;
    private Map<String, List<FormSelectorItem>> mapSelector;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> referenceStringList;
    private List<String> municipalitiesStringList;
    private CFAlertDialog.Builder builder;
    private String referenceZoneUuid;


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
        binding.spinnerMunicipalities.setEnabled(false);
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
            binding.spinnerMunicipalities.setEnabled(true);
            setupMunicipalitiesDialog();
            setupReferenceDialog();
        } else {
            binding.ivErrorMunicipalities.setVisibility(View.VISIBLE);
            binding.ivErrorReferenceZone.setVisibility(View.VISIBLE);
            binding.tvError.setText("Error al cargar formulario");
            binding.spinnerMunicipalities.setEnabled(false);
        }
    }

    public void pasiveUpdateInputs(String address, String referenceZone, String municipality) {
        binding.etAddress.setText(address);
        binding.tvReferenceZone.setText(getNameByUuid(referenceZone,mapSelector.get("referenceZone")));
        binding.spinnerMunicipalities.setSelection(getPosByUuid(municipality,mapSelector.get("municipalities")));
    }

    //--------------------------------- STEP ---------------------------
    @Nullable
    @Override
    public VerificationError verifyStep() {
        String address = binding.etAddress.getText().toString();
        if (!address.equals("")
                && referenceZoneUuid!=null && referenceZoneUuid.length()>0
                && binding.spinnerMunicipalities.getSelectedItemPosition() >= 0) {
            onStepFormEnd.submitSecondForm(address,referenceZoneUuid, getUuidByWish(binding.spinnerMunicipalities.getSelectedItemPosition(),mapSelector.get("municipalities")));
            return null;
        } else {
            return new VerificationError("Por favor llene los campos requeridos");
        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_reference_zone:
                if (mapSelector.containsKey("referenceZone") && mapSelector.get("referenceZone").size() > 0) {
                    builder.show();
                }
                break;
        }
    }

    private void setupReferenceDialog() {
        builder = new CFAlertDialog.Builder(getActivity());
        builder.setTitle("Zona de Referencia");
        builder.setMessage("Seleccione la zona donde crea, pertenesca su renta");
        builder.setItems(getReferenceStringFromList(mapSelector.get("referenceZone")), (dialog, which) -> {
            binding.tvReferenceZone.setText(referenceStringList.get(which));
            referenceZoneUuid = getUuidByWish(which, mapSelector.get("referenceZone"));
            dialog.dismiss();
        });
    }

    private void setupMunicipalitiesDialog() {
        binding.spinnerMunicipalities.setTitle("Seleccione un Municipio");
        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_custom_item_no_padding,
                    getMunicipalitiesStringFromList(mapSelector.get("municipalities")));
        }
        binding.spinnerMunicipalities.setAdapter(arrayAdapter);
        binding.spinnerMunicipalities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String uuid = getUuidByWish(position, mapSelector.get("municipalities"));
                Toast.makeText(getActivity(), uuid, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String[] getReferenceStringFromList(List<FormSelectorItem> formSelectorItems) {
        referenceStringList = new ArrayList<>();
        for (FormSelectorItem formSelectorItem : formSelectorItems) {
            referenceStringList.add(formSelectorItem.getName());
        }
        return referenceStringList.toArray(new String[referenceStringList.size()]);
    }

    private String[] getMunicipalitiesStringFromList(List<FormSelectorItem> formSelectorItems) {
        municipalitiesStringList = new ArrayList<>();
        for (FormSelectorItem formSelectorItem : formSelectorItems) {
            municipalitiesStringList.add(formSelectorItem.getName());
        }
        return municipalitiesStringList.toArray(new String[municipalitiesStringList.size()]);
    }

    private String getUuidByWish(int wish, List<FormSelectorItem> list) {
        return list.get(wish).getUuid();
    }

    private String getNameByUuid(String uuid, List<FormSelectorItem> list) {
        String name = "";
        for(FormSelectorItem item: list){
            if(item.getUuid().equals(uuid)){
                name = item.getName();
                break;
            }
        }
        return name;
    }

    private int getPosByUuid(String uuid, List<FormSelectorItem> list) {
        int pos = -1;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).getUuid().equals(uuid)){
                pos = i;
                break;
            }
        }
        return pos;
    }
}
