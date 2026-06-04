import http from "k6/http";
import { check, fail } from "k6";

export const options = {
  stages: [{ duration: "30s", target: 200 }],
};

const HOST = __ENV.HOST || "http://localhost:3001";
const TARGET_PATH = __ENV.TARGET_PATH || "/api/devices";

const TEST_EMAIL =
  __ENV.TEST_EMAIL ||
  `loadtest+${Date.now()}_${Math.floor(Math.random() * 10000)}@example.com`;
const TEST_PASSWORD = __ENV.TEST_PASSWORD || "loadtestpass";

export function setup() {
  const headers = { headers: { "Content-Type": "application/json" } };

  let usedEmail = TEST_EMAIL;
  let reg = http.post(
    `${HOST}/api/auth/register`,
    JSON.stringify({
      email: usedEmail,
      password: TEST_PASSWORD,
      name: "Load Test User",
    }),
    headers,
  );

  if (reg.status === 201) {
    // created successfully
  } else if (reg.status === 409) {
    // user already exists — try to login; if login fails, create an alternative user
    let loginRes = http.post(
      `${HOST}/api/auth/login`,
      JSON.stringify({ email: usedEmail, password: TEST_PASSWORD }),
      headers,
    );

    if (loginRes.status !== 200) {
      // try several alternative unique emails
      let created = false;
      for (let i = 0; i < 5; i++) {
        usedEmail = `loadtest+alt${Date.now()}_${Math.floor(Math.random() * 10000)}@example.com`;
        reg = http.post(
          `${HOST}/api/auth/register`,
          JSON.stringify({
            email: usedEmail,
            password: TEST_PASSWORD,
            name: "Load Test User",
          }),
          headers,
        );
        if (reg.status === 201) {
          created = true;
          break;
        }
      }
      if (!created) {
        fail("Failed to create a test user after several attempts");
      }
    }
  } else if (reg.status !== 201) {
    console.warn("register returned unexpected status", reg.status, reg.body);
  }

  // Login to obtain token for the user that exists/was created
  const loginResFinal = http.post(
    `${HOST}/api/auth/login`,
    JSON.stringify({ email: usedEmail, password: TEST_PASSWORD }),
    headers,
  );
  if (loginResFinal.status !== 200) {
    console.error("login failed", loginResFinal.status, loginResFinal.body);
    fail(`Login failed with status ${loginResFinal.status}`);
  }

  const token = loginResFinal.json("token");
  if (!token) {
    fail("No token returned from login");
  }

  return { token };
}

export default function (data) {
  const token = data.token;
  if (!token) {
    fail("Missing auth token in VU context");
  }

  const params = { headers: { Authorization: `Bearer ${token}` } };

  const res = http.get(`${HOST}${TARGET_PATH}`, params);
  check(res, { "status is 200": (r) => r.status === 200 });
}
