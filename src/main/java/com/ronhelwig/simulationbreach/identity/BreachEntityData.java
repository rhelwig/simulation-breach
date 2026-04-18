package com.ronhelwig.simulationbreach.identity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;

public final class BreachEntityData {
	private final OriginDisposition originDisposition;
	private final String originalEntityType;
	private final InfectionStage infectionStage;
	private final TransformationState transformationState;
	private final Long lastConversionGameTime;
	private final UUID convertedByEntityUuid;
	private final Integer conversionGeneration;
	private final Long immuneUntilGameTime;
	private final Long transformationStartedGameTime;
	private final Integer transformationDurationTicks;
	private final String pendingReplacementEntityType;
	private final InfectionStage pendingInfectionStage;
	private final String debugSource;

	private BreachEntityData(Builder builder) {
		this.originDisposition = Objects.requireNonNull(builder.originDisposition, "originDisposition");
		this.originalEntityType = requireEntityId(builder.originalEntityType, "originalEntityType");
		this.infectionStage = Objects.requireNonNull(builder.infectionStage, "infectionStage");
		this.transformationState = Objects.requireNonNull(builder.transformationState, "transformationState");
		this.lastConversionGameTime = builder.lastConversionGameTime;
		this.convertedByEntityUuid = builder.convertedByEntityUuid;
		this.conversionGeneration = builder.conversionGeneration;
		this.immuneUntilGameTime = builder.immuneUntilGameTime;
		this.transformationStartedGameTime = builder.transformationStartedGameTime;
		this.transformationDurationTicks = builder.transformationDurationTicks;
		this.pendingReplacementEntityType = builder.pendingReplacementEntityType;
		this.pendingInfectionStage = builder.pendingInfectionStage;
		this.debugSource = builder.debugSource;

		validate();
	}

	public static BreachEntityData clean(OriginDisposition originDisposition, String originalEntityType) {
		return builder(originDisposition, originalEntityType)
				.infectionStage(InfectionStage.NONE)
				.transformationState(TransformationState.NONE)
				.build();
	}

	public static Builder builder(OriginDisposition originDisposition, String originalEntityType) {
		return new Builder(originDisposition, originalEntityType);
	}

	public OriginDisposition originDisposition() {
		return originDisposition;
	}

	public String originalEntityType() {
		return originalEntityType;
	}

	public InfectionStage infectionStage() {
		return infectionStage;
	}

	public TransformationState transformationState() {
		return transformationState;
	}

	public OptionalLong lastConversionGameTime() {
		return optionalLong(lastConversionGameTime);
	}

	public Optional<UUID> convertedByEntityUuid() {
		return Optional.ofNullable(convertedByEntityUuid);
	}

	public OptionalInt conversionGeneration() {
		return optionalInt(conversionGeneration);
	}

	public OptionalLong immuneUntilGameTime() {
		return optionalLong(immuneUntilGameTime);
	}

	public OptionalLong transformationStartedGameTime() {
		return optionalLong(transformationStartedGameTime);
	}

	public OptionalInt transformationDurationTicks() {
		return optionalInt(transformationDurationTicks);
	}

	public Optional<String> pendingReplacementEntityType() {
		return Optional.ofNullable(pendingReplacementEntityType);
	}

	public Optional<InfectionStage> pendingInfectionStage() {
		return Optional.ofNullable(pendingInfectionStage);
	}

	public Optional<String> debugSource() {
		return Optional.ofNullable(debugSource);
	}

	public Builder toBuilder() {
		return builder(originDisposition, originalEntityType)
				.infectionStage(infectionStage)
				.transformationState(transformationState)
				.lastConversionGameTime(lastConversionGameTime)
				.convertedByEntityUuid(convertedByEntityUuid)
				.conversionGeneration(conversionGeneration)
				.immuneUntilGameTime(immuneUntilGameTime)
				.transformationStartedGameTime(transformationStartedGameTime)
				.transformationDurationTicks(transformationDurationTicks)
				.pendingReplacementEntityType(pendingReplacementEntityType)
				.pendingInfectionStage(pendingInfectionStage)
				.debugSource(debugSource);
	}

	public Map<String, String> toMap() {
		Map<String, String> values = new LinkedHashMap<>();
		values.put(BreachDataKeys.ORIGIN_DISPOSITION, originDisposition.name());
		values.put(BreachDataKeys.ORIGINAL_ENTITY_TYPE, originalEntityType);
		values.put(BreachDataKeys.INFECTION_STAGE, infectionStage.name());
		values.put(BreachDataKeys.TRANSFORMATION_STATE, transformationState.name());
		putLong(values, BreachDataKeys.LAST_CONVERSION_GAME_TIME, lastConversionGameTime);
		putUuid(values, BreachDataKeys.CONVERTED_BY_ENTITY_UUID, convertedByEntityUuid);
		putInt(values, BreachDataKeys.CONVERSION_GENERATION, conversionGeneration);
		putLong(values, BreachDataKeys.IMMUNE_UNTIL_GAME_TIME, immuneUntilGameTime);
		putLong(values, BreachDataKeys.TRANSFORMATION_STARTED_GAME_TIME, transformationStartedGameTime);
		putInt(values, BreachDataKeys.TRANSFORMATION_DURATION_TICKS, transformationDurationTicks);
		putString(values, BreachDataKeys.PENDING_REPLACEMENT_ENTITY_TYPE, pendingReplacementEntityType);
		putEnum(values, BreachDataKeys.PENDING_INFECTION_STAGE, pendingInfectionStage);
		putString(values, BreachDataKeys.DEBUG_SOURCE, debugSource);
		return values;
	}

