package com.urjc.backend.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class AssistanceCareersValidator implements ConstraintValidator<AssistanceCareersConstraint, List<String>> {

    @Override
    public boolean isValid(List<String> assistanceCareers, ConstraintValidatorContext cxt) {
        if(assistanceCareers == null){
            return true;
        }

        Pattern pattern = Pattern.compile("[^\\[\\]<>'\";!=]*");

        for (String assistanceCareer: assistanceCareers) {
             if(!pattern.matcher(assistanceCareer).matches()){
                return false;
             }
        }
        return true;
    }

}