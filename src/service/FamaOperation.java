package service;

import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.questions.*;
import es.us.isa.FAMA.errors.Error;
import es.us.isa.FAMA.errors.Explanation;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.GenericRelation;
import es.us.isa.FAMA.models.featureModel.Product;
import es.us.isa.FAMA.models.variabilityModel.GenericProduct;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

import java.util.Collection;
import java.util.Iterator;

public class FamaOperation {
    private QuestionTrader mQuestionTrader;
    private VariabilityModel mVariabilityModel;

    public FamaOperation(String filePath) {
        mQuestionTrader = new QuestionTrader();

        mVariabilityModel = mQuestionTrader.openFile(filePath);
        mQuestionTrader.setVariabilityModel(mVariabilityModel);
    }

    private String checkValidation() {
        ValidQuestion validQuestion = (ValidQuestion) mQuestionTrader.createQuestion("Valid");
        mQuestionTrader.ask(validQuestion);
        if(validQuestion.isValid())
            return "Your feature model is valid";
        else
            return "Your feature model is not valid";
    }

    private String numberOfProducts() {
        ValidQuestion validQuestion = (ValidQuestion) mQuestionTrader.createQuestion("Valid");
        mQuestionTrader.ask(validQuestion);
        if(validQuestion.isValid()) {
            NumberOfProductsQuestion npq = (NumberOfProductsQuestion) mQuestionTrader
                    .createQuestion("#Products");
            mQuestionTrader.ask(npq);
            return "The number of products is: " + npq.getNumberOfProducts();
        } else {
            return ("Your feature model is not valid");
        }
    }

    private String GetProducts() {
        StringBuilder sb = new StringBuilder();
        Question q = mQuestionTrader.createQuestion("Products");
        mQuestionTrader.ask(q);
        ProductsQuestion pq = (ProductsQuestion) q;
        long imax = pq.getNumberOfProducts();
        Iterator<? extends GenericProduct> it = pq.getAllProducts().iterator();
        int i = 0;
        while (it.hasNext()) {
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

    private String getVariability() {
        VariabilityQuestion vq = (VariabilityQuestion) mQuestionTrader.createQuestion("Variability");
        mQuestionTrader.ask(vq);
        return String.valueOf(vq.getVariability());
    }

    private String getErrorDetection() {
        StringBuilder sb = new StringBuilder();
        DetectErrorsQuestion vq = (DetectErrorsQuestion) mQuestionTrader.createQuestion("DetectErrors");
        vq.setObservations(mVariabilityModel.getObservations());
        mQuestionTrader.ask(vq);
        for (Error e : vq.getErrors()) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }

    private String getErrorExplanations() {
        StringBuilder sb = new StringBuilder();

        DetectErrorsQuestion q = (DetectErrorsQuestion) mQuestionTrader.createQuestion("DetectErrors");
        q.setObservations(mVariabilityModel.getObservations());
        mQuestionTrader.ask(q);

        Collection<Error> errors = q.getErrors();
        ExplainErrorsQuestion qe = (ExplainErrorsQuestion) mQuestionTrader.createQuestion("Explanations");
        qe.setErrors(errors);
        mQuestionTrader.ask(qe);
        errors = qe.getErrors();

        for (Error e : errors) {
            Collection<Explanation> explanations = e.getExplanations();
            Iterator<Explanation> itExp = explanations.iterator();
            sb.append("Explanations for error ").append(e).append("\n");
            sb.append(explanations.size()).append(" Explanations").append("\n");
            sb.append("------------------------").append("\n");
            while (itExp.hasNext()) {
                Explanation exp = itExp.next();
                Collection<GenericRelation> relations = exp.getRelations();
                for (GenericRelation rel : relations) {
                    sb.append("[").append(rel.getName()).append("]\n");
                    sb.append("------------------------").append("\n");
                }
            }
        }
        return sb.toString();
    }

    public String getOperationOutput(String operationName) {
        StringBuilder output = new StringBuilder();

        switch (operationName) {
            case "Validation":
                output.append(checkValidation());
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
            case "Error Detection":
                output.append(getErrorDetection());
                break;
            case "Error Explanations":
                output.append(getErrorExplanations());
                break;
            default:
                output.append("Not Implemented Yet");
                break;
        }

        return output.toString();
    }
}
