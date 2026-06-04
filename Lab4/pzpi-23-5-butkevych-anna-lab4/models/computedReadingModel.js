try {
  const ComputedReading = require("./ComputedReading");
  const redis = require("./redisClient");

  const TTL = 30;

  function keyForDevice(deviceId) {
    return `computed:device:${deviceId}`;
  }

  async function getComputedByDevice(deviceId) {
    const key = keyForDevice(deviceId);
    try {
      const cached = await redis.get(key);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getComputedByDevice:", e);
    }

    const rows = await ComputedReading.findAll({
      where: { device_id: deviceId },
      order: [["created_at", "desc"]],
    });

    try {
      await redis.setEx(key, TTL, rows);
    } catch (e) {
      console.error("Redis set error in getComputedByDevice:", e);
    }

    return rows;
  }

  async function addComputed(data) {
    const rec = await ComputedReading.create(data);
    try {
      await redis.del(keyForDevice(data.device_id));
    } catch (e) {
      console.error("Error clearing computed cache:", e);
    }
    return rec;
  }

  module.exports = {
    getComputedByDevice,
    addComputed,
  };

  console.log("DEBUG: computedReadingModel initialized.");
} catch (error) {
  console.error(
    "FATAL ERROR IN computedReadingModel INITIALIZATION:",
    error.message,
  );
  process.exit(1);
}
