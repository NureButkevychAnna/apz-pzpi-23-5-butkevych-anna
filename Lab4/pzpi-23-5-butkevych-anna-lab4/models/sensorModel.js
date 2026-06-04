try {
  const SensorReading = require("./SensorReading");
  const redis = require("./redisClient");

  const TTL = 30; // cache per-device readings for 30s

  function readingsKey(deviceId) {
    return `readings:device:${deviceId}`;
  }

  async function clearDeviceCache(deviceId) {
    try {
      await redis.del(readingsKey(deviceId));
      console.log(`DEBUG: Cleared sensor cache for device ${deviceId}`);
    } catch (err) {
      console.error("Redis del error in clearDeviceCache:", err);
    }
  }

  async function getReadingsByDevice(deviceId, limit = 100) {
    const key = readingsKey(deviceId);
    try {
      const cached = await redis.get(key);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getReadingsByDevice:", e);
    }

    const rows = await SensorReading.findAll({
      where: { device_id: deviceId },
      order: [["measured_at", "desc"]],
      limit,
    });

    try {
      await redis.setEx(key, TTL, rows);
    } catch (e) {
      console.error("Redis set error in getReadingsByDevice:", e);
    }

    return rows;
  }

  async function addReading(data) {
    const rec = await SensorReading.create(data);
    try {
      await clearDeviceCache(data.device_id);
    } catch (e) {
      console.error("Error clearing device cache after addReading:", e);
    }
    return rec;
  }

  module.exports = {
    getReadingsByDevice,
    addReading,
    clearDeviceCache,
  };

  console.log("DEBUG: sensorModel initialized.");
} catch (error) {
  console.error("FATAL ERROR IN sensorModel INITIALIZATION:", error.message);
  process.exit(1);
}
