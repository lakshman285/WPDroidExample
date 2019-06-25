package com.ikvaesolutions.android.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ikvaesolutions.android.R;
import com.ikvaesolutions.android.utils.CommonUtils;


public class FancyAlertDialog {
    public static final String TAG = FancyAlertDialog.class.getSimpleName();

//    public static Builder builder;

    public static class Builder {

        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;

        private String textTitle;
        private String textSubTitle;
        private String body;

        private OnPositiveClicked onPositiveClicked;
        private OnNegativeClicked onNegativeClicked;
        private OnNeutralClicked onNeutralClicked;


        private boolean autoHide;

        private int timeToHide;

        private int positiveTextColor;
        private int backgroundColor;
        private int negativeColor;
        private int neutralColor;
        private int titleColor;
        private int subtitleColor;
        private int bodyColor;

        private int imageResource;
        private Drawable imageDrawable;

        private Typeface titleFont;
        private Typeface subTitleFont;
        private Typeface bodyFont;
        private Typeface positiveButtonFont;
        private Typeface negativeButtonFont;
        private Typeface neutralButtonFont;

        private Typeface alertFont;

        private Activity context;

        private PanelGravity buttonsGravity;
        private TextGravity titleGravity, subtitleGravity, bodyGravity;
        private boolean cancelable;


        private PanelGravity getButtonsGravity() {
            return buttonsGravity;
        }

        public Builder setButtonsGravity(PanelGravity buttonsGravity) {
            this.buttonsGravity = buttonsGravity;
            return this;
        }

        private Typeface getAlertFont() {
            return alertFont;
        }

        public Builder setAlertFont(String alertFont) {
            this.alertFont = Typeface.createFromAsset(context.getAssets(), alertFont);
            return this;
        }

        private Typeface getPositiveButtonFont() {
            return positiveButtonFont;
        }

        public Builder setPositiveButtonFont(String positiveButtonFont) {
            this.positiveButtonFont = Typeface.createFromAsset(context.getAssets(), positiveButtonFont);
            return this;
        }

        private Typeface getNegativeButtonFont() {
            return negativeButtonFont;
        }

        private Typeface getNeutralButtonFont() {
            return neutralButtonFont;
        }

        public Builder setNegativeButtonFont(String negativeButtonFont) {
            this.negativeButtonFont = Typeface.createFromAsset(context.getAssets(), negativeButtonFont);
            return this;
        }

        public Builder setNeutalButtonFont(String negativeButtonFont) {
            this.negativeButtonFont = Typeface.createFromAsset(context.getAssets(), negativeButtonFont);
            return this;
        }

        private Typeface getTitleFont() {
            return titleFont;
        }


        public Builder setTitleFont(String titleFontPath) {
            this.titleFont = Typeface.createFromAsset(context.getAssets(), titleFontPath);
            return this;
        }

        private Typeface getSubTitleFont() {
            return subTitleFont;
        }

        public Builder setSubTitleFont(String subTitleFontPath) {
            this.subTitleFont = Typeface.createFromAsset(context.getAssets(), subTitleFontPath);
            return this;
        }

        private Typeface getBodyFont() {
            return bodyFont;
        }

        public Builder setBodyFont(String bodyFontPath) {
            this.bodyFont = Typeface.createFromAsset(context.getAssets(), bodyFontPath);
            return this;
        }


        private int getTimeToHide() {
            return timeToHide;
        }

        public Builder setTimeToHide(int timeToHide) {
            this.timeToHide = timeToHide;
            return this;
        }

        private boolean isAutoHide() {
            return autoHide;
        }

        public Builder setAutoHide(boolean autoHide) {
            this.autoHide = autoHide;
            return this;
        }

        private boolean isCancelable() {
            return cancelable;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Activity getContext() {
            return context;
        }

        public Builder setActivity(Activity context) {
            this.context = context;
            return this;
        }

        public Builder(Activity context) {
            this.context = context;
        }

        private int getPositiveTextColor() {
            return positiveTextColor;
        }

        public Builder setPositiveColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }


