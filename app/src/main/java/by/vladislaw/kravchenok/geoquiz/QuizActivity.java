package by.vladislaw.kravchenok.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = QuizActivity.class.getSimpleName();
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERS = "answers";
    private static final String KEY_ANSWERS_QUANTITY = "answers quantity";
    private static final String KEY_CORRECT_ANSWERS = "correct answers";
    private static final String KEY_IS_CHEATER_ANSWERS = "is cheater answers";
    private static final int REQUEST_CODE_CHEAT = 0;

    private TextView mQuestionTextView;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mPreviousButton;
    private ImageButton mNextButton;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_ocean, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;
    private boolean[] mAnswers = new boolean[]{
            false, false, false, false, false, false, false,
    };
    private int mAnswersQuantity = 0;
    private boolean[] mCorrectAnswers = new boolean[]{
            false, false, false, false, false, false, false,
    };
    private boolean[] mIsCheaterAnswers = new boolean[]{
            false, false, false, false, false, false, false,
    };

    private int mAvailableTips = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);


        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        updateQuestion();
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswers[mCurrentIndex] = true;
                checkAnswer(true);
                blockInputButtons();
                showResult();
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnswers[mCurrentIndex] = true;
                checkAnswer(false);
                blockInputButtons();
                showResult();
            }
        });
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue, mAvailableTips);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex == 0 ? mQuestionBank.length - 1 : mCurrentIndex - 1) % mQuestionBank.length;
                updateQuestion();
                blockInputButtons();
            }
        });
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
                blockInputButtons();
            }
        });
        blockInputButtons();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERS, mAnswers);
        outState.putInt(KEY_ANSWERS_QUANTITY, mAnswersQuantity);
        outState.putBooleanArray(KEY_CORRECT_ANSWERS, mCorrectAnswers);
        outState.putBooleanArray(KEY_IS_CHEATER_ANSWERS, mIsCheaterAnswers);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
        mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        mAnswers = savedInstanceState.getBooleanArray(KEY_ANSWERS);
        mAnswersQuantity = savedInstanceState.getInt(KEY_ANSWERS_QUANTITY, 0);
        mCorrectAnswers = savedInstanceState.getBooleanArray(KEY_CORRECT_ANSWERS);
        mIsCheaterAnswers = savedInstanceState.getBooleanArray(KEY_IS_CHEATER_ANSWERS);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        mAnswersQuantity++;

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;
        if (mIsCheaterAnswers[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                mCorrectAnswers[mCurrentIndex] = true;
                messageResId = R.string.correct_toast;
            } else {
                mCorrectAnswers[mCurrentIndex] = false;
                messageResId = R.string.incorrect_toast;
            }

        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void showResult() {
        if (mAnswersQuantity == mQuestionBank.length) {
            int correctAnswers = 0;
            for (Boolean correct : mCorrectAnswers) {
                if (correct) {
                    correctAnswers++;
                }
            }


            Toast.makeText(this, (correctAnswers == 0 ? 0 : (correctAnswers * 100) / mQuestionBank.length) + "% of correct answers", Toast.LENGTH_LONG).show();
        }
    }

    private void blockInputButtons() {
        if (mAnswers[mCurrentIndex]) {
            mFalseButton.setEnabled(false);
            mTrueButton.setEnabled(false);
        } else {
            mFalseButton.setEnabled(true);
            mTrueButton.setEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) return;
            mIsCheaterAnswers[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
            mAvailableTips = CheatActivity.getAvailableTips(data);
        }
    }
}
