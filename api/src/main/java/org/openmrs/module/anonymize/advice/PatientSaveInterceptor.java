package org.openmrs.module.anonymize.advice;

import org.apache.commons.lang.RandomStringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class PatientSaveInterceptor implements MethodBeforeAdvice {
    private static final String SAVE_PATIENT_METHOD = "savePatient";
    private static final String ANONYMISE = "Consent for anonymization";

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals(SAVE_PATIENT_METHOD)) {
            Patient patient = (Patient) args[0];
            PersonAttribute personAttribute = patient != null ? patient.getAttribute(ANONYMISE) : null;
            if(personAttribute != null && personAttribute.getValue().equals("true")) {
                PersonName personName = patient.getPersonName();
                if(personName != null) {
                    personName.setFamilyName(RandomStringUtils.randomAlphabetic(5));
                    personName.setGivenName(RandomStringUtils.randomAlphabetic(5));
                    personName.setMiddleName(RandomStringUtils.randomAlphabetic(5));
                    personAttribute.setValue("false");
                }
            }

        }
    }
}
