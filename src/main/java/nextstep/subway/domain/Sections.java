package nextstep.subway.domain;

import nextstep.subway.exception.SectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    private static String DOWN_STATION_REGISTERED_ERROR_MASSAGE = "하행역만 상행역으로 등록될 수 있습니다.";
    private static String SECTION_STATION_REGISTERED_ERROR_MASSAGE = "이미 구간에 등록되어 있습니다.";

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public List<Station> getStations() {
        List<Station> result = new ArrayList<>();
        if (sections.size() > 0) {
            sections.forEach(sec
                    -> result.add(sec.getUpStation()));
            result.add(sections.get(sections.size() - 1).getDownStation());
        }
        return result;
    }

    public void validationSectionStation(Station upStation, Station downStation) {
        if (!isDownStationRegistered(upStation)) {
            throw new SectionException(DOWN_STATION_REGISTERED_ERROR_MASSAGE);
        }

        if (isSectionStationRegistered(downStation)) {
            throw new SectionException(SECTION_STATION_REGISTERED_ERROR_MASSAGE);
        }
    }

    private boolean isDownStationRegistered(Station station) {
        return sections.size() == 0
                || sections.stream()
                        .anyMatch(sec -> sec.isDownStationRegistered(station));
    }

    private boolean isSectionStationRegistered(Station station) {
        return sections.stream()
                        .anyMatch(sec -> sec.isSectionStationRegistered(station));
    }
}
