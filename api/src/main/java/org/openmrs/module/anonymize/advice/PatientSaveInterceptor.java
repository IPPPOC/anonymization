package org.openmrs.module.anonymize.advice;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.springframework.aop.MethodBeforeAdvice;

import java.io.File;
import java.lang.reflect.Method;

public class PatientSaveInterceptor implements MethodBeforeAdvice {
    private static final String SAVE_PATIENT_METHOD = "savePatient";
    private static final String ANONYMISE = "AnonymisePatientData";
    private static final String IS_ALREADY_ANONYMISED = "isAlreadyAnonymised";

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.getName().equals(SAVE_PATIENT_METHOD)) {
            Patient patient = (Patient) args[0];
            if(patient != null) {
                Boolean anonymise = patient.getAttribute(ANONYMISE) != null && patient.getAttribute(ANONYMISE).getValue().equals("true");
                Boolean alreadyAnonymised = patient.getAttribute(IS_ALREADY_ANONYMISED) != null && patient.getAttribute(IS_ALREADY_ANONYMISED).getValue().equals("true");
                if(anonymise && !alreadyAnonymised) {
                    PersonName personName = patient.getPersonName();
                    if (personName != null) {
                        setAlreadyAnonymisedAttribute(patient);
                        randomizePatientNames(personName);
                        deletePatientImage(patient.getUuid());
                    }
                }
            }
        }
    }

    private void randomizePatientNames(PersonName personName) {
        personName.setFamilyName(RandomStringUtils.randomAlphabetic(5));
        personName.setGivenName(RandomStringUtils.randomAlphabetic(5));
        personName.setMiddleName(RandomStringUtils.randomAlphabetic(5));
    }

    private void setAlreadyAnonymisedAttribute(Patient patient) {
        PersonAttributeType alreadyAnonymised = Context.getPersonService().getPersonAttributeTypeByName(IS_ALREADY_ANONYMISED);
        if(alreadyAnonymised != null) {
            patient.addAttribute(new PersonAttribute(alreadyAnonymised, "true"));
        }
    }

    private void deletePatientImage(String uuid) {
        String imagesDirectory = Context.getAdministrationService().getGlobalProperty("emr.personImagesDirectory");
        if(uuid != null && imagesDirectory != null)
            FileUtils.deleteQuietly(new File(imagesDirectory + "/" + uuid + ".jpeg"));
    }
}
