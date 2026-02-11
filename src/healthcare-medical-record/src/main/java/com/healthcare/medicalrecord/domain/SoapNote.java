package com.healthcare.medicalrecord.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoapNote {

    @Column(name = "soap_subjective", columnDefinition = "TEXT")
    private String subjective;

    @Column(name = "soap_objective", columnDefinition = "TEXT")
    private String objective;

    @Column(name = "soap_assessment", columnDefinition = "TEXT")
    private String assessment;

    @Column(name = "soap_plan", columnDefinition = "TEXT")
    private String plan;

    public static SoapNote of(String subjective, String objective, String assessment, String plan) {
        return new SoapNote(subjective, objective, assessment, plan);
    }

    public boolean isComplete() {
        return hasContent(subjective) && hasContent(objective)
                && hasContent(assessment) && hasContent(plan);
    }

    public String getSummary() {
        if (hasContent(assessment)) {
            return assessment.length() > 200 ? assessment.substring(0, 200) + "..." : assessment;
        }
        return "";
    }

    private boolean hasContent(String text) {
        return text != null && !text.isBlank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SoapNote soapNote = (SoapNote) o;
        return Objects.equals(subjective, soapNote.subjective) &&
               Objects.equals(objective, soapNote.objective) &&
               Objects.equals(assessment, soapNote.assessment) &&
               Objects.equals(plan, soapNote.plan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjective, objective, assessment, plan);
    }
}
