try {
  const Device = require("./Device");
  const redis = require("./redisClient");

  const CACHE_KEY = "all_devices";
  const CACHE_TTL = 60;

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: Device cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getAllDevices() {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getAllDevices:", e);
    }

    const rows = await Device.findAll();

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getAllDevices:", e);
    }

    return rows;
  }

  async function getDeviceById(id) {
    return Device.findByPk(id);
  }

  async function addDevice(data) {
    const rec = await Device.create(data);
    await clearCache();
    return rec;
  }

  async function updateDevice(id, changes) {
    await Device.update(changes, { where: { id } });
    await clearCache();
    return Device.findByPk(id);
  }

  async function removeDevice(id) {
    const res = await Device.destroy({ where: { id } });
    await clearCache();
    return res;
  }

  console.log("DEBUG: deviceModel initialized.");

  module.exports = {
    getAllDevices,
    getDeviceById,
    addDevice,
    updateDevice,
    removeDevice,
  };
} catch (error) {
  console.error("FATAL ERROR IN deviceModel INITIALIZATION:", error.message);
  process.exit(1);
}
