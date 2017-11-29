package org.openmrs.module.anonymize.advice;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.File;
import java.lang.reflect.Method;

public class PatientSaveInterceptor implements MethodBeforeAdvice {
    private static final String SAVE_PATIENT_METHOD = "savePatient";
    private static final String ANONYMISE = "Consent for anonymization";

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals(SAVE_PATIENT_METHOD)) {
            Patient patient = (Patient) args[0];
            if(patient != null && patient.getAttribute(ANONYMISE) != null
                    && patient.getAttribute(ANONYMISE).getValue().equals("true")) {
                PersonName personName = patient.getPersonName();
                if(personName != null) {
                    personName.setFamilyName(RandomStringUtils.randomAlphabetic(5));
                    personName.setGivenName(RandomStringUtils.randomAlphabetic(5));
                    personName.setMiddleName(RandomStringUtils.randomAlphabetic(5));
                    String imagesDirectory = Context.getAdministrationService().getGlobalProperty("emr.personImagesDirectory");
                    if(patient.getUuid() != null && imagesDirectory != null)
                        FileUtils.deleteQuietly(new File(imagesDirectory + "/" + patient.getUuid() + ".jpeg"));
                }
            }
        }
    }
}
