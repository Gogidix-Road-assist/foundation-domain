package com.gogidix.rapidassist.dashboard.configuration.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for dashboard configuration
 */
public class DashboardConfiguration {

    private final String id;
    private final String tenantId;
    private final String dashboardId;
    private final String name;
    private final String description;
    private final DashboardMetadata metadata;
    private final DashboardLayout layout;
    private final List<DashboardWidget> widgets;
    private final DashboardTheme theme;
    private final DashboardPermissions permissions;
    private final boolean active;
    private final String environment;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    public record DashboardMetadata(
        String category,
        String icon,
        Set<String> tags,
        Map<String, String> customMetadata
    ) {
        public static DashboardMetadata defaults() {
            return new DashboardMetadata("general", "dashboard", Set.of(), Map.of());
        }
    }

    public record DashboardLayout(
        String type,  // GRID, FREE_FORM, TABS
        int columns,
        boolean responsive,
        Map<String, Object> layoutConfig
    ) {
        public static DashboardLayout defaults() {
            Map<String, Object> config = new HashMap<>();
            config.put("spacing", 16);
            config.put("padding", 16);
            return new DashboardLayout("GRID", 3, true, config);
        }
    }

    public record DashboardWidget(
        String widgetId,
        String type,
        String title,
        int position,
        WidgetSize size,
        WidgetConfig config,
        WidgetDataSource dataSource,
        boolean refreshEnabled,
        int refreshIntervalSeconds,
        Map<String, String> customSettings
    ) {
        public record WidgetSize(int width, int height) {
            public static WidgetSize defaults() {
                return new WidgetSize(1, 1);
            }
        }

        public record WidgetConfig(
            Map<String, Object> properties,
            Map<String, String> styles,
            Map<String, Object> behavior
        ) {
            public static WidgetConfig defaults() {
                return new WidgetConfig(Map.of(), Map.of(), Map.of());
            }
        }

        public record WidgetDataSource(
            String service,
            String endpoint,
            String method,
            Map<String, String> headers,
            Map<String, Object> params,
            String dataTransformer
        ) {
            public static WidgetDataSource defaults() {
                return new WidgetDataSource("", "", "GET", Map.of(), Map.of(), "");
            }
        }
    }

    public record DashboardTheme(
        String primaryColor,
        String secondaryColor,
        String backgroundColor,
        String cardColor,
        String textColor,
        String borderColor,
        String font,
        boolean darkMode
    ) {
        public static DashboardTheme defaults() {
            return new DashboardTheme(
                "#007bff",
                "#6c757d",
                "#f8f9fa",
                "#ffffff",
                "#212529",
                "#dee2e6",
                "Segoe UI",
                false
            );
        }
    }

    public record DashboardPermissions(
        Set<String> viewRoles,
        Set<String> editRoles,
        Set<String> deleteRoles,
        Set<String> shareRoles,
        boolean publicView
    ) {
        public static DashboardPermissions defaults() {
            return new DashboardPermissions(
                Set.of("USER"),
                Set.of("ADMIN"),
                Set.of("ADMIN"),
                Set.of("ADMIN", "USER"),
                false
            );
        }
    }

    private DashboardConfiguration(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.dashboardId = builder.dashboardId;
        this.name = builder.name;
        this.description = builder.description;
        this.metadata = builder.metadata;
        this.layout = builder.layout;
        this.widgets = builder.widgets;
        this.theme = builder.theme;
        this.permissions = builder.permissions;
        this.active = builder.active;
        this.environment = builder.environment;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasPermission(String action, String role) {
        return switch (action.toUpperCase()) {
            case "VIEW" -> permissions.viewRoles().contains(role) || permissions.publicView();
            case "EDIT" -> permissions.editRoles().contains(role);
            case "DELETE" -> permissions.deleteRoles().contains(role);
            case "SHARE" -> permissions.shareRoles().contains(role);
            default -> false;
        };
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String dashboardId() { return dashboardId; }
    public String name() { return name; }
    public String description() { return description; }
    public DashboardMetadata metadata() { return metadata; }
    public DashboardLayout layout() { return layout; }
    public List<DashboardWidget> widgets() { return widgets; }
    public DashboardTheme theme() { return theme; }
    public DashboardPermissions permissions() { return permissions; }
    public boolean active() { return active; }
    public String environment() { return environment; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String dashboardId;
        private String name;
        private String description;
        private DashboardMetadata metadata = DashboardMetadata.defaults();
        private DashboardLayout layout = DashboardLayout.defaults();
        private List<DashboardWidget> widgets = List.of();
        private DashboardTheme theme = DashboardTheme.defaults();
        private DashboardPermissions permissions = DashboardPermissions.defaults();
        private boolean active = true;
        private String environment = "production";
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder dashboardId(String dashboardId) { this.dashboardId = dashboardId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder metadata(DashboardMetadata metadata) { this.metadata = metadata; return this; }
        public Builder layout(DashboardLayout layout) { this.layout = layout; return this; }
        public Builder widgets(List<DashboardWidget> widgets) { this.widgets = widgets; return this; }
        public Builder theme(DashboardTheme theme) { this.theme = theme; return this; }
        public Builder permissions(DashboardPermissions permissions) { this.permissions = permissions; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public DashboardConfiguration build() {
            return new DashboardConfiguration(this);
        }
    }
}
