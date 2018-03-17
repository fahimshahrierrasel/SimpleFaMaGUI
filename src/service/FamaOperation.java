package service;

import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion;
import es.us.isa.FAMA.Reasoner.questions.ValidQuestion;
import es.us.isa.FAMA.Reasoner.questions.VariabilityQuestion;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class FamaOperation {
    private QuestionTrader mQuestionTrader;

    public FamaOperation(String filePath) {
        mQuestionTrader = new QuestionTrader();
        VariabilityModel mVariabilityModel = mQuestionTrader.openFile(filePath);
        mQuestionTrader.setVariabilityModel(mVariabilityModel);
    }

    private boolean isValid() {
        ValidQuestion validQuestion = (ValidQuestion) mQuestionTrader.createQuestion("Valid");
        mQuestionTrader.ask(validQuestion);
        return validQuestion.isValid();
    }

    private String numberOfProducts(){
        if (isValid()) {
            NumberOfProductsQuestion npq = (NumberOfProductsQuestion) mQuestionTrader
                    .createQuestion("#Products");
            mQuestionTrader.ask(npq);
           return "The number of products is: " + npq.getNumberOfProducts();
        } else {
            return ("Your feature model is not valid");
        }
    }

    private String getVariability()
    {
        VariabilityQuestion vq = (VariabilityQuestion) mQuestionTrader.createQuestion("Variability");
        mQuestionTrader.ask(vq);
        return String.valueOf(vq.getVariability());
    }

    public String getOperationOutput(String operationName)
    {
        StringBuilder output = new StringBuilder();

        switch (operationName)
        {
            case "Validation":
                if (isValid())
                    output.append("Your feature model is valid");
                else
                    output.append("Your feature model is not valid");
                break;
            case "Number of Products":
                output.append(numberOfProducts());
                break;
            case "Variability":
                output.append("Model variability: ").append(getVariability());
                break;
            default:
                output.append("Operation is not implemented yet");
                break;
        }

        return output.toString();
    }
}
