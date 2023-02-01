package subway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.domain.Line;
import subway.domain.LineRepository;
import subway.ui.dto.LineRequest;
import subway.ui.dto.LineResponse;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    public List<LineResponse> findAll() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findById(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("지하철 노선이 존재하지 않습니다."));

        return createLineResponse(line);
    }

    @Transactional
    public LineResponse save(LineRequest lineRequest) {
        Line line = lineRepository.save(lineRequest.toEntity());
        return createLineResponse(line);
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(line);
    }
}
