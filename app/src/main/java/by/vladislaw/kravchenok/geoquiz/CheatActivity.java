package by.vladislaw.kravchenok.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mTipsQuantityTextView;
    private TextView mDeviceVersionTextView;
    private static final String TAG = CheatActivity.class.getSimpleName();
    private static final String EXTRA_ANSWER_IS_TRUE = "by.vladislaw.kravchenok.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "by.vladislaw.kravchenok.geoquiz.answer_shown";
    private static final String EXTRA_AVAILABLE_TIPS = "by.vladislaw.kravchenok.geoquiz.available.tips";
    private static final String KEY_ANSWER_SHOWN = "answer shown";
    private static final String KEY_AVAILABLE_TIPS = "available_tips";

    private boolean mAnswerIsTrue;
    private boolean mAnswerShown;
    private int mAvailableTips;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int availableTips) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_AVAILABLE_TIPS, availableTips);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int getAvailableTips(Intent result) {
        return result.getIntExtra(EXTRA_AVAILABLE_TIPS, 3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        if (savedInstanceState != null) {
            mAnswerShown = savedInstanceState.getBoolean(KEY_ANSWER_SHOWN);
            mAvailableTips = savedInstanceState.getInt(KEY_AVAILABLE_TIPS);
            setAnswerShownResult();
        }
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mAvailableTips = getIntent().getIntExtra(EXTRA_AVAILABLE_TIPS, 3);
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswerShown = true;
                mAvailableTips--;
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                mTipsQuantityTextView.setText(String.format(getString(R.string.tips), mAvailableTips));
                setAnswerShownResult();
                disableShowAnswerButtonIfNeed();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }

            }
        });
        disableShowAnswerButtonIfNeed();
        mTipsQuantityTextView = (TextView) findViewById(R.id.available_tips_text_view);
        mTipsQuantityTextView.setText(String.format(getString(R.string.tips), mAvailableTips));
        mDeviceVersionTextView = (TextView) findViewById(R.id.device_version_text_view);
        String apiLevel = "API Level " + android.os.Build.VERSION.SDK_INT;
        mDeviceVersionTextView.setText(apiLevel);

    }

    private void setAnswerShownResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, mAnswerShown);
        data.putExtra(EXTRA_AVAILABLE_TIPS, mAvailableTips);
        setResult(Activity.RESULT_OK, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putBoolean(KEY_ANSWER_SHOWN, mAnswerShown);
        outState.putInt(KEY_AVAILABLE_TIPS, mAvailableTips);

    }

    private void disableShowAnswerButtonIfNeed() {
        if (mAvailableTips == 0) {
            mShowAnswerButton.setEnabled(false);
        } else {
            mShowAnswerButton.setEnabled(true);
        }
    }
}
