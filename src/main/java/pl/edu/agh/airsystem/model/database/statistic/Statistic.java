package pl.edu.agh.airsystem.model.database.statistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.edu.agh.airsystem.model.database.Station;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"station_db_id", "id"})})
public abstract class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long dbId;

    private String id;

    @ManyToOne
    @JoinColumn(name = "station_db_id")
    private Station station;

    private StatisticType statisticType;

    private StatisticPrivacyMode statisticPrivacyMode;

    public Statistic(String id, StatisticType statisticType, StatisticPrivacyMode statisticPrivacyMode) {
        this.id = id;
        this.statisticType = statisticType;
        this.statisticPrivacyMode = statisticPrivacyMode;
    }

}