	public static BreachEntityData fromMap(Map<String, String> values) {
		Objects.requireNonNull(values, "values");
		Builder builder = builder(
				readEnum(values, BreachDataKeys.ORIGIN_DISPOSITION, OriginDisposition.class),
				requireMapValue(values, BreachDataKeys.ORIGINAL_ENTITY_TYPE)
		);
		builder.infectionStage(readEnum(values, BreachDataKeys.INFECTION_STAGE, InfectionStage.class));
		builder.transformationState(readEnum(values, BreachDataKeys.TRANSFORMATION_STATE, TransformationState.class));
		builder.lastConversionGameTime(readOptionalLong(values, BreachDataKeys.LAST_CONVERSION_GAME_TIME));
		builder.convertedByEntityUuid(readOptionalUuid(values, BreachDataKeys.CONVERTED_BY_ENTITY_UUID));
		builder.conversionGeneration(readOptionalInt(values, BreachDataKeys.CONVERSION_GENERATION));
		builder.immuneUntilGameTime(readOptionalLong(values, BreachDataKeys.IMMUNE_UNTIL_GAME_TIME));
		builder.transformationStartedGameTime(readOptionalLong(values, BreachDataKeys.TRANSFORMATION_STARTED_GAME_TIME));
		builder.transformationDurationTicks(readOptionalInt(values, BreachDataKeys.TRANSFORMATION_DURATION_TICKS));
		builder.pendingReplacementEntityType(readOptionalEntityId(values, BreachDataKeys.PENDING_REPLACEMENT_ENTITY_TYPE));
		builder.pendingInfectionStage(readOptionalEnum(values, BreachDataKeys.PENDING_INFECTION_STAGE, InfectionStage.class));
		builder.debugSource(emptyToNull(values.get(BreachDataKeys.DEBUG_SOURCE)));
		return builder.build();
	}

	private void validate() {
		validateNonNegative(lastConversionGameTime, "lastConversionGameTime");
		validateNonNegative(conversionGeneration, "conversionGeneration");
		validateNonNegative(immuneUntilGameTime, "immuneUntilGameTime");
		validateNonNegative(transformationStartedGameTime, "transformationStartedGameTime");
		validatePositive(transformationDurationTicks, "transformationDurationTicks");

		if (pendingReplacementEntityType != null) {
			requireEntityId(pendingReplacementEntityType, "pendingReplacementEntityType");
		}

		if (transformationState == TransformationState.TRANSFORMING) {
			if (transformationStartedGameTime == null || transformationDurationTicks == null
					|| pendingReplacementEntityType == null || pendingInfectionStage == null) {
				throw new IllegalArgumentException("transforming breach data requires start time, duration, replacement entity type, and pending infection stage");
			}
		}

		if (transformationState == TransformationState.NONE
				&& (transformationStartedGameTime != null || transformationDurationTicks != null
				|| pendingReplacementEntityType != null || pendingInfectionStage != null)) {
			throw new IllegalArgumentException("non-transforming breach data must not carry pending transformation fields");
		}
	}

	private static String requireEntityId(String value, String name) {
		String checked = Objects.requireNonNull(value, name).trim();
		if (checked.isEmpty() || checked.indexOf(':') <= 0 || checked.indexOf(':') == checked.length() - 1) {
			throw new IllegalArgumentException(name + " must be a namespaced entity id");
		}
		return checked;
	}