        private int getBackgroundColor() {
            return backgroundColor;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        private int getNegativeColor() {
            return negativeColor;
        }

        public Builder setNegativeColor(int negativeColor) {
            this.negativeColor = negativeColor;
            return this;
        }

        private int getNeutralColor() {
            return neutralColor;
        }

        public Builder setNeutralColor(int neutralColor) {
            this.neutralColor = neutralColor;
            return this;
        }


        private int getTitleColor() {
            return titleColor;
        }

        public Builder setTitleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        private int getSubtitleColor() {
            return subtitleColor;
        }

        public Builder setSubtitleColor(int subtitleColor) {
            this.subtitleColor = subtitleColor;
            return this;
        }

        private int getBodyColor() {
            return bodyColor;
        }

        public Builder setBodyColor(int bodyColor) {
            this.bodyColor = bodyColor;
            return this;
        }

        private int getImageResource() {
            return imageResource;
        }

        public Builder setimageResource(int imageResource) {
            this.imageResource = imageResource;
            return this;
        }

        private Drawable getImageDrawable() {
            return imageDrawable;
        }

        public Builder setImageDrawable(Drawable imageDrawable) {
            this.imageDrawable = imageDrawable;
            return this;
        }

        private String getPositiveButtonText() {
            return positiveButtonText;
        }


        public Builder setPositiveButtonText(int positiveButtonText) {
            this.positiveButtonText = context.getString(positiveButtonText);
            return this;
        }

        public Builder setPositiveButtonText(String positiveButtonText) {
            this.positiveButtonText = positiveButtonText;
            return this;
        }

        private String getNegativeButtonText() {
            return negativeButtonText;
        }

        public Builder setNegativeButtonText(String negativeButtonText) {
            this.negativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setNeutralButtonText(int neutralButtonText) {
            this.neutralButtonText = context.getString(neutralButtonText);
            return this;
        }

        private String getNeutralButtonText() {
            return neutralButtonText;
        }

        public Builder setNeutralButtonText(String neutralButtonText) {
            this.neutralButtonText = neutralButtonText;
            return this;
        }

        public Builder setNegativeButtonText(int neutralButtonText) {
            this.neutralButtonText = context.getString(neutralButtonText);
            return this;
        }

        private String getTextTitle() {
            return textTitle;
        }

        public Builder setTextTitle(String textTitle) {
            this.textTitle = textTitle;
            return this;
        }

        public Builder setTextTitle(int textTitle) {
            this.textTitle = context.getString(textTitle);
            return this;
        }

        private TextGravity getTitleGravity() {
            return this.titleGravity;
        }

        public Builder setTitleGravity(TextGravity gravity) {
            this.titleGravity = gravity;
            return this;
        }

        private TextGravity getSubtitleGravity() {
            return this.subtitleGravity;
        }

        public Builder setSubtitleGravity(TextGravity gravity) {
            this.subtitleGravity = gravity;
            return this;
        }

        private TextGravity getBodyGravity() {
            return this.bodyGravity;
        }

        public Builder setBodyGravity(TextGravity gravity) {
            this.bodyGravity = gravity;
            return this;
        }

        private String getTextSubTitle() {
            return textSubTitle;
        }

        public Builder setTextSubTitle(String textSubTitle) {
            this.textSubTitle = textSubTitle;
            return this;
        }

        public Builder setTextSubTitle(int textSubTitle) {
            this.textSubTitle = context.getString(textSubTitle);
            return this;
        }

        public String getBody() {
            return body;
        }

        public Builder setBody(String body) {
            this.body = body;
            return this;
        }

        public Builder setBody(int body) {
            this.body = context.getString(body);
            return this;
        }


        private OnPositiveClicked getOnPositiveClicked() {
            return onPositiveClicked;
        }

        public Builder setOnPositiveClicked(OnPositiveClicked onPositiveClicked) {
            this.onPositiveClicked = onPositiveClicked;
            return this;
        }

        private OnNegativeClicked getOnNegativeClicked() {
            return onNegativeClicked;
        }

        public Builder setOnNegativeClicked(OnNegativeClicked onNegativeClicked) {
            this.onNegativeClicked = onNegativeClicked;
            return this;
        }

        public Builder setOnNeutralClicked(OnNeutralClicked onNeutralClicked) {
            this.onNeutralClicked = onNeutralClicked;
            return this;
        }

        public Builder getOnNeutralClicked(OnNeutralClicked onNeutralClicked) {
            this.onNeutralClicked = onNeutralClicked;
            return this;
        }

        private OnNeutralClicked getOnNeutralClicked() {
            return onNeutralClicked;
        }

        public void build() {
            try {
                showDialog(this);
            }catch (Exception e) {
            }
        }

//        public void show() {
//            //empty method, to get rid of errors, we need to actually remove all calls to this method.
//        }

    }



    private static void showDialog(final Builder builder) {

        CardView cardView;
        AppCompatImageView image;
        TextView title, subTitle, body;
        Button positive, negative, neutral;
        LinearLayout buttonsPanel;


        Activity activity = builder.getContext();

        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }catch (Exception e) {
        }
        dialog.setCancelable(builder.isCancelable());
        dialog.setContentView(R.layout.dialog_alert);

        cardView = (CardView) dialog.findViewById(R.id.card_view);
        image = (AppCompatImageView) dialog.findViewById(R.id.image);
        title = (TextView) dialog.findViewById(R.id.title);
        subTitle = (TextView) dialog.findViewById(R.id.sub_title);
        body = (TextView) dialog.findViewById(R.id.body);
        positive = (Button) dialog.findViewById(R.id.position);
        negative = (Button) dialog.findViewById(R.id.negative);
        neutral = (Button) dialog.findViewById(R.id.neutral);
        buttonsPanel = (LinearLayout) dialog.findViewById(R.id.buttons_panel);

//        initViews(dialog);

            if (builder.getTextTitle() != null) {
                title.setText(builder.getTextTitle());
            } else {
                title.setVisibility(View.GONE);
            }
            if (builder.getTitleColor() != 0) {
                title.setTextColor(ContextCompat.getColor(activity, builder.getTitleColor()));
            }
            if (builder.getTextSubTitle() != null) {
                subTitle.setText(builder.getTextSubTitle());
            } else {
                subTitle.setVisibility(View.GONE);
            }
            if (builder.getSubtitleColor() != 0) {
                subTitle.setTextColor(ContextCompat.getColor(activity, builder.getSubtitleColor()));
            }
            if (builder.getBody() != null) {
                body.setText(CommonUtils.fromHtml(builder.getBody()));
            } else {
                body.setVisibility(View.GONE);
            }
            body.setText(CommonUtils.fromHtml(builder.getBody()));
            if (builder.getBodyColor() != 0) {
                body.setTextColor(ContextCompat.getColor(activity, builder.getBodyColor()));
            }

            if (builder.getPositiveButtonText() != null) {
                positive.setText(builder.getPositiveButtonText());
                if (builder.getPositiveTextColor() != 0) {
                    positive.setTextColor(ContextCompat.getColor(activity, builder.getPositiveTextColor()));
                }
                if (builder.getOnPositiveClicked() != null) {
                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.getOnPositiveClicked().OnClick(v, dialog);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                positive.setVisibility(View.GONE);
            }
            if (builder.getNegativeButtonText() != null) {
                negative.setText(builder.getNegativeButtonText());
                if (builder.getNegativeColor() != 0) {
                    negative.setTextColor(ContextCompat.getColor(activity, builder.getNegativeColor()));
                }
                if (builder.getOnNegativeClicked() != null) {
                    negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.getOnNegativeClicked().OnClick(v, dialog);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                negative.setVisibility(View.GONE);
            }

            if (builder.getNeutralButtonText() != null) {
                neutral.setText(builder.getNeutralButtonText());
                if (builder.getNeutralColor() != 0) {
                    neutral.setTextColor(ContextCompat.getColor(activity, builder.getNeutralColor()));
                }
                if (builder.getOnNeutralClicked() != null) {
                    neutral.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.getOnNeutralClicked().OnClick(v, dialog);
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                neutral.setVisibility(View.GONE);
            }

            if (builder.getImageResource() != 0) {
                Drawable imageRes = VectorDrawableCompat.create(activity.getResources(), builder.getImageResource(), activity.getTheme());
                image.setImageDrawable(imageRes);
            } else if (builder.getImageDrawable() != null) {
                image.setImageDrawable(builder.getImageDrawable());
            } else {
                image.setVisibility(View.GONE);
            }

            if (builder.getBackgroundColor() != 0) {
                cardView.setCardBackgroundColor(ContextCompat.getColor(activity, builder.getBackgroundColor()));
            }

            if (builder.isAutoHide()) {
                int time = builder.getTimeToHide() != 0 ? builder.getTimeToHide() : 10000;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null) dialog.dismiss();
                    }
                }, time);
            }

