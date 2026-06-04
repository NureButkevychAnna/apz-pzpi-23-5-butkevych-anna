try {
  const DeviceHealth = require("./DeviceHealth");
  const redis = require("./redisClient");

  const CACHE_KEY = "all_device_health";
  const CACHE_TTL = 60; // seconds

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: DeviceHealth cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getAllDeviceHealth() {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getAllDeviceHealth:", e);
    }

    const rows = await DeviceHealth.findAll();

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getAllDeviceHealth:", e);
    }

    return rows;
  }

  async function getByDeviceId(deviceId) {
    return DeviceHealth.findOne({ where: { device_id: deviceId } });
  }

  async function addDeviceHealth(data) {
    const rec = await DeviceHealth.create(data);
    await clearCache();
    return rec;
  }

  async function updateDeviceHealth(id, changes) {
    await DeviceHealth.update(changes, { where: { id } });
    await clearCache();
    return DeviceHealth.findByPk(id);
  }

  async function removeDeviceHealth(id) {
    const res = await DeviceHealth.destroy({ where: { id } });
    await clearCache();
    return res;
  }

  console.log("DEBUG: deviceHealthModel initialized.");

  module.exports = {
    getAllDeviceHealth,
    getByDeviceId,
    addDeviceHealth,
    updateDeviceHealth,
    removeDeviceHealth,
  };
} catch (error) {
  console.error(
    "FATAL ERROR IN deviceHealthModel INITIALIZATION:",
    error.message,
  );
  process.exit(1);
}
