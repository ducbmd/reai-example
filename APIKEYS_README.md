# API Authentication Guide

A comprehensive guide to the API key authentication system for secure programmatic access to tenant-scoped data.

## Table of Contents

- [Overview](#overview)
- [Quick Start](#quick-start)
- [API Key Management](#api-key-management)
- [Authentication](#authentication)
- [Available Endpoints](#available-endpoints)
- [Security](#security)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)
- [Implementation Reference](#implementation-reference)

## Overview

The API authentication system provides secure, tenant-scoped access to application data through API keys. Each API key is associated with a specific user and tenant, ensuring proper data isolation and access control.

### Key Features

- **Tenant-scoped access**: API keys automatically inherit user's tenant context
- **Secure storage**: Secrets are hashed using BCrypt encryption
- **Web management**: Create and manage keys through the user interface
- **One-time display**: API keys are shown only once for security

## Quick Start

### 1. Create an API Key

**Via Web Interface:**
1. Log into the application
2. Navigate to **User Settings**
3. Click **Create API Key**
4. Enter a descriptive name (or leave empty for "Secret key")
5. **Copy the key immediately** - it won't be shown again

**Via API:**
```bash
curl -X POST /user-settings/api-keys \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=My Integration"
```

### 2. Use Your API Key

Include the API key in the `X-API-KEY` header:

```bash
curl -H "X-API-KEY: ak_abc123...xyz789" \
     https://your-domain.com/api/test/hello
```

## API Key Management

### API Key Format

```
ak_{keyId}.{secret}
```

**Components:**
- `ak_` - Fixed prefix identifying API keys
- `{keyId}` - 32-character hexadecimal identifier (defined as `ApiKey.KEY_ID_LENGTH`)
- `{secret}` - 32-character hexadecimal secret (defined as `ApiKey.SECRET_LENGTH`)

**Example:**
```
ak_550e8400e29b41d4a716446655440000.a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
```

### Creating API Keys

#### Web Interface

The user-friendly interface provides:
- **Modal form** for creating new keys
- **Descriptive naming** with auto-default to "Secret key"
- **One-time key display** with copy-to-clipboard functionality
- **Key list management** with usage tracking

#### API Endpoint

**Request:**
```http
POST /user-settings/api-keys
Content-Type: application/x-www-form-urlencoded

name=My Integration Key
```

**Response:**
```json
{
  "id": 1,
  "name": "My Integration Key",
  "key": "ak_550e8400e29b41d4a716446655440000.a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
  "markedKey": "ak_550...o5p6",
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### Listing API Keys

**Request:**
```http
GET /user-settings/api-keys
```

**Response:** HTML fragment showing:
- Key name and creation date
- Masked key preview (first 3 + last 4 characters)
- Last used timestamp
- Creator information
- Delete action

### Deleting API Keys

**Request:**
```http
DELETE /user-settings/api-keys/{keyId}
```

**Response:**
```json
{
  "status": "deleted"
}
```

## Authentication

### Header-Based Authentication

Include your API key in every request:

```http
X-API-KEY: ak_550e8400e29b41d4a716446655440000.a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
```

### Authentication Flow

1. **Request Processing**: `ApiKeyAuthenticationFilter` extracts the `X-API-KEY` header
2. **Key Validation**: Key format is validated and parsed
3. **Database Lookup**: Key ID is used to find the stored key record
4. **Secret Verification**: Secret is verified using BCrypt comparison
5. **Context Loading**: User and tenant information is loaded
6. **Security Context**: Spring Security context is populated
7. **Request Continuation**: Request proceeds with authenticated context

### Authenticated Context

Once authenticated, your API requests have access to:

```json
{
  "user": {
    "id": 1,
    "email": "user@example.com",
    "roles": ["USER"],
    "permissions": ["all:read", "all:write"]
  },
  "tenant": {
    "id": 1,
    "companyName": "Example Company",
    "countryCode": "USD"
  }
}
```

## Available Endpoints

### Test Endpoints

#### Hello World
**GET /api/test/hello**

Returns basic information about the authenticated context.

```bash
curl -H "X-API-KEY: ak_550e...o5p6" \
     https://your-domain.com/api/test/hello
```

**Response:**
```json
{
  "message": "Hello, API!",
  "timestamp": "2024-01-15T10:30:00Z",
  "user": {
    "id": 1,
    "email": "user@example.com"
  },
  "tenant": {
    "id": 1,
    "companyName": "Example Company",
    "countryCode": "USD"
  }
}
```

#### User Information
**GET /api/test/user-info**

Returns detailed user and tenant information.

```bash
curl -H "X-API-KEY: ak_550e...o5p6" \
     https://your-domain.com/api/test/user-info
```

**Response:**
```json
{
  "user": {
    "id": 1,
    "email": "user@example.com",
    "roles": ["USER"],
    "permissions": ["all:read", "all:write"]
  },
  "tenant": {
    "id": 1,
    "companyName": "Example Company"
  }
}
```

### Error Responses

#### Missing API Key
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized",
  "message": "Authentication failed"
}
```

#### Invalid API Key
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "error": "Unauthorized", 
  "message": "Authentication failed"
}
```

## Implementation Reference

### Database Schema

```sql
CREATE TABLE api_keys (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id bigint NOT NULL REFERENCES tenants(id),
    user_id bigint NOT NULL REFERENCES users(id),
    name varchar(255) NOT NULL,
    key_id varchar(32) NOT NULL UNIQUE,
    hashed_secret varchar(255) NOT NULL,
    marked_api_key varchar(255) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at timestamptz
);

CREATE INDEX idx_api_keys_key_id ON api_keys(key_id);
CREATE INDEX idx_api_keys_user_tenant ON api_keys(user_id, tenant_id);
```

### Key Components

#### ApiKeyAuthenticationFilter
- Processes `X-API-KEY` header
- Validates key format and extracts components
- Performs database lookup and secret verification
- Sets Spring Security authentication context

#### ApiKeyService
- Generates cryptographically secure API keys
- Handles BCrypt hashing of secrets
- Manages key creation, lookup, and deletion
- Updates usage timestamps

#### Security Configuration
- Configures API key authentication for `/api/**` endpoints
- Integrates with existing Spring Security setup
- Handles authentication failures and error responses