            if (builder.getTitleFont() != null) {
                title.setTypeface(builder.getTitleFont());
            }

            if (builder.getSubTitleFont() != null) {
                subTitle.setTypeface(builder.getSubTitleFont());
            }

            if (builder.getBodyFont() != null) {
                body.setTypeface(builder.getBodyFont());
            }

            if (builder.getPositiveButtonFont() != null) {
                positive.setTypeface(builder.getPositiveButtonFont());
            }
            if (builder.getNegativeButtonFont() != null) {
                negative.setTypeface(builder.getNegativeButtonFont());
            }
            if (builder.getNeutralButtonFont() != null) {
                negative.setTypeface(builder.getNeutralButtonFont());
            }

            if (builder.getAlertFont() != null) {
                title.setTypeface(builder.getAlertFont());
                subTitle.setTypeface(builder.getAlertFont());
                body.setTypeface(builder.getAlertFont());
                positive.setTypeface(builder.getAlertFont());
                negative.setTypeface(builder.getAlertFont());
                neutral.setTypeface(builder.getAlertFont());
            }

            if (builder.getButtonsGravity() != null) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                switch (builder.getButtonsGravity()) {
                    case LEFT:
                        params.gravity = Gravity.START;
                        break;
                    case RIGHT:
                        params.gravity = Gravity.END;
                        break;
                    case CENTER:
                        params.gravity = Gravity.CENTER;
                        break;
                }
                if (buttonsPanel != null)
                    buttonsPanel.setLayoutParams(params);
            }

