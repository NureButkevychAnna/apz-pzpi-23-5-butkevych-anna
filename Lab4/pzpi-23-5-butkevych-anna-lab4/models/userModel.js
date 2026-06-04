try {
  const User = require("./User");
  const redis = require("./redisClient");

  const CACHE_KEY = "all_users";
  const CACHE_TTL = 60;

  async function clearCache() {
    try {
      await redis.del(CACHE_KEY);
      console.log("DEBUG: User cache cleared.");
    } catch (error) {
      console.error("Cache clear error:", error);
    }
  }

  async function getAllUsers() {
    try {
      const cached = await redis.get(CACHE_KEY);
      if (cached) return cached;
    } catch (e) {
      console.error("Redis get error in getAllUsers:", e);
    }

    const rows = await User.findAll({
      attributes: ["id", "name", "email", "role"],
      order: [["id", "asc"]],
    });

    try {
      await redis.setEx(CACHE_KEY, CACHE_TTL, rows);
    } catch (e) {
      console.error("Redis set error in getAllUsers:", e);
    }

    return rows;
  }

  async function addUser(data) {
    const rec = await User.create(data);
    await clearCache();
    return rec;
  }

  async function getUserByEmail(email) {
    return User.findOne({ where: { email } });
  }

  async function updateUser(id, changes) {
    await User.update(changes, { where: { id } });
    await clearCache();
    return User.findByPk(id);
  }

  async function removeUser(id) {
    const res = await User.destroy({ where: { id } });
    await clearCache();
    return res;
  }

  console.log("DEBUG: userModel initialized.");

  module.exports = {
    getAllUsers,
    addUser,
    getUserByEmail,
    updateUser,
    removeUser,
  };
} catch (error) {
  console.error("FATAL ERROR IN userModel INITIALIZATION:", error.message);
  process.exit(1);
}
