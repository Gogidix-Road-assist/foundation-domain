package com.gogidix.ai.dashboard.configuration;

import com.gogidix.ai.dashboard.model.ServiceHealth;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for all 27 AI Services to be monitored
 */
@Configuration
public class ServicesConfiguration {

    /**
     * All 27 AI Services with their connection details
     */
    public static final List<ServiceDefinition> ALL_SERVICES = List.of(

        // Core AI Services (13) - Ports 8081-8093
        new ServiceDefinition("ai-leads-generator-service", "AI Leads Generator", "core",
            "Lead generation using AI algorithms", 8081),
        new ServiceDefinition("ai-content-generator-service", "AI Content Generator", "core",
            "Automated content creation and generation", 8082),
        new ServiceDefinition("ai-document-analyzer-service", "AI Document Analyzer", "core",
            "Document analysis and intelligent processing", 8083),
        new ServiceDefinition("ai-recommendation-engine-service", "AI Recommendation Engine", "core",
            "Personalized recommendation algorithms", 8084),
        new ServiceDefinition("ai-chatbot-service", "AI Chatbot", "core",
            "Conversational AI and intelligent chatbot", 8085),
        new ServiceDefinition("ai-image-recognition-service", "AI Image Recognition", "core",
            "Computer vision and image processing", 8086),
        new ServiceDefinition("ai-speech-recognition-service", "AI Speech Recognition", "core",
            "Speech-to-text and voice processing", 8087),
        new ServiceDefinition("ai-data-prediction-service", "AI Data Prediction", "core",
            "Predictive analytics and forecasting", 8088),
        new ServiceDefinition("ai-sentiment-analysis-service", "AI Sentiment Analysis", "core",
            "Sentiment analysis from text data", 8089),
        new ServiceDefinition("ai-translation-service", "AI Translation", "core",
            "Multi-language translation services", 8090),
        new ServiceDefinition("ai-anomaly-detection-service", "AI Anomaly Detection", "core",
            "Anomaly detection in data streams", 8091),
        new ServiceDefinition("ai-text-summarization-service", "AI Text Summarization", "core",
            "Intelligent text summarization", 8092),
        new ServiceDefinition("ai-voice-assistant-service", "AI Voice Assistant", "core",
            "Voice-activated AI assistant", 8093),

        // Business Intelligence Services (7) - Ports 8094-8100
        new ServiceDefinition("ai-training-ml-service", "AI Training ML", "business-intelligence",
            "Machine learning model training", 8094),
        new ServiceDefinition("analytics-service", "Analytics Service", "business-intelligence",
            "General analytics and reporting", 8095),
        new ServiceDefinition("customer-behaviour-analytics-service", "Customer Behaviour Analytics", "business-intelligence",
            "Customer behavior analysis and insights", 8096),
        new ServiceDefinition("data-analytics-service", "Data Analytics", "business-intelligence",
            "Data processing and advanced analytics", 8097),
        new ServiceDefinition("predictive-maintenance-service", "Predictive Maintenance", "business-intelligence",
            "Predictive maintenance algorithms", 8098),
        new ServiceDefinition("sentiment-analysis-service", "Sentiment Analysis (Legacy)", "business-intelligence",
            "Additional sentiment analysis capabilities", 8099),
        new ServiceDefinition("vendors-product-listing-ai-service", "Vendors Product Listing AI", "business-intelligence",
            "AI-powered vendor product management", 8100),

        // Business Operations Services (7) - Ports 8101-8107
        new ServiceDefinition("customer-support-chatbot-service", "Customer Support Chatbot", "business-operations",
            "Automated customer service", 8101),
        new ServiceDefinition("document-intelligence-service", "Document Intelligence", "business-operations",
            "Advanced document processing", 8102),
        new ServiceDefinition("dynamic-pricing-service", "Dynamic Pricing", "business-operations",
            "AI-powered pricing optimization", 8103),
        new ServiceDefinition("fraud-detection-service", "Fraud Detection", "business-operations",
            "Real-time fraud detection", 8104),
        new ServiceDefinition("intelligent-dispatch-service", "Intelligent Dispatch", "business-operations",
            "Smart dispatching algorithms", 8105),
        new ServiceDefinition("recommendation-engine-service", "Recommendation Engine", "business-operations",
            "Additional recommendation capabilities", 8106),
        new ServiceDefinition("route-optimization-service", "Route Optimization", "business-operations",
            "Route planning and optimization", 8107)
    );

    public record ServiceDefinition(
        String id,
        String name,
        String category,
        String description,
        Integer port
    ) {
        public String getHealthUrl() {
            return "http://localhost:" + port + "/actuator/health";
        }

        public String getMetricsUrl() {
            return "http://localhost:" + port + "/actuator/metrics";
        }

        public String getServiceUrl() {
            return "http://localhost:" + port;
        }
    }
}
