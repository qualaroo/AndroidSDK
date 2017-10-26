package com.qualaroo.ui.render;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.view.View;

import com.qualaroo.internal.model.Message;
import com.qualaroo.internal.model.QScreen;
import com.qualaroo.internal.model.Question;
import com.qualaroo.internal.model.QuestionType;
import com.qualaroo.ui.OnAnsweredListener;
import com.qualaroo.ui.OnLeadGenAnswerListener;
import com.qualaroo.ui.OnMessageConfirmedListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public class Renderer {

    private final Theme theme;
    private final Map<QuestionType, QuestionRenderer> questionRenderers = new HashMap<>();
    private final MessageRenderer messageRenderer;
    private final LeadGenRenderer leadGenRenderer;

    public Renderer(Theme theme) {
        this.theme = theme;
        this.messageRenderer = new MessageRenderer(theme);
        this.leadGenRenderer = new LeadGenRenderer(theme);
        this.questionRenderers.put(QuestionType.RADIO, new RadioQuestionRenderer(theme));
        this.questionRenderers.put(QuestionType.CHECKBOX, new CheckboxQuestionRenderer(theme));
        this.questionRenderers.put(QuestionType.NPS, new NpsQuestionRenderer(theme));
        this.questionRenderers.put(QuestionType.TEXT, new TextQuestionRenderer(theme));
        this.questionRenderers.put(QuestionType.UNKNOWN, new UnknownQuestionTypeRenderer(theme));
        this.questionRenderers.put(QuestionType.DROPDOWN, new DropdownQuestionRenderer(theme));
    }

    public View renderMessage(Context context, Message message, OnMessageConfirmedListener onMessageConfirmedListener) {
        return messageRenderer.render(context, message, onMessageConfirmedListener);
    }

    public QuestionView renderQuestion(Context context, Question question, OnAnsweredListener onAnsweredListener) {
        return questionRenderers.get(question.type()).render(context, question, onAnsweredListener);
    }

    public View renderLeadGen(Context context, QScreen qScreen, List<Question> questions, OnLeadGenAnswerListener onLeadGenAnswerListener) {
        return leadGenRenderer.render(context, qScreen, questions, onLeadGenAnswerListener);
    }

}
