try {
  const Alert = require("./Alert");
  const redis = require("./redisClient");

  const CACHE_KEY = "recent_alerts";
  const CACHE_TTL = 30;

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: Alert cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getRecentAlerts(limit = 50) {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getRecentAlerts:", e);
    }

    const rows = await Alert.findAll({
      order: [["created_at", "desc"]],
      limit,
    });

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getRecentAlerts:", e);
    }

    return rows;
  }

  async function addAlert(data) {
    const rec = await Alert.create(data);
    await clearCache();
    return rec;
  }

  module.exports = {
    getRecentAlerts,
    addAlert,
  };

  console.log("DEBUG: alertModel initialized.");
} catch (error) {
  console.error("FATAL ERROR IN alertModel INITIALIZATION:", error.message);
  process.exit(1);
}
