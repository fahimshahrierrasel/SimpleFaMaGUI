package service;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.*;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

import java.util.Iterator;

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

    private String GetProducts(){
        StringBuilder sb = new StringBuilder();
        Question q = mQuestionTrader.createQuestion("Products");
        mQuestionTrader.ask(q);
        ProductsQuestion pq = (ProductsQuestion) q;
        long imax = pq.getNumberOfProducts();
        Iterator<? extends GenericProduct> it = pq.getAllProducts().iterator();
        int i = 0;
        while (it.hasNext()){
            i++;
            Product p = (Product) it.next();
            ValidProductQuestion vpq = (ValidProductQuestion) mQuestionTrader.createQuestion("ValidProduct");
            vpq.setProduct(p);
            mQuestionTrader.ask(vpq);
            sb.append("PRODUCT ").append(i).append(" of ").append(imax).append(".\nFeatures: ");
            for (GenericFeature genericFeature : p.getFeatures()) {
                sb.append(genericFeature.getName()).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private String getVariability()
    {
        VariabilityQuestion vq = (VariabilityQuestion) mQuestionTrader.createQuestion("Variability");
        mQuestionTrader.ask(vq);
        return String.valueOf(vq.getVariability());
    }

    private String getErrorExplaination()
    {
        StringBuilder sb = new StringBuilder();
        DetectErrorsQuestion vq = (DetectErrorsQuestion ) mQuestionTrader.createQuestion("DetectErrors");
        vq.setObservations(mVariabilityModel.getObservations());
        mQuestionTrader.ask(vq);
        for (Error e : vq.getErrors()) {
            sb.append(e).append("\n");
        }
        return sb.toString();
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
            case "Products":
                output.append(GetProducts());
                break;
            case "Error Explanations":
                output.append(getErrorExplaination());
                break;
            default:
                break;
        }

        return output.toString();
    }
}
