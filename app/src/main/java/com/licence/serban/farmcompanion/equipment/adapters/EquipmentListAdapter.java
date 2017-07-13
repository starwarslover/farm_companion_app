package com.licence.serban.farmcompanion.equipment.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.equipment.models.Equipment;

import java.util.List;

/**
 * Created by Serban on 25.04.2017.
 */

public class EquipmentListAdapter extends ArrayAdapter<Equipment> {
  private Context myContext;
  private int myResourceId;
  private List<Equipment> myEquipments;

  public EquipmentListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Equipment> objects) {
    super(context, resource, objects);
    this.myContext = context;
    this.myResourceId = resource;
    this.myEquipments = objects;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View row = convertView;
    EquipmentHolder holder = null;

    if (row == null) {
      LayoutInflater layoutInflater = LayoutInflater.from(myContext);
      row = layoutInflater.inflate(myResourceId, parent, false);
      holder = new EquipmentHolder();
      holder.manufacturerTextView = (TextView) row.findViewById(R.id.equipmentRowManufacturerTextView);
      holder.modelTextView = (TextView) row.findViewById(R.id.equipmentRowModelTextView);
      holder.equipRowStatusTextView = (TextView) row.findViewById(R.id.equipRowStatusTextView);
      row.setTag(holder);
    } else {
      holder = (EquipmentHolder) row.getTag();
    }

    Equipment currentEquipment = myEquipments.get(position);

    holder.manufacturerTextView.setText(currentEquipment.getManufacturer());
    holder.modelTextView.setText(currentEquipment.getModel());
    holder.equipRowStatusTextView.setText(currentEquipment.getState() != null ? currentEquipment.getState().toString() : "N/A");

    return row;
  }

  public void removeEquipment(String equipId) {
    Equipment tempEequip = null;
    for (Equipment equipment : myEquipments) {
      if (equipId.equals(equipment.getId())) {
        tempEequip = equipment;
        break;
      }
    }
    if (tempEequip != null) {
      myEquipments.remove(tempEequip);
      this.notifyDataSetChanged();
    }
  }

  public void updateEquipment(Equipment equipment) {
    int idx = -1;
    for (Equipment equip : myEquipments) {
      idx++;
      if (equipment.getId().equals(equip.getId())) {
        break;
      }
    }
    if (idx != -1)
      this.myEquipments.set(idx, equipment);
    notifyDataSetChanged();
  }

  private class EquipmentHolder {
    public TextView manufacturerTextView;
    public TextView modelTextView;
    public TextView equipRowStatusTextView;
  }
}
