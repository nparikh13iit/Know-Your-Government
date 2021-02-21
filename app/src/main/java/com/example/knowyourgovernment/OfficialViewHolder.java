package com.example.knowyourgovernment;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder {

    TextView officeTitle,officialInfo;

    public OfficialViewHolder(@NonNull View itemView) {
        super(itemView);
        officeTitle = itemView.findViewById(R.id.officeTitle);
        officialInfo = itemView.findViewById(R.id.officialInfo);
    }
}
