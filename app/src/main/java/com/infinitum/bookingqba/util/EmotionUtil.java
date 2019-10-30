package com.infinitum.bookingqba.util;

import com.infinitum.bookingqba.R;

public class EmotionUtil {
    public static int getEmotionDrawableId(int emotionLevel) {
        int id = R.drawable.ic_laugh_emotion;
        switch (emotionLevel) {
            case 1:
                id = R.drawable.ic_angry_emotion;
                break;
            case 2:
                id = R.drawable.ic_frown_emotion;
                break;
            case 3:
                id = R.drawable.ic_meh_emotion;
                break;
            case 4:
                id = R.drawable.ic_grin_emotion;
                break;
            case 5:
                id = R.drawable.ic_laugh_emotion;
                break;
        }
        return id;
    }

    public static String emotionTextByLevel(int emotionLevel) {
        switch (emotionLevel) {
            case 1:
                return "Furioso";
            case 2:
                return "Disgustado";
            case 3:
                return "Serio";
            case 4:
                return "Contento";
            case 5:
                return "Excelente";
            default:
                return "Excelente";
        }
    }
}
