package com.qualaroo.ui.render;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.LongSparseArray;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.qualaroo.R;
import com.qualaroo.internal.model.QScreen;
import com.qualaroo.internal.model.Question;
import com.qualaroo.ui.OnLeadGenAnswerListener;
import com.qualaroo.util.DebouncingOnClickListener;
import com.qualaroo.util.TextWatcherAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LeadGenRenderer {

    private static final String FIELD_TYPE_FIRST_NAME = "first_name";
    private static final String FIELD_TYPE_LAST_NAME = "last_name";
    private static final String FIELD_TYPE_PHONE = "phone";
    private static final String FIELD_TYPE_EMAIL = "email";

    private final Theme theme;

    LeadGenRenderer(Theme theme) {
        this.theme = theme;
    }

    public View render(Context context, QScreen qScreen, final List<Question> questions, final OnLeadGenAnswerListener onLeadGenAnswerListener) {
        final View view = LayoutInflater.from(context).inflate(R.layout.qualaroo__view_question_lead_gen, null);

        final Button button = view.findViewById(R.id.qualaroo__view_question_lead_gen_confirm);
        button.setText(qScreen.sendText());
        ThemeUtils.applyTheme(button, theme);

        final ViewGroup parent = view.findViewById(R.id.qualaroo__view_question_lead_gen_input_fields);
        final LongSparseArray<TextInputLayout> fields = new LongSparseArray<>();
        final List<EditText> requiredFields = new ArrayList<>();
        for (Question question : questions) {
            TextInputLayout inputField = buildTextInput(context, question);
            parent.addView(inputField);
            if (question.isRequired()) {
                button.setEnabled(false);
                requiredFields.add(inputField.getEditText());
                inputField.getEditText().addTextChangedListener(new TextWatcherAdapter() {
                    @Override public void afterTextChanged(Editable s) {
                        boolean enableButton = true;
                        for (EditText requiredField : requiredFields) {
                            if (requiredField.length() == 0) {
                                enableButton = false;
                            }
                        }
                        button.setEnabled(enableButton);
                    }
                });
            }
            fields.append(question.id(), inputField);
        }

        button.setOnClickListener(new DebouncingOnClickListener() {
            @Override public void doClick(View v) {
                Map<Long, String> answers = new HashMap<>(questions.size());
                for (Question question : questions) {
                    TextInputLayout inputLayout = fields.get(question.id());
                    String answer = inputLayout.getEditText().getText().toString();
                    answers.put(question.id(), answer);
                }
                onLeadGenAnswerListener.onLeadGenAnswered(answers);
            }
        });
        return view;
    }

    private TextInputLayout buildTextInput(Context context, Question question) {
        TextInputLayout inputLayout = new TextInputLayout(context);
        TextInputEditText editText = new TextInputEditText(context);
        String hint = question.title();
        if (question.isRequired()) {
            hint = hint.concat(" *");
        }
        editText.setHint(hint);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        String fieldType = question.cname();
        if (FIELD_TYPE_FIRST_NAME.equals(fieldType) || FIELD_TYPE_LAST_NAME.equals(fieldType)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        } else if (FIELD_TYPE_PHONE.equals(fieldType)) {
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        } else if (FIELD_TYPE_EMAIL.equals(fieldType)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        editText.setMaxLines(1);
        inputLayout.addView(editText);
        ThemeUtils.applyTheme(inputLayout, theme);
        ThemeUtils.applyTheme(editText, theme);
        return inputLayout;
    }

}
