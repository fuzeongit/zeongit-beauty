package com.zeongit.data.database.admin.entity

import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "nsfw_level")
@EntityListeners(AuditingEntityListener::class)
class NsfwLevel() : Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 10)
    var id: Int? = null

    @Column(name = "url", length = 100)
    lateinit var url: String

    @Column(name = "drawings")
    var drawings: Float = 0F

    @Column(name = "hentai")
    var hentai: Float = 0F

    @Column(name = "neutral")
    var neutral: Float = 0F

    @Column(name = "porn")
    var porn: Float = 0F

    @Column(name = "sexy")
    var sexy: Float = 0F

    @Column(name = "classify", length = 100)
    lateinit var classify: String

    constructor(
            url: String,
            drawings: Float,
            hentai: Float,
            neutral: Float,
            porn: Float,
            sexy: Float,
            classify: String
    ) : this() {
        this.url = url
        this.drawings = drawings
        this.hentai = hentai
        this.neutral = neutral
        this.porn = porn
        this.sexy = sexy
        this.classify = classify
    }
}