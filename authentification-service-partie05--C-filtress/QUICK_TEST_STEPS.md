# Quick Test Steps for JWT Authentication

## ✅ Application Status
- Server is running on: **http://localhost:8089**
- Database initialized with test users

## 👥 Test Users Available

| Username | Password | Roles |
|----------|----------|-------|
| user1    | 123      | USER  |
| user2    | 456      | USER, ADMIN |

## 🧪 Test 1: Login and Get JWT Token

### Using ARC (Advanced REST Client)

1. **Method**: POST
2. **URL**: `http://localhost:8089/login`
3. **Headers Tab**: Click on "HEADERS" tab
   - Add header: `Content-Type` = `application/x-www-form-urlencoded`
4. **Body Tab**: Click on "BODY" tab
   - Select format: `application/x-www-form-urlencoded`
   - Add parameters:
     - Name: `username`, Value: `user1`
     - Name: `password`, Value: `123`
5. **Click Send** (the blue arrow button)

### Expected Response:
- **Status**: 200 OK
- **Response Headers**: Look for `Authorization` header containing the JWT token
- **Body**: Empty (no body content)

### If you get 403 Forbidden:
This means the authentication failed. Check:
- Username and password are correct
- Content-Type header is set correctly
- Body format is `application/x-www-form-urlencoded`

## 🧪 Test 2: Access Protected Resource with JWT

1. **Copy the JWT token** from the Authorization header in Test 1 response
2. **Method**: GET
3. **URL**: `http://localhost:8089/users`
4. **Headers Tab**:
   - Add header: `Authorization` = `Bearer <YOUR_JWT_TOKEN>`
   - ⚠️ **Important**: Add a space after "Bearer"
   - Example: `Bearer eyJ0eXAiOiJKV1QiLCJhbGc...`
5. **Click Send**

### Expected Response:
- **Status**: 200 OK
- **Body**: JSON array with list of users

```json
[
  {
    "id": 1,
    "username": "user1",
    "password": "$2a$10$...",
    "roles": [...]
  },
  {
    "id": 2,
    "username": "user2",
    "password": "$2a$10$...",
    "roles": [...]
  }
]
```

## 🧪 Test 3: Verify JWT Content

1. Go to https://jwt.io
2. Paste your JWT token in the "Encoded" section
3. Verify the decoded content:

**Header:**
```json
{
  "typ": "JWT",
  "alg": "HS256"
}
```

**Payload:**
```json
{
  "sub": "user1",
  "roles": ["USER"],
  "iss": "http://localhost:8089/login",
  "exp": 1234567890
}
```

## 🧪 Test 4: Test Without JWT (Should Fail)

1. **Method**: GET
2. **URL**: `http://localhost:8089/users`
3. **Headers**: Don't add Authorization header
4. **Click Send**

### Expected Response:
- **Status**: 403 Forbidden

## 🧪 Test 5: Test with Expired JWT

1. Wait 5 minutes after getting a JWT token
2. Try to access `/users` with the old token
3. Should get 403 Forbidden

## 📝 Using curl (Alternative)

### Login:
```bash
curl -X POST http://localhost:8089/login \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=user1&password=123" \
  -i
```

### Access Resource:
```bash
curl -X GET http://localhost:8089/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## 🔧 Troubleshooting

### Problem: 403 Forbidden on /login
**Possible causes:**
1. Wrong username or password
2. Missing Content-Type header
3. Wrong body format (should be x-www-form-urlencoded)

**Solution:**
- Verify credentials: user1/123 or user2/456
- Add Content-Type header
- Use x-www-form-urlencoded format in body

### Problem: 403 Forbidden on /users
**Possible causes:**
1. Missing Authorization header
2. Missing "Bearer " prefix
3. JWT token expired (5 minutes)
4. Invalid JWT token

**Solution:**
- Add Authorization header
- Format: `Bearer <token>` (with space after Bearer)
- Get a new token if expired

### Problem: No Authorization header in response
**Possible causes:**
1. Authentication failed
2. Filter not registered properly

**Solution:**
- Check server logs for errors
- Verify filter is added in SecurityConfig

## 📊 Server Logs

When testing, watch the server console for these messages:
- `attemptAuthentication` - When login is attempted
- `successfulAuthentication` - When login succeeds and JWT is created

## 🎯 Next Steps After Successful Tests

1. Test with user2 (has ADMIN role)
2. Modify JWT expiration time to 1 minute for testing
3. Test expired token behavior
4. Add role-based authorization to endpoints
5. Externalize JWT secret key to application.properties
