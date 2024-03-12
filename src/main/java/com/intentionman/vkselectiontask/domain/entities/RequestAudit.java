package com.intentionman.vkselectiontask.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests_audit")
public class RequestAudit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "created__date", nullable = false, updatable = false)
    @CreatedDate
    private Date createdDate;

    @Column(name = "created__by")
    @CreatedBy
    private String createdBy;


    @Column(name = "method")
    private String method;

    @Column(name = "path")
    private String path;

    @Column(name = "has_necessary_authority")
    private boolean hasNecessaryAuthority;

    public RequestAudit(String method, String path, boolean hasNecessaryAuthority) {
        this.method = method;
        this.path = path;
        this.hasNecessaryAuthority = hasNecessaryAuthority;
    }
}
