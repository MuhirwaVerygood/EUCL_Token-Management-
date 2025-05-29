# Testing Your Dockerized Application

This guide provides information on how to test your application running in Docker.

## Base URL

When running the application in Docker, you can access it through the Nginx load balancer at:

```
http://localhost
```

## Available Endpoints

### Public Endpoints (No Authentication Required)

These endpoints are accessible without authentication:

- **Register a new user**:
  - URL: `http://localhost/users/register`
  - Method: `POST`
  - Body:
    ```json
    {
      "name": "Test User",
      "email": "test@example.com",
      "phone": "1234567890",
      "nationalId": "1234567890123456",
      "password": "password123",
      "role": "ROLE_CUSTOMER"
    }
    ```

- **Login**:
  - URL: `http://localhost/users/login`
  - Method: `POST`
  - Body:
    ```json
    {
      "email": "test@example.com",
      "password": "password123"
    }
    ```
  - Response: Contains JWT tokens (access and refresh) that you'll need for authenticated endpoints

- **Health Check**:
  - URL: `http://localhost/actuator/health`
  - Method: `GET`

### Authenticated Endpoints (Require JWT Token)

For these endpoints, you need to include the JWT token in the Authorization header:
`Authorization: Bearer your_jwt_token_here`

#### User Endpoints (Customer & Admin)

- **Get User by ID** (Users can access their own data, Admins can access any user's data):
  - URL: `http://localhost/users/{id}`
  - Method: `GET`

- **Update User** (Users can update their own data, Admins can update any user's data):
  - URL: `http://localhost/users/{id}`
  - Method: `PUT`
  - Body: Same format as registration

#### Token Endpoints (Customer & Admin)

- **Purchase Token** (Customer only):
  - URL: `http://localhost/tokens/purchase`
  - Method: `POST`
  - Body:
    ```json
    {
      "meterNumber": "12345678",
      "amount": 1000
    }
    ```

- **Validate Token**:
  - URL: `http://localhost/tokens/validate/{token}`
  - Method: `GET`

- **Get Tokens by Meter Number**:
  - URL: `http://localhost/tokens/meter/{meterNumber}`
  - Method: `GET`

#### Admin-Only Endpoints

- **Get All Users**:
  - URL: `http://localhost/users`
  - Method: `GET`

- **Delete User**:
  - URL: `http://localhost/users/{id}`
  - Method: `DELETE`

- **Get All Tokens**:
  - URL: `http://localhost/tokens`
  - Method: `GET`

- **Get Token by ID**:
  - URL: `http://localhost/tokens/{id}`
  - Method: `GET`

- **Update Token Status**:
  - URL: `http://localhost/tokens/{id}/status?status=USED`
  - Method: `PUT`

- **Delete Token**:
  - URL: `http://localhost/tokens/{id}`
  - Method: `DELETE`

- **Register Meter**:
  - URL: `http://localhost/meters/register`
  - Method: `POST`
  - Body:
    ```json
    {
      "meterNumber": "12345678",
      "userId": 1
    }
    ```

- **Get All Meters**:
  - URL: `http://localhost/meters`
  - Method: `GET`

- **Get Meter by ID**:
  - URL: `http://localhost/meters/{id}`
  - Method: `GET`

- **Get Meter by Number**:
  - URL: `http://localhost/meters/number/{meterNumber}`
  - Method: `GET`

- **Get Meters by User ID**:
  - URL: `http://localhost/meters/user/{userId}`
  - Method: `GET`

- **Update Meter**:
  - URL: `http://localhost/meters/{id}`
  - Method: `PUT`
  - Body: Same format as meter registration

- **Delete Meter**:
  - URL: `http://localhost/meters/{id}`
  - Method: `DELETE`

## Testing Flow

1. **Register a User**: Start by creating a user account
2. **Login**: Authenticate to get the JWT token
3. **Use the JWT Token**: Include the token in the Authorization header for subsequent requests
4. **Test Functionality**: Test the various endpoints based on the user's role

## Notes

- The Nginx configuration automatically rewrites URLs, so you can use `/users/...` instead of `/api/users/...`, etc.
- Make sure your Docker containers are running before testing
- If you encounter a 403 error, check if:
  - You're using the correct endpoint
  - You've included the JWT token in the Authorization header
  - The user has the required role for the endpoint

## Troubleshooting

### Health Check Endpoint

If you encounter an error like "No static resource actuator/health" when trying to access the health endpoint:

1. Make sure you're using the correct URL: `http://localhost/actuator/health`
2. The Nginx configuration has been updated to properly route requests to the actuator endpoints
3. The health endpoint is configured to be publicly accessible in the security configuration
4. The actuator endpoints are properly exposed in the application.properties file
