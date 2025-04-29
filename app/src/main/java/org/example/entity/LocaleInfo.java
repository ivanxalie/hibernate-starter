package org.example.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class LocaleInfo {
    @JdbcTypeCode(SqlTypes.CHAR)
    private String lang;
    private String description;
}
