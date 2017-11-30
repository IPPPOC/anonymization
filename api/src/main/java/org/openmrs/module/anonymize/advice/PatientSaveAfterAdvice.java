package org.openmrs.module.anonymize.advice;

import org.apache.commons.io.FileUtils;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;

import java.io.File;
import java.lang.reflect.Method;

public class PatientSaveAfterAdvice implements AfterReturningAdvice {
    private static final String GET_PATIENT_METHOD = "getPatientByUuid";
    private static final String ANONYMISE = "AnonymisePatientData";
    private static final String ANONYMISE_INFO = "isAlreadyAnonymised";


    private void deletePatientImage(String uuid) {
        String imagesDirectory = Context.getAdministrationService().getGlobalProperty("emr.personImagesDirectory");
        if(uuid != null && imagesDirectory != null)
            FileUtils.deleteQuietly(new File(imagesDirectory + "/" + uuid + ".jpeg"));
    }

    @Override
    public void afterReturning(Object o, Method method, Object[] objects, Object o1) throws Throwable {
        if (method.getName().equals(GET_PATIENT_METHOD)) {
            Patient patient = (Patient) o;
            if (patient != null) {
                Boolean anonymise = patient.getAttribute(ANONYMISE) != null && patient.getAttribute(ANONYMISE).getValue().equals("true");
                Boolean alreadyAnonymised = patient.getAttribute(ANONYMISE_INFO) != null && patient.getAttribute(ANONYMISE_INFO).getValue().equals("true");
                if (anonymise && !alreadyAnonymised) {
                    deletePatientImage(patient.getUuid());
                }
            }
        }
    }
}
