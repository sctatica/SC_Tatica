package com.sang.sc_tatica.Ranks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.sang.sc_tatica.R;

import static com.sang.sc_tatica.Ranks.RankActivity.WHICH_RANK;

public class RankDetailDialog extends AppCompatDialogFragment {

    private ImageView rank_imageImv;
    private TextView rank_typeImv;
    private TextView rank_descriptionImv;
    Intent intent;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rank_details, null);

        rank_descriptionImv = view.findViewById(R.id.dialog_rank_description);
        rank_imageImv = view.findViewById(R.id.dialog_rank_image);
        rank_typeImv = view.findViewById(R.id.dialog_rank_type);

        // Receive which rank
        Bundle bundle = getArguments();
//        intent = getActivity().getIntent();
        int rank_number = bundle.getInt(WHICH_RANK, 0);

        rank_imageImv.setImageResource(Rank.ranksDetails[rank_number].getRank_image());
        rank_typeImv.setText(Rank.ranksDetails[rank_number].getRank_type());
        rank_descriptionImv.setText(Rank.ranksDetails[rank_number].getRank_description());

        builder.setView(view);

        return builder.create();
    }
}
