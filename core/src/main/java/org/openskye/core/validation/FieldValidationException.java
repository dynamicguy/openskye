package org.openskye.core.validation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FieldValidationException extends RuntimeException
{
    private List<FieldValidationDetail> details = new ArrayList<>();

    public void addDetail(FieldValidationDetail detail)
    {
        details.add(detail);
    }
}
