package com.infinitum.bookingqba.view.adapters.items.spinneritem;

import java.util.List;

public class CommonSpinnerList {

    private List<CommonSpinnerItem> itemList;
    private String[] arrayNames;
    private String[] arrayUuid;

    public CommonSpinnerList(List<CommonSpinnerItem> itemList) {
        this.itemList = itemList;
        this.arrayNames = createArrayNames();
        this.arrayUuid = createArrayUuid();
    }

    private String[] createArrayNames(){
        String[] arr  = new String[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
           arr[i] = itemList.get(i).getName();
        }
        return arr;
    }

    private String[] createArrayUuid(){
        String[] arr  = new String[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            arr[i] = itemList.get(i).getUuid();
        }
        return arr;
    }

    public int provincePosByUuid(String uuid){
        int pos = -1;
        for(int i=0; i < arrayUuid.length; i++){
            if(arrayUuid[i].equals(uuid)){
                pos = i;
                break;
            }
        }
        return pos;
    }

    public String getUuidOnPos(int pos){
        return arrayUuid[pos];
    }

    public String getNameOnPos(int pos){
        return arrayNames[pos];
    }

    public String[] getArrayNames() {
        return arrayNames;
    }

    public void setArrayNames(String[] arrayNames) {
        this.arrayNames = arrayNames;
    }

    public String[] getArrayUuid() {
        return arrayUuid;
    }

    public void setArrayUuid(String[] arrayUuid) {
        this.arrayUuid = arrayUuid;
    }
}
