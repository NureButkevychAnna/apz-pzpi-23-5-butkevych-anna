try {
  const Location = require("./Location");
  const redis = require("./redisClient");

  const CACHE_KEY = "all_locations";
  const CACHE_TTL = 60;

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: Location cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getAllLocations() {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getAllLocations:", e);
    }

    const rows = await Location.findAll();

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getAllLocations:", e);
    }

    return rows;
  }

  async function getLocationById(id) {
    return Location.findByPk(id);
  }

  async function addLocation(data) {
    const rec = await Location.create(data);
    await clearCache();
    return rec;
  }

  async function updateLocation(id, changes) {
    await Location.update(changes, { where: { id } });
    await clearCache();
    return Location.findByPk(id);
  }

  async function removeLocation(id) {
    const res = await Location.destroy({ where: { id } });
    await clearCache();
    return res;
  }

  console.log("DEBUG: locationModel initialized.");

  module.exports = {
    getAllLocations,
    getLocationById,
    addLocation,
    updateLocation,
    removeLocation,
  };
} catch (error) {
  console.error("FATAL ERROR IN locationModel INITIALIZATION:", error.message);
  process.exit(1);
}
