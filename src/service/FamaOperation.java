package service;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class FamaOperation {
    private QuestionTrader mQuestionTrader;
    private VariabilityModel mVariabilityModel;

    public FamaOperation(String filePath) {
        mQuestionTrader = new QuestionTrader();
        mVariabilityModel = mQuestionTrader.openFile(filePath);
        mQuestionTrader.setVariabilityModel(mVariabilityModel);
    }

    private boolean isValid() {
        ValidQuestion validQuestion = (ValidQuestion) mQuestionTrader.createQuestion("Valid");
        mQuestionTrader.ask(validQuestion);
        return validQuestion.isValid();
    }

    public String getOperationOutput(String operationName)
    {
        StringBuilder output = new StringBuilder();

        switch (operationName)
        {
            case "Validation":
                if (isValid())
                    output.append("Model is Valid");
                else
                    output.append("Model is not Valid");

                break;
            default:
                output.append("Operation is not implemented yet");
                break;
        }

        return output.toString();
    }
}
