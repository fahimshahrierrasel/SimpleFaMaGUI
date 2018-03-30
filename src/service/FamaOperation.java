package service;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.*;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

import java.util.Iterator;

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
            sb.append("RODUCT ").append(i).append(" of ").append(imax).append(".\nFeatures: ");
            Iterator<GenericFeature> it2 = p.getFeatures().iterator();
            while (it2.hasNext()){
                sb.append(it2.next().getName() + ", ");
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
                output.append(GetProducts());
                break;
        }

        return output.toString();
    }
}
