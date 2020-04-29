package pl.edu.agh.airsystem.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import pl.edu.agh.airsystem.model.database.Measurement;
import pl.edu.agh.airsystem.model.database.Sensor;
import pl.edu.agh.airsystem.repository.MeasurementRepository;
import pl.edu.agh.airsystem.repository.SensorRepository;
import pl.edu.agh.airsystem.util.SensorUtilsService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static pl.edu.agh.airsystem.util.RandomUtils.randomBetween;

@Slf4j
public class LinearGeneratorMeasurement
        implements GeneratorMeasurementDefinition {
    private final double min;
    private final double max;
    private final double minStep;
    private final double maxStep;
    private final Duration stepAfterMin;
    private final Duration stepAfterMax;
    private int direction = 1;
    private double currentValue;

    public LinearGeneratorMeasurement(double min, double max,
                                      double minStep, double maxStep,
                                      Duration stepAfterMin, Duration stepAfterMax) {
        this.min = min;
        this.max = max;
        this.minStep = minStep;
        this.maxStep = maxStep;
        this.stepAfterMin = stepAfterMin;
        this.stepAfterMax = stepAfterMax;
        currentValue = randomBetween(min, max);
    }

    public Duration getTimeStep() {
        return Duration.ofSeconds((long) randomBetween(stepAfterMin.getSeconds(), stepAfterMax.getSeconds()));
    }

    public long getValueStep() {
        return (long) randomBetween(minStep, maxStep);
    }

    private void generateNextValue() {
        currentValue += getValueStep() * direction;
        if (currentValue > max) {
            currentValue = max;
            direction = -1;
        } else if (currentValue < min) {
            currentValue = min;
            direction = 1;
        }
    }

    private void generateAndAddNewMeasurement(MeasurementRepository measurementRepository,
                                              SensorRepository sensorRepository, Sensor sensor, LocalDateTime current) {
        Measurement measurement = new Measurement(
                sensor,
                current,
                currentValue);
        sensor.getMeasurements().add(measurement);
        sensor.setLatestMeasurement(measurement);
        measurementRepository.save(measurement);
        sensorRepository.save(sensor);
    }

    public void catchUpOnMeasurements(Sensor sensor,
                                      SensorRepository sensorRepository,
                                      SensorUtilsService sensorUtilsService,
                                      MeasurementRepository repository,
                                      TaskScheduler taskScheduler) {
        LocalDateTime from;
        LocalDateTime current;
        LocalDateTime to = LocalDateTime.now();

        Optional<Measurement> measurement = sensorUtilsService.findLatestMeasurementInSensor(sensor);
        if (measurement.isPresent()) {
            from = measurement.get().getTimestamp();
        } else {
            from = LocalDateTime.now().minusDays(1);
        }

        current = LocalDateTime.from(from).plus(getTimeStep());
        while (current.isBefore(to)) {
            generateNextValue();
            generateAndAddNewMeasurement(repository, sensorRepository, sensor, current);
            current = current.plus(getTimeStep());
        }
    }

    @Override
    public void startMeasurementsGenerator(Sensor sensor,
                                           SensorRepository sensorRepository,
                                           SensorUtilsService sensorUtilsService,
                                           MeasurementRepository repository,
                                           TaskScheduler taskScheduler) {
        catchUpOnMeasurements(sensor, sensorRepository, sensorUtilsService, repository, taskScheduler);
        scheduleNextIteration(sensor, sensorRepository, sensorUtilsService, repository, taskScheduler);
    }

    private void generatorIteration(Sensor sensor,
                                    SensorRepository sensorRepository,
                                    SensorUtilsService sensorUtilsService,
                                    MeasurementRepository repository,
                                    TaskScheduler taskScheduler) {
        generateNextValue();
        generateAndAddNewMeasurement(repository, sensorRepository, sensor, LocalDateTime.now());
        scheduleNextIteration(sensor, sensorRepository, sensorUtilsService, repository, taskScheduler);
    }

    private void scheduleNextIteration(Sensor sensor,
                                       SensorRepository sensorRepository,
                                       SensorUtilsService sensorUtilsService,
                                       MeasurementRepository repository,
                                       TaskScheduler taskScheduler) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.plus(getTimeStep());

        taskScheduler.schedule(() -> generatorIteration(sensor, sensorRepository, sensorUtilsService, repository, taskScheduler),
                next.atZone(ZoneId.systemDefault()).toInstant());
    }

}
