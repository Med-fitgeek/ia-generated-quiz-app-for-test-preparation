package com.fitgeek.IATestPreparator.entities;

import com.fitgeek.IATestPreparator.entities.enums.SourceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "sources",
        indexes = {
                @Index(name = "idx_source_owner", columnList = "owner_id"),
                @Index(name = "idx_source_checksum", columnList = "checksum"),
                @Index(name = "idx_source_owner_checksum", columnList = "owner_id, checksum")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class KnowledgeSource extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String normalizedContent;

    private String originalFilename;

    private String storagePath;

    @Column(nullable = false)
    private String checksum;
}
