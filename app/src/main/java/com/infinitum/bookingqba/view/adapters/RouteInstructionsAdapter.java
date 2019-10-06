package com.infinitum.bookingqba.view.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.graphhopper.util.Helper;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.infinitum.bookingqba.R;
import com.infinitum.bookingqba.util.UnitConverterUtil;
import com.infinitum.bookingqba.view.adapters.items.rentdetail.RentPoiItem;

import java.util.List;

public class RouteInstructionsAdapter extends RecyclerView.Adapter<RouteInstructionsAdapter.MyViewHolder> {

    InstructionList instructions;

    public RouteInstructionsAdapter(InstructionList instructions) {
        this.instructions = instructions;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_route_instructions, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Instruction instruction = instructions.get(i);
        String direction = getDirectionDescription(instruction);
        myViewHolder.ivSign.setImageResource(getDirectionSign(instruction));
        myViewHolder.tvSign.setText(direction);
        myViewHolder.tvDistance.setText(String.format("avance unos %s", UnitConverterUtil.getString(instruction.getDistance())));
    }


    @Override
    public int getItemCount() {
        return instructions.size();
    }


    /**
     * @param instruction
     * @return direction
     */
    private String getDirectionDescription(Instruction instruction) {
        if (instruction.getSign() == 4) return "Fin de la ruta";//4
        String str; // TODO: Translate all this instructions to Language?
        String streetName = instruction.getName();
        int sign = instruction.getSign();
        String dir = "";
        String dirTo = "en";
        switch (sign) {
            case Instruction.CONTINUE_ON_STREET:
                dir = ("Continue");
                dirTo = "por";
                break;
            case Instruction.LEAVE_ROUNDABOUT:
                dir = ("Deje la rotonda");
                break;
            case Instruction.TURN_SHARP_LEFT:
                dir = ("Gire bruscamente a la izquierda");
                break;
            case Instruction.TURN_LEFT:
                dir = ("Gire izquierda");
                break;
            case Instruction.TURN_SLIGHT_LEFT:
                dir = ("Gire ligeramente a la izquierda");
                break;
            case Instruction.TURN_SLIGHT_RIGHT:
                dir = ("Gire ligeramente a la derecha");
                break;
            case Instruction.TURN_RIGHT:
                dir = ("Gire derecha");
                break;
            case Instruction.TURN_SHARP_RIGHT:
                dir = ("Gire bruscamente a la derecha");
                break;
            case Instruction.REACHED_VIA:
                dir = ("Alcanzado a travéz de");
                break;
            case Instruction.USE_ROUNDABOUT:
                dir = ("Use la rotonda");
                break;
            case Instruction.KEEP_LEFT:
                dir = ("Mantengase a la izquierda");
                break;
            case Instruction.KEEP_RIGHT:
                dir = ("Mantengase a la derecha");
                break;
        }
        str = Helper.isEmpty(streetName) ? dir : (dir + " " + dirTo + " " + streetName);
        return str;
    }

    /**
     * @param itemData
     * @return int resId to instruction direction's sign icon
     */
    public int getDirectionSign(Instruction itemData) {
        switch (itemData.getSign()) {
            case Instruction.LEAVE_ROUNDABOUT:
                return R.drawable.ic_undo_nav;
            case Instruction.TURN_SHARP_LEFT:
                return R.drawable.ic_turn_left_1_nav;
            case Instruction.TURN_LEFT:
                return R.drawable.ic_curve_arrow_2_nav;
            case Instruction.TURN_SLIGHT_LEFT:
                return R.drawable.ic_diagonal_arrow_1_nav;
            case Instruction.CONTINUE_ON_STREET:
                return R.drawable.ic_up_arrow_2_nav;
            case Instruction.TURN_SLIGHT_RIGHT:
                return R.drawable.ic_diagonal_arrow_3_nav;
            case Instruction.TURN_RIGHT:
                return R.drawable.ic_curve_arrow_3_nav;
            case Instruction.TURN_SHARP_RIGHT:
                return R.drawable.ic_turn_right_nav;
            case Instruction.FINISH:
                return R.drawable.ic_flag_black_24dp;
            case Instruction.REACHED_VIA:
                return R.drawable.ic_compress_nav;
            case Instruction.USE_ROUNDABOUT:
                return R.drawable.ic_exchange_2_nav;
            case Instruction.KEEP_RIGHT:
                return R.drawable.ic_right_chevron_2_nav;
            case Instruction.KEEP_LEFT:
                return R.drawable.ic_left_chevron_2_nav;
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView ivSign;
        private TextView tvSign;
        private TextView tvDistance;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSign = itemView.findViewById(R.id.iv_sign);
            tvSign = itemView.findViewById(R.id.tv_sign);
            tvDistance = itemView.findViewById(R.id.tv_distance);
        }
    }

}
