package com.licence.serban.farmcompanion.consumables.adapters;

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
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;

import java.util.Date;
import java.util.List;


public class ConsumableListAdapter extends ArrayAdapter<Consumable> {
  private Context myContext;
  private int myResId;
  private List<Consumable> myConsumables;

  public ConsumableListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Consumable> objects) {
    super(context, resource, objects);
    this.myContext = context;
    this.myResId = resource;
    this.myConsumables = objects;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    View row = convertView;
    ConsumableHolder holder;
    if (row == null) {
      LayoutInflater inflater = LayoutInflater.from(myContext);
      row = inflater.inflate(myResId, parent, false);
      holder = new ConsumableHolder();
      holder.amountTextView = (TextView) row.findViewById(R.id.consumableAmountTextView);
      holder.nameTextView = (TextView) row.findViewById(R.id.consumableNameTextView);
      holder.purchaseDateTextView = (TextView) row.findViewById(R.id.consumablePurchaseDateTextView);
      row.setTag(holder);
    } else {
      holder = (ConsumableHolder) row.getTag();
    }

    Consumable currentConsumable = myConsumables.get(position);
    holder.nameTextView.setText(currentConsumable.getName());
    String date = "N/A";
    if (currentConsumable.getPurchaseDate() != 0) {
      date = StringDateFormatter.dateToString(new Date(currentConsumable.getPurchaseDate()));
    }
    holder.purchaseDateTextView.setText(date);
    String amount = String.valueOf(currentConsumable.getPurchasePrice()) + " " + getUM(currentConsumable);
    holder.amountTextView.setText(amount);

    return row;
  }

  private String getUM(Consumable currentConsumable) {
    if (currentConsumable.getType() == ConsumableEnum.CHEMICAL || currentConsumable.getType() == ConsumableEnum.FUEL)
      return "L";
    return "Kg";
  }

  public void removeConsumable(String id) {
    Consumable consumableToRemove = null;
    for (Consumable t : myConsumables) {
      if (t.getId().equals(id)) {
        consumableToRemove = t;
        break;
      }
    }
    if (consumableToRemove != null) {
      myConsumables.remove(consumableToRemove);
      notifyDataSetChanged();
    }
  }

  public int containsConsumable(Consumable consumable) {
    if (consumable != null) {
      for (int i = 0; i < myConsumables.size(); i++) {
        if (consumable.getId().equals(myConsumables.get(i).getId())) {
          return i;
        }
      }
    }
    return -1;
  }

  public void setConsumable(int pos, Consumable consumable) {
    myConsumables.set(pos, consumable);
    this.notifyDataSetChanged();
  }

  public void updateConsumable(Consumable consumable) {
    int index = 0;
    for (Consumable t : myConsumables) {
      index++;
      if (t.getId().equals(consumable.getId())) {
        break;
      }
    }
    myConsumables.set(index - 1, consumable);
    this.notifyDataSetChanged();
  }

  private class ConsumableHolder {
    public TextView nameTextView;
    public TextView amountTextView;
    public TextView purchaseDateTextView;
  }
}
