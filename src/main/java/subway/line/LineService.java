package subway.line;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.station.Station;
import subway.station.StationResponse;
import subway.station.StationService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private final StationService stationService;
    private final LineRepository lineRepository;

    public LineService(StationService stationService, LineRepository lineRepository) {
        this.stationService = stationService;
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse saveLine(LineRequest lineRequest) {

        Station upStation = stationService.findOneStation(lineRequest.getUpStationId());
        Station downStation = stationService.findOneStation(lineRequest.getDownStationId());

        Line line = new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation);

        Line savedLine = lineRepository.save(line);

        return createLineResponse(savedLine);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findOneLine(Long id) {
        Line line = findOne(id);
        return createLineResponse(line);
    }

    @Transactional
    public void updateLine(Long id, LineRequest lineRequest) {
        Line line = findOne(id);

        line.update(lineRequest.getName(), lineRequest.getColor());

        lineRepository.save(line);
    }

    @Transactional
    public void deleteLine(Long id) {
        lineRepository.deleteById(id);
    }

    private Line findOne(Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("없는 노선 입니다"));
    }

    private LineResponse createLineResponse(Line line) {
        List<StationResponse> stationResponses = line.getStations()
                .stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());

        return LineResponse.builder()
                .id(line.getId())
                .name(line.getName())
                .color(line.getColor())
                .stations(stationResponses)
                .build();
    }
}
