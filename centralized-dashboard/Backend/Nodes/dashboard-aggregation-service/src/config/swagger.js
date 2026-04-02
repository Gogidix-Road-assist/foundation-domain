/**
 * OpenAPI/Swagger Configuration
 * API Documentation for Dashboard Aggregation Service
 */

const swaggerConfig = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Dashboard Aggregation Service API',
      version: '1.0.0',
      description: `
        Dashboard Aggregation Service for Gogidix RapidAssist Platform.

        ## Features
        - **Dashboard Management**: Create, read, update, and delete dashboard widgets
        - **Metrics Aggregation**: Aggregate metrics from multiple services
        - **Analytics**: Custom analytics and reporting capabilities
        - **Service Health**: Monitor health of all platform services
        - **Alerts**: Manage and respond to system alerts
        - **Reports**: Generate and export comprehensive reports

        ## Authentication
        Most endpoints require JWT authentication. Include the token in the Authorization header:
        \`Authorization: Bearer <your-jwt-token>\`

        ## Rate Limiting
        API requests are rate limited based on user tier and endpoint type.
        See rate limit headers in response for current limits.
      `,
      contact: {
        name: 'Gogidix API Support',
        email: 'api-support@gogidix.com',
        url: 'https://gogidix.com/support'
      },
      license: {
        name: 'UNLICENSED',
        url: 'https://gogidix.com/terms'
      }
    },
    servers: [
      {
        url: 'http://localhost:3000',
        description: 'Development server'
      },
      {
        url: 'https://api-staging.gogidix.com',
        description: 'Staging server'
      },
      {
        url: 'https://api.gogidix.com',
        description: 'Production server'
      }
    ],
    tags: [
      {
        name: 'Health',
        description: 'Health check and status endpoints'
      },
      {
        name: 'Dashboard',
        description: 'Dashboard management operations'
      },
      {
        name: 'Metrics',
        description: 'Metrics aggregation and retrieval'
      },
      {
        name: 'Analytics',
        description: 'Custom analytics and reporting'
      },
      {
        name: 'Services',
        description: 'Service health monitoring'
      },
      {
        name: 'Alerts',
        description: 'Alert management'
      },
      {
        name: 'Reports',
        description: 'Report generation and export'
      },
      {
        name: 'Authentication',
        description: 'Authentication and token management'
      }
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
          description: 'JWT authentication token. Obtain from /api/v1/auth/login'
        },
        apiKeyAuth: {
          type: 'apiKey',
          in: 'header',
          name: 'X-API-Key',
          description: 'API key for service-to-service authentication'
        }
      },
      schemas: {
        Error: {
          type: 'object',
          properties: {
            error: {
              type: 'string',
              description: 'Error type/name'
            },
            message: {
              type: 'string',
              description: 'Human-readable error message'
            },
            type: {
              type: 'string',
              enum: ['validation', 'authentication', 'authorization', 'not_found', 'business', 'external_service', 'database', 'rate_limit', 'system'],
              description: 'Error category'
            },
            details: {
              type: 'object',
              description: 'Additional error details (for validation errors)'
            },
            timestamp: {
              type: 'string',
              format: 'date-time',
              description: 'ISO 8601 timestamp'
            },
            requestId: {
              type: 'string',
              description: 'Unique request identifier for tracing'
            }
          },
          required: ['error', 'message', 'type', 'timestamp']
        },
        SuccessResponse: {
          type: 'object',
          properties: {
            success: {
              type: 'boolean',
              example: true
            },
            message: {
              type: 'string'
            },
            data: {
              type: 'object'
            }
          }
        },
        Dashboard: {
          type: 'object',
          properties: {
            id: {
              type: 'string',
              description: 'Dashboard ID'
            },
            name: {
              type: 'string',
              description: 'Dashboard name'
            },
            tenantId: {
              type: 'string',
              description: 'Tenant ID'
            },
            widgets: {
              type: 'array',
              items: {
                $ref: '#/components/schemas/Widget'
              }
            }
          }
        },
        Widget: {
          type: 'object',
          properties: {
            id: {
              type: 'string',
              description: 'Widget ID'
            },
            name: {
              type: 'string',
              description: 'Widget name'
            },
            type: {
              type: 'string',
              enum: ['metric', 'chart', 'table', 'list', 'gauge', 'progress'],
              description: 'Widget type'
            },
            title: {
              type: 'string',
              description: 'Widget display title'
            },
            description: {
              type: 'string',
              description: 'Widget description'
            },
            position: {
              type: 'object',
              properties: {
                x: { type: 'number', minimum: 0 },
                y: { type: 'number', minimum: 0 },
                width: { type: 'number', minimum: 1, maximum: 12 },
                height: { type: 'number', minimum: 1, maximum: 12 }
              },
              required: ['x', 'y', 'width', 'height']
            },
            config: {
              type: 'object',
              description: 'Widget-specific configuration'
            },
            dataSource: {
              type: 'object',
              properties: {
                type: {
                  type: 'string',
                  enum: ['service', 'api', 'database', 'custom']
                },
                endpoint: {
                  type: 'string'
                },
                method: {
                  type: 'string',
                  enum: ['GET', 'POST', 'PUT', 'DELETE']
                },
                headers: {
                  type: 'object'
                },
                params: {
                  type: 'object'
                },
                refreshInterval: {
                  type: 'number',
                  minimum: 5000,
                  description: 'Refresh interval in milliseconds'
                }
              },
              required: ['type', 'endpoint']
            },
            visibility: {
              type: 'object',
              properties: {
                roles: {
                  type: 'array',
                  items: { type: 'string' }
                },
                users: {
                  type: 'array',
                  items: { type: 'string' }
                },
                tenants: {
                  type: 'array',
                  items: { type: 'string' }
                }
              }
            },
            tenantId: {
              type: 'string',
              description: 'Tenant ID'
            },
            createdAt: {
              type: 'string',
              format: 'date-time'
            },
            updatedAt: {
              type: 'string',
              format: 'date-time'
            }
          },
          required: ['name', 'type', 'title', 'position', 'config', 'dataSource', 'tenantId']
        },
        CreateWidgetRequest: {
          type: 'object',
          required: ['name', 'type', 'title', 'position', 'config', 'dataSource', 'tenantId'],
          properties: {
            name: {
              type: 'string',
              minLength: 1,
              maxLength: 100
            },
            type: {
              type: 'string',
              enum: ['metric', 'chart', 'table', 'list', 'gauge', 'progress']
            },
            title: {
              type: 'string',
              minLength: 1,
              maxLength: 200
            },
            description: {
              type: 'string',
              maxLength: 500
            },
            position: {
              $ref: '#/components/schemas/Widget.properties.position'
            },
            config: {
              type: 'object'
            },
            dataSource: {
              $ref: '#/components/schemas/Widget.properties.dataSource'
            },
            visibility: {
              $ref: '#/components/schemas/Widget.properties.visibility'
            },
            tenantId: {
              type: 'string'
            }
          }
        },
        MetricsSummary: {
          type: 'object',
          properties: {
            totalRequests: {
              type: 'integer'
            },
            successRate: {
              type: 'number',
              format: 'float'
            },
            avgResponseTime: {
              type: 'number',
              format: 'float'
            },
            activeServices: {
              type: 'integer'
            },
            timeRange: {
              type: 'string'
            }
          }
        },
        Alert: {
          type: 'object',
          properties: {
            id: {
              type: 'string'
            },
            title: {
              type: 'string'
            },
            description: {
              type: 'string'
            },
            severity: {
              type: 'string',
              enum: ['critical', 'warning', 'info']
            },
            status: {
              type: 'string',
              enum: ['active', 'acknowledged', 'resolved']
            },
            source: {
              type: 'string'
            },
            service: {
              type: 'string'
            },
            metadata: {
              type: 'object'
            },
            tags: {
              type: 'array',
              items: {
                type: 'string'
              }
            },
            tenantId: {
              type: 'string'
            },
            createdAt: {
              type: 'string',
              format: 'date-time'
            },
            acknowledgedAt: {
              type: 'string',
              format: 'date-time'
            },
            resolvedAt: {
              type: 'string',
              format: 'date-time'
            }
          }
        },
        Report: {
          type: 'object',
          properties: {
            id: {
              type: 'string'
            },
            name: {
              type: 'string'
            },
            description: {
              type: 'string'
            },
            type: {
              type: 'string',
              enum: ['analytics', 'financial', 'usage', 'performance', 'health', 'custom']
            },
            format: {
              type: 'string',
              enum: ['pdf', 'csv', 'excel', 'json']
            },
            status: {
              type: 'string',
              enum: ['pending', 'generating', 'completed', 'failed']
            },
            parameters: {
              type: 'object'
            },
            fileUrl: {
              type: 'string',
              format: 'uri'
            },
            expiresAt: {
              type: 'string',
              format: 'date-time'
            },
            tenantId: {
              type: 'string'
            },
            createdAt: {
              type: 'string',
              format: 'date-time'
            }
          }
        }
      },
      parameters: {
        TenantId: {
          name: 'tenantId',
          in: 'query',
          description: 'Tenant ID for multi-tenant data isolation',
          required: false,
          schema: {
            type: 'string'
          }
        },
        TimeRange: {
          name: 'timeRange',
          in: 'query',
          description: 'Time range for data aggregation',
          required: false,
          schema: {
            type: 'string',
            enum: ['1h', '6h', '24h', '7d', '30d', '90d'],
            default: '24h'
          }
        },
        Page: {
          name: 'page',
          in: 'query',
          description: 'Page number for pagination',
          required: false,
          schema: {
            type: 'integer',
            minimum: 1,
            default: 1
          }
        },
        Limit: {
          name: 'limit',
          in: 'query',
          description: 'Number of items per page',
          required: false,
          schema: {
            type: 'integer',
            minimum: 1,
            maximum: 100,
            default: 20
          }
        }
      },
      responses: {
        Unauthorized: {
          description: 'Authentication failed',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'AuthenticationError',
                message: 'Authentication token is required',
                type: 'authentication',
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        },
        Forbidden: {
          description: 'Access denied',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'AuthorizationError',
                message: 'You do not have permission to access this resource',
                type: 'authorization',
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        },
        NotFound: {
          description: 'Resource not found',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'NotFoundError',
                message: 'Resource not found',
                type: 'not_found',
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        },
        ValidationError: {
          description: 'Validation failed',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'ValidationError',
                message: 'Request validation failed',
                type: 'validation',
                details: [
                  {
                    field: 'name',
                    message: 'Name is required'
                  }
                ],
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        },
        RateLimitExceeded: {
          description: 'Rate limit exceeded',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'RateLimitError',
                message: 'Too many requests, please try again later',
                type: 'rate_limit',
                retryAfter: 60,
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        },
        InternalError: {
          description: 'Internal server error',
          content: {
            'application/json': {
              schema: {
                $ref: '#/components/schemas/Error'
              },
              example: {
                error: 'Internal server error',
                message: 'An unexpected error occurred',
                type: 'system',
                timestamp: '2026-01-12T10:30:00Z'
              }
            }
          }
        }
      }
    }
  },
  apis: [
    './src/adapters/in/http/routes/*.js',
    './src/adapters/in/http/controllers/*.js'
  ]
}

module.exports = swaggerConfig