            if (builder.getTitleGravity() != null) {
                switch (builder.getTitleGravity()) {
                    case LEFT:
                        title.setGravity(Gravity.START); break;
                    case RIGHT:
                        title.setGravity(Gravity.END); break;
                }
            }

            if (builder.getSubtitleGravity() != null) {
                switch (builder.getSubtitleGravity()) {
                    case LEFT:
                        subTitle.setGravity(Gravity.START); break;
                    case RIGHT:
                        subTitle.setGravity(Gravity.END); break;
                }
            }

            if (builder.getBodyGravity() != null) {
                switch (builder.getBodyGravity()) {
                    case LEFT:
                        body.setGravity(Gravity.START); break;
                    case RIGHT:
                        body.setGravity(Gravity.END); break;
                }
            }

        dialog.show();

    }

//    private static void initViews(Dialog view) {
//        cardView = (CardView) view.findViewById(R.id.card_view);
//        image = (AppCompatImageView) view.findViewById(R.id.image);
//        title = (TextView) view.findViewById(R.id.title);
//        subTitle = (TextView) view.findViewById(R.id.sub_title);
//        body = (TextView) view.findViewById(R.id.body);
//        positive = (Button) view.findViewById(R.id.position);
//        negative = (Button) view.findViewById(R.id.negative);
//        neutral = (Button) view.findViewById(R.id.neutral);
//        buttonsPanel = (LinearLayout) view.findViewById(R.id.buttons_panel);
//    }


    public interface OnPositiveClicked {
        void OnClick(View view, Dialog dialog);
    }

    public interface OnNegativeClicked {
        void OnClick(View view, Dialog dialog);
    }

    public interface OnNeutralClicked {
        void OnClick(View view, Dialog dialog);
    }

    public static enum PanelGravity {
        LEFT,
        RIGHT,
        CENTER
    }

    public static enum TextGravity {
        LEFT,
        RIGHT,
        CENTER
    }

}