	private static String requireMapValue(Map<String, String> values, String key) {
		String value = values.get(key);
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException("missing breach data key: " + key);
		}
		return value;
	}

	private static <E extends Enum<E>> E readEnum(Map<String, String> values, String key, Class<E> enumType) {
		return Enum.valueOf(enumType, requireMapValue(values, key));
	}

	private static <E extends Enum<E>> E readOptionalEnum(Map<String, String> values, String key, Class<E> enumType) {
		String value = emptyToNull(values.get(key));
		if (value == null) {
			return null;
		}
		return Enum.valueOf(enumType, value);
	}

	private static Integer readOptionalInt(Map<String, String> values, String key) {
		String value = emptyToNull(values.get(key));
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}

	private static Long readOptionalLong(Map<String, String> values, String key) {
		String value = emptyToNull(values.get(key));
		if (value == null) {
			return null;
		}
		return Long.parseLong(value);
	}

	private static UUID readOptionalUuid(Map<String, String> values, String key) {
		String value = emptyToNull(values.get(key));
		if (value == null) {
			return null;
		}
		return UUID.fromString(value);
	}

	private static String readOptionalEntityId(Map<String, String> values, String key) {
		String value = emptyToNull(values.get(key));
		if (value == null) {
			return null;
		}
		return requireEntityId(value, key);
	}

	private static String emptyToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}

	private static void putString(Map<String, String> values, String key, String value) {
		if (value != null) {
			values.put(key, value);
		}
	}

	private static void putEnum(Map<String, String> values, String key, Enum<?> value) {
		if (value != null) {
			values.put(key, value.name());
		}
	}

	private static void putInt(Map<String, String> values, String key, Integer value) {
		if (value != null) {
			values.put(key, Integer.toString(value));
		}
	}

	private static void putLong(Map<String, String> values, String key, Long value) {
		if (value != null) {
			values.put(key, Long.toString(value));
		}
	}

	private static void putUuid(Map<String, String> values, String key, UUID value) {
		if (value != null) {
			values.put(key, value.toString());
		}
	}

	private static OptionalLong optionalLong(Long value) {
		if (value == null) {
			return OptionalLong.empty();
		}
		return OptionalLong.of(value);
	}

	private static OptionalInt optionalInt(Integer value) {
		if (value == null) {
			return OptionalInt.empty();
		}
		return OptionalInt.of(value);
	}

	private static void validateNonNegative(Long value, String name) {
		if (value != null && value < 0L) {
			throw new IllegalArgumentException(name + " must be non-negative");
		}
	}

	private static void validateNonNegative(Integer value, String name) {
		if (value != null && value < 0) {
			throw new IllegalArgumentException(name + " must be non-negative");
		}
	}

	private static void validatePositive(Integer value, String name) {
		if (value != null && value <= 0) {
			throw new IllegalArgumentException(name + " must be positive");
		}
	}

	public static final class Builder {
		private final OriginDisposition originDisposition;
		private final String originalEntityType;
		private InfectionStage infectionStage = InfectionStage.NONE;
		private TransformationState transformationState = TransformationState.NONE;
		private Long lastConversionGameTime;
		private UUID convertedByEntityUuid;
		private Integer conversionGeneration;
		private Long immuneUntilGameTime;
		private Long transformationStartedGameTime;
		private Integer transformationDurationTicks;
		private String pendingReplacementEntityType;
		private InfectionStage pendingInfectionStage;
		private String debugSource;

		private Builder(OriginDisposition originDisposition, String originalEntityType) {
			this.originDisposition = originDisposition;
			this.originalEntityType = originalEntityType;
		}

		public Builder infectionStage(InfectionStage infectionStage) {
			this.infectionStage = infectionStage;
			return this;
		}

		public Builder transformationState(TransformationState transformationState) {
			this.transformationState = transformationState;
			return this;
		}

		public Builder lastConversionGameTime(Long lastConversionGameTime) {
			this.lastConversionGameTime = lastConversionGameTime;
			return this;
		}

		public Builder convertedByEntityUuid(UUID convertedByEntityUuid) {
			this.convertedByEntityUuid = convertedByEntityUuid;
			return this;
		}

		public Builder conversionGeneration(Integer conversionGeneration) {
			this.conversionGeneration = conversionGeneration;
			return this;
		}

		public Builder immuneUntilGameTime(Long immuneUntilGameTime) {
			this.immuneUntilGameTime = immuneUntilGameTime;
			return this;
		}

		public Builder transformationStartedGameTime(Long transformationStartedGameTime) {
			this.transformationStartedGameTime = transformationStartedGameTime;
			return this;
		}

		public Builder transformationDurationTicks(Integer transformationDurationTicks) {
			this.transformationDurationTicks = transformationDurationTicks;
			return this;
		}

		public Builder pendingReplacementEntityType(String pendingReplacementEntityType) {
			this.pendingReplacementEntityType = pendingReplacementEntityType;
			return this;
		}

		public Builder pendingInfectionStage(InfectionStage pendingInfectionStage) {
			this.pendingInfectionStage = pendingInfectionStage;
			return this;
		}

		public Builder debugSource(String debugSource) {
			this.debugSource = debugSource;
			return this;
		}

		public Builder beginTransformation(long startedGameTime, int durationTicks, String replacementEntityType, InfectionStage pendingInfectionStage) {
			this.transformationState = TransformationState.TRANSFORMING;
			this.transformationStartedGameTime = startedGameTime;
			this.transformationDurationTicks = durationTicks;
			this.pendingReplacementEntityType = replacementEntityType;
			this.pendingInfectionStage = pendingInfectionStage;
			return this;
		}

		public Builder clearTransformation() {
			this.transformationState = TransformationState.NONE;
			this.transformationStartedGameTime = null;
			this.transformationDurationTicks = null;
			this.pendingReplacementEntityType = null;
			this.pendingInfectionStage = null;
			return this;
		}

		public BreachEntityData build() {
			return new BreachEntityData(this);
		}
	}
}
