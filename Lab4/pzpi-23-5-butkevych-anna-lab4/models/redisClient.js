const redis = require("redis");

const redisUrl = process.env.REDIS_URL || "redis://localhost:6379";

const redisClient = redis.createClient({ url: redisUrl });

redisClient
  .connect()
  .then(() => console.log("DEBUG: Redis connected successfully."))
  .catch((err) => console.error("Redis connection error:", err));

async function get(key) {
  try {
    const val = await redisClient.get(key);
    return val ? JSON.parse(val) : null;
  } catch (err) {
    console.error("Redis get error:", err);
    return null;
  }
}

async function setEx(key, ttl, value) {
  try {
    await redisClient.setEx(key, ttl, JSON.stringify(value));
  } catch (err) {
    console.error("Redis setEx error:", err);
  }
}

async function del(key) {
  try {
    await redisClient.del(key);
  } catch (err) {
    console.error("Redis del error:", err);
  }
}

module.exports = {
  get,
  setEx,
  del,
  client: redisClient,
};